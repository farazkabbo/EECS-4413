package ca.yorku.eecs4413.fritzev.service.order;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import org.springframework.stereotype.Service;

// STUB - M8 Order Manager (D1 interfaces ICheckout + IOrderQuery).
@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final OrderDAO orderDAO;
    private final VehicleDAO vehicleDAO;
    private final ca.yorku.eecs4413.fritzev.service.cart.CartService cartService;
    private final ca.yorku.eecs4413.fritzev.integration.payment.PaymentGateway paymentGateway;

    public CheckoutServiceImpl(OrderDAO orderDAO, VehicleDAO vehicleDAO, ca.yorku.eecs4413.fritzev.service.cart.CartService cartService, ca.yorku.eecs4413.fritzev.integration.payment.PaymentGateway paymentGateway) {
        this.orderDAO = orderDAO;
        this.vehicleDAO = vehicleDAO;
        this.cartService = cartService;
        this.paymentGateway = paymentGateway;
    }

    @Override
    public CheckoutSummaryDto startCheckout(Long userId, String sessionToken) {
        // TODO(Zaaid): empty cart -> BadRequestException; keep the checkout state (e.g. a Map<checkoutId, ...> or a PENDING order)
        throw new NotImplementedYetException("UC-14 CheckoutService.startCheckout is not implemented yet (owner: Zaaid, PB-25)");
    }

    @Override
    public void confirmShipping(Long userId, String checkoutId, AddressDto address) {
        // TODO(Zaaid): implement
        throw new NotImplementedYetException("UC-14 CheckoutService.confirmShipping is not implemented yet (owner: Zaaid, PB-25)");
    }

    @Override
    public OrderResultDto placeOrder(Long userId, String checkoutId, CardInfo card) {
        // TODO(Zaaid): messages must be EXACTLY "Order Successfully Completed." / "Credit Card Authorization Failed."; on failure leave cart + inventory unchanged
        throw new NotImplementedYetException("UC-14 CheckoutService.placeOrder is not implemented yet (owner: Zaaid, PB-25)");
    }

    @Override
    public List<Order> getOrdersByMonth(int year, int month) {
        // TODO(Zaaid): orderDAO.findByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(COMPLETED, first day, first day of next month)
        throw new NotImplementedYetException("UC-17 CheckoutService.getOrdersByMonth is not implemented yet (owner: Zaaid, PB-25)");
    }
}
