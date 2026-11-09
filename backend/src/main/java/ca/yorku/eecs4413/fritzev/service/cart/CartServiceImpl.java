package ca.yorku.eecs4413.fritzev.service.cart;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import org.springframework.stereotype.Service;

// STUB - M7 Cart Manager (D1 interface ICart).
@Service
public class CartServiceImpl implements CartService {

    private final CartDAO cartDAO;
    private final CartItemDAO cartItemDAO;
    private final VehicleDAO vehicleDAO;
    private final ca.yorku.eecs4413.fritzev.service.configurator.ConfiguratorService configuratorService;

    public CartServiceImpl(CartDAO cartDAO, CartItemDAO cartItemDAO, VehicleDAO vehicleDAO, ca.yorku.eecs4413.fritzev.service.configurator.ConfiguratorService configuratorService) {
        this.cartDAO = cartDAO;
        this.cartItemDAO = cartItemDAO;
        this.vehicleDAO = vehicleDAO;
        this.configuratorService = configuratorService;
    }

    @Override
    public CartDto getCart(Long userId, String sessionToken) {
        // TODO(Zaaid): find by userId first, else by sessionToken
        throw new NotImplementedYetException("UC-12 CartService.getCart is not implemented yet (owner: Zaaid, PB-24)");
    }

    @Override
    public CartDto addItem(Long userId, String sessionToken, AddCartItemRequest request) {
        // TODO(Zaaid): vehicle must be AVAILABLE and quantity <= vehicle.quantity, else ConflictException; price via configuratorService
        throw new NotImplementedYetException("UC-12 CartService.addItem is not implemented yet (owner: Zaaid, PB-24)");
    }

    @Override
    public CartDto updateQuantity(Long userId, String sessionToken, Long itemId, int quantity) {
        // TODO(Zaaid): item must belong to this cart
        throw new NotImplementedYetException("UC-13 CartService.updateQuantity is not implemented yet (owner: Zaaid, PB-24)");
    }

    @Override
    public CartDto removeItem(Long userId, String sessionToken, Long itemId) {
        // TODO(Zaaid): implement
        throw new NotImplementedYetException("UC-13 CartService.removeItem is not implemented yet (owner: Zaaid, PB-24)");
    }

    @Override
    public void clearCart(Long cartId) {
        // TODO(Zaaid): implement
        throw new NotImplementedYetException("UC-14 CartService.clearCart is not implemented yet (owner: Zaaid, PB-24)");
    }
}
