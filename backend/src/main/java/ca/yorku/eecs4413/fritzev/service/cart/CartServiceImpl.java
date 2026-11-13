package ca.yorku.eecs4413.fritzev.service.cart; 

import ca.yorku.eecs4413.fritzev.controller.dto.*; 
import ca.yorku.eecs4413.fritzev.dao.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 
import ca.yorku.eecs4413.fritzev.exception.*; 

import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 

import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; 

// Keeps anonymous and signed-in carts consistent while prices and stock change. 
@Service 
public class CartServiceImpl implements CartService { 

    private final CartDAO cartDAO; 
    private final CartItemDAO cartItemDAO; 
    private final VehicleDAO vehicleDAO; 
    private final UserDAO userDAO; 
    private final ca.yorku.eecs4413.fritzev.service.configurator.ConfiguratorService configuratorService; 

    public CartServiceImpl(CartDAO cartDAO, CartItemDAO cartItemDAO, VehicleDAO vehicleDAO, UserDAO userDAO, ca.yorku.eecs4413.fritzev.service.configurator.ConfiguratorService configuratorService) { 
        this.cartDAO = cartDAO; 
        this.cartItemDAO = cartItemDAO; 
        this.vehicleDAO = vehicleDAO; 
        this.userDAO = userDAO; 
        this.configuratorService = configuratorService; 
    } 

    @Override 
    @Transactional 
    public CartDto getCart(Long userId, String sessionToken) { 
        return toDto(resolveCart(userId, sessionToken)); 
    } 

    @Override 
    @Transactional 
    public CartDto addItem(Long userId, String sessionToken, AddCartItemRequest request) { 
        // The price is recalculated from the selected options; client-supplied totals are never trusted. 
        Cart cart = resolveCart(userId, sessionToken); 
        Vehicle vehicle = vehicleDAO.findDetailedById(request.vehicleId()) 
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found")); 
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE || request.quantity() > vehicle.getQuantity()) { 
            throw new ConflictException("Vehicle is not available in the requested quantity"); 
        } 
        ConfigurationPriceDto price = configuratorService.priceConfiguration( 
                new ConfigurationRequest(request.vehicleId(), request.optionIds() == null ? List.of() : request.optionIds())); 
        Set<Long> optionIds = new HashSet<>(request.optionIds() == null ? List.of() : request.optionIds()); 
        CartItem existing = cart.getItems().stream().filter(i -> i.getVehicle().getId().equals(vehicle.getId()) 
                && i.getOptions().stream().map(CustomizationOption::getId).collect(java.util.stream.Collectors.toSet()).equals(optionIds)).findFirst().orElse(null); 
        if (existing != null) { 
            if (existing.getQuantity() + request.quantity() > vehicle.getQuantity()) throw new ConflictException("Requested quantity exceeds stock"); 
            existing.setQuantity(existing.getQuantity() + request.quantity()); 
            existing.setUnitPrice(price.total()); 
        } else { 
            CartItem item = new CartItem(); 
            item.setCart(cart); 
            item.setVehicle(vehicle); 
            item.setQuantity(request.quantity()); 
            item.setUnitPrice(price.total()); 
            item.setOptions(vehicle.getOptions().stream().filter(o -> optionIds.contains(o.getId())).collect(java.util.stream.Collectors.toSet())); 
            cart.getItems().add(item); 
        } 
        cart.setUpdatedAt(LocalDateTime.now()); 
        return toDto(cartDAO.save(cart)); 
    } 

    @Override 
    @Transactional 
    public CartDto updateQuantity(Long userId, String sessionToken, Long itemId, int quantity) { 
        Cart cart = resolveCart(userId, sessionToken); 
        CartItem item = ownedItem(cart, itemId); 
        if (quantity <= 0) return removeItem(userId, sessionToken, itemId); 
        if (quantity > item.getVehicle().getQuantity()) throw new ConflictException("Requested quantity exceeds stock"); 
        item.setQuantity(quantity); 
        cart.setUpdatedAt(LocalDateTime.now()); 
        return toDto(cartDAO.save(cart)); 
    } 

    @Override 
    @Transactional 
    public CartDto removeItem(Long userId, String sessionToken, Long itemId) { 
        Cart cart = resolveCart(userId, sessionToken); 
        CartItem item = ownedItem(cart, itemId); 
        cart.getItems().remove(item); 
        cart.setUpdatedAt(LocalDateTime.now()); 
        return toDto(cartDAO.save(cart)); 
    } 

    @Override 
    @Transactional 
    public void clearCart(Long cartId) { 
        Cart cart = cartDAO.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found")); 
        cart.getItems().clear(); 
        cart.setUpdatedAt(LocalDateTime.now()); 
        cartDAO.save(cart); 
    } 

    @Override 
    @Transactional 
    public void mergeAnonymousCart(Long userId, String sessionToken) { 
        // Login combines matching configurations instead of creating duplicate cart lines. 
        Cart anonymous = cartDAO.findBySessionToken(sessionToken).orElse(null); 
        if (anonymous == null || anonymous.getItems().isEmpty()) return; 
        Cart userCart = resolveCart(userId, null); 
        for (CartItem source : new ArrayList<>(anonymous.getItems())) { 
            Set<Long> ids = source.getOptions().stream().map(CustomizationOption::getId).collect(java.util.stream.Collectors.toSet()); 
            CartItem target = userCart.getItems().stream().filter(i -> i.getVehicle().getId().equals(source.getVehicle().getId()) 
                    && i.getOptions().stream().map(CustomizationOption::getId).collect(java.util.stream.Collectors.toSet()).equals(ids)).findFirst().orElse(null); 
            if (target == null) { source.setCart(userCart); userCart.getItems().add(source); } 
            else target.setQuantity(Math.min(target.getVehicle().getQuantity(), target.getQuantity() + source.getQuantity())); 
        } 
        anonymous.getItems().clear(); 
        cartDAO.save(userCart); cartDAO.save(anonymous); 
    } 

    private Cart resolveCart(Long userId, String sessionToken) { 
        Cart cart = userId == null ? null : cartDAO.findByUserId(userId).orElse(null); 
        if (cart == null && sessionToken != null) cart = cartDAO.findBySessionToken(sessionToken).orElse(null); 
        if (cart != null) return cart; 
        cart = new Cart(); 
        if (userId != null) cart.setUser(userDAO.getReferenceById(userId)); else cart.setSessionToken(sessionToken); 
        return cartDAO.save(cart); 
    } 

    private static CartItem ownedItem(Cart cart, Long itemId) { 
        return cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst() 
                .orElseThrow(() -> new ForbiddenException("Cart item does not belong to this cart")); 
    } 

    private static CartDto toDto(Cart cart) { 
        List<CartLineDto> lines = cart.getItems().stream().map(i -> { 
            BigDecimal line = i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())); 
            String title = i.getVehicle().getBrand() + " " + i.getVehicle().getModel(); 
            List<String> options = i.getOptions().stream().sorted(Comparator.comparing(CustomizationOption::getId)).map(CustomizationOption::getName).toList(); 
            return new CartLineDto(i.getId(), i.getVehicle().getId(), title, options, i.getQuantity(), i.getUnitPrice(), line); 
        }).toList(); 
        BigDecimal total = lines.stream().map(CartLineDto::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add); 
        return new CartDto(cart.getId(), lines, total); 
    } 
} 
