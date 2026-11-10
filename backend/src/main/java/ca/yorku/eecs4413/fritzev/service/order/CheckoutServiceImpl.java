package ca.yorku.eecs4413.fritzev.service.order;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;
import ca.yorku.eecs4413.fritzev.integration.payment.PaymentResult;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Coordinates the checkout steps: recheck stock, authorize payment, then create the order.
@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final OrderDAO orderDAO;
    private final VehicleDAO vehicleDAO;
    private final ca.yorku.eecs4413.fritzev.service.cart.CartService cartService;
    private final ca.yorku.eecs4413.fritzev.integration.payment.PaymentGateway paymentGateway;
    private final ca.yorku.eecs4413.fritzev.dao.CartDAO cartDAO;
    private final ca.yorku.eecs4413.fritzev.dao.UserDAO userDAO;
    private final ca.yorku.eecs4413.fritzev.service.account.AccountService accountService;
    private final ca.yorku.eecs4413.fritzev.dao.VisitEventDAO visitEventDAO;
    private final Map<String, CheckoutState> checkouts = new java.util.concurrent.ConcurrentHashMap<>();

    public CheckoutServiceImpl(OrderDAO orderDAO, VehicleDAO vehicleDAO, ca.yorku.eecs4413.fritzev.service.cart.CartService cartService,
                               ca.yorku.eecs4413.fritzev.integration.payment.PaymentGateway paymentGateway,
                               ca.yorku.eecs4413.fritzev.dao.CartDAO cartDAO, ca.yorku.eecs4413.fritzev.dao.UserDAO userDAO,
                               ca.yorku.eecs4413.fritzev.service.account.AccountService accountService,
                               ca.yorku.eecs4413.fritzev.dao.VisitEventDAO visitEventDAO) {
        this.orderDAO = orderDAO;
        this.vehicleDAO = vehicleDAO;
        this.cartService = cartService;
        this.paymentGateway = paymentGateway;
        this.cartDAO = cartDAO;
        this.userDAO = userDAO;
        this.accountService = accountService;
        this.visitEventDAO = visitEventDAO;
    }

    @Override
    @Transactional
    public CheckoutSummaryDto startCheckout(Long userId, String sessionToken) {
        // Checkout IDs are bound to the authenticated user before shipping or payment is accepted.
        CartDto cart = cartService.getCart(userId, sessionToken);
        if (cart.items().isEmpty()) throw new BadRequestException("Cart is empty");
        String checkoutId = UUID.randomUUID().toString();
        checkouts.put(checkoutId, new CheckoutState(userId, cart.cartId(), null, false));
        AddressDto shipping = null;
        try { shipping = accountService.getDefaultShipping(userId); } catch (ResourceNotFoundException ignored) { }
        return new CheckoutSummaryDto(checkoutId, cart, shipping);
    }

    @Override
    public void confirmShipping(Long userId, String checkoutId, AddressDto address) {
        CheckoutState state = owned(checkoutId, userId);
        checkouts.put(checkoutId, new CheckoutState(state.userId(), state.cartId(), address, state.placed()));
    }

    @Override
    @Transactional
    public synchronized OrderResultDto placeOrder(Long userId, String checkoutId, CardInfo card) {
        // Rechecking stock immediately before payment prevents an old cart view from overselling inventory.
        CheckoutState state = owned(checkoutId, userId);
        if (state.placed()) throw new ConflictException("Checkout has already been completed");
        if (state.address() == null) throw new BadRequestException("Shipping address is required");
        Cart cart = cartDAO.findById(state.cartId()).orElseThrow(() -> new BadRequestException("Cart is empty"));
        if (cart.getItems().isEmpty()) throw new BadRequestException("Cart is empty");
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            Vehicle vehicle = vehicleDAO.findById(item.getVehicle().getId()).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
            if (vehicle.getStatus() != VehicleStatus.AVAILABLE || item.getQuantity() > vehicle.getQuantity()) {
                throw new ConflictException("Vehicle is no longer available in the requested quantity");
            }
            total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        PaymentResult payment = paymentGateway.authorize(card, total);
        // A declined authorization returns before changing inventory, orders, or the cart.
        if (!payment.approved()) return new OrderResultDto(false, "Credit Card Authorization Failed.", null);
        Order order = new Order();
        order.setUser(userDAO.getReferenceById(userId));
        order.setStatus(OrderStatus.COMPLETED);
        order.setTotal(total);
        order.setShipStreet(state.address().street()); order.setShipCity(state.address().city());
        order.setShipProvince(state.address().province()); order.setShipCountry(state.address().country() == null ? "Canada" : state.address().country());
        order.setShipPostalCode(state.address().postalCode());
        for (CartItem item : cart.getItems()) {
            Vehicle vehicle = vehicleDAO.findById(item.getVehicle().getId()).orElseThrow();
            OrderItem line = new OrderItem(); line.setOrder(order); line.setVehicle(vehicle); line.setQuantity(item.getQuantity()); line.setUnitPrice(item.getUnitPrice());
            line.setOptionsSummary(item.getOptions().stream().sorted(Comparator.comparing(CustomizationOption::getId)).map(CustomizationOption::getName).reduce((a,b) -> a + " / " + b).orElse(null));
            order.getItems().add(line);
            vehicle.setQuantity(vehicle.getQuantity() - item.getQuantity());
            if (vehicle.getQuantity() == 0) vehicle.setStatus(VehicleStatus.SOLD);
            vehicleDAO.save(vehicle);
            VisitEvent event = new VisitEvent(); event.setUserId(userId); event.setVehicleId(vehicle.getId()); event.setEventType(EventType.PURCHASE); visitEventDAO.save(event);
        }
        order = orderDAO.save(order);
        cartService.clearCart(cart.getId());
        checkouts.put(checkoutId, new CheckoutState(state.userId(), state.cartId(), state.address(), true));
        return new OrderResultDto(true, "Order Successfully Completed.", order.getId());
    }

    @Override
    public List<Order> getOrdersByMonth(int year, int month) {
        LocalDateTime from = LocalDate.of(year, month, 1).atStartOfDay();
        return orderDAO.findByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(OrderStatus.COMPLETED, from, from.plusMonths(1));
    }

    private CheckoutState owned(String checkoutId, Long userId) {
        CheckoutState state = checkouts.get(checkoutId);
        if (state == null) throw new ResourceNotFoundException("Checkout not found");
        if (!state.userId().equals(userId)) throw new ForbiddenException("Checkout does not belong to this user");
        return state;
    }

    private record CheckoutState(Long userId, Long cartId, AddressDto address, boolean placed) { }
}
