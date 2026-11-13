package ca.yorku.eecs4413.fritzev.controller; 

import ca.yorku.eecs4413.fritzev.controller.dto.AddCartItemRequest; 
import ca.yorku.eecs4413.fritzev.controller.dto.CartDto; 
import ca.yorku.eecs4413.fritzev.controller.dto.UpdateQuantityRequest; 
import ca.yorku.eecs4413.fritzev.service.cart.CartService; 
import jakarta.servlet.http.HttpSession; 
import jakarta.validation.Valid; 
import jakarta.validation.constraints.Positive; 
import org.springframework.web.bind.annotation.*; 

// UC-12 Add to Cart, UC-13 Edit / Remove Cart Items. 
@RestController 
@RequestMapping("/api/cart") 
public class CartController { 

    private final CartService cartService; 

    public CartController(CartService cartService) { 
        this.cartService = cartService; 
    } 

    @GetMapping 
    public CartDto get(HttpSession session) { 
        return cartService.getCart(SessionAuth.currentUserId(session), session.getId()); 
    } 

    @PostMapping("/items") 
    public CartDto add(@Valid @RequestBody AddCartItemRequest request, HttpSession session) { 
        return cartService.addItem(SessionAuth.currentUserId(session), session.getId(), request); 
    } 

    @PatchMapping("/items/{itemId}") 
    public CartDto update(@PathVariable @Positive Long itemId, @Valid @RequestBody UpdateQuantityRequest request, 
                          HttpSession session) { 
        return cartService.updateQuantity(SessionAuth.currentUserId(session), session.getId(), itemId, 
                request.quantity()); 
    } 

    @DeleteMapping("/items/{itemId}") 
    public CartDto remove(@PathVariable @Positive Long itemId, HttpSession session) { 
        return cartService.removeItem(SessionAuth.currentUserId(session), session.getId(), itemId); 
    } 
} 
