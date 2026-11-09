package ca.yorku.eecs4413.fritzev.service.cart;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

// M7 Cart Manager (D1 interface ICart).
public interface CartService {

    // UC-12
    CartDto getCart(Long userId, String sessionToken);

    // UC-12
    CartDto addItem(Long userId, String sessionToken, AddCartItemRequest request);

    // UC-13
    CartDto updateQuantity(Long userId, String sessionToken, Long itemId, int quantity);

    // UC-13
    CartDto removeItem(Long userId, String sessionToken, Long itemId);

    // UC-14
    void clearCart(Long cartId);
}
