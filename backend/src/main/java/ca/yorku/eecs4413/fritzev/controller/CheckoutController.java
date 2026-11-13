package ca.yorku.eecs4413.fritzev.controller; 

import ca.yorku.eecs4413.fritzev.controller.dto.AddressDto; 
import ca.yorku.eecs4413.fritzev.controller.dto.CheckoutSummaryDto; 
import ca.yorku.eecs4413.fritzev.controller.dto.OrderResultDto; 
import ca.yorku.eecs4413.fritzev.controller.dto.PlaceOrderRequest; 
import ca.yorku.eecs4413.fritzev.service.order.CheckoutService; 
import jakarta.servlet.http.HttpSession; 
import jakarta.validation.Valid; 
import jakarta.validation.constraints.NotBlank; 
import jakarta.validation.constraints.Size; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.*; 

// UC-14 Check Out. 
@RestController 
@RequestMapping("/api/checkout") 
public class CheckoutController { 

    private final CheckoutService checkoutService; 

    public CheckoutController(CheckoutService checkoutService) { 
        this.checkoutService = checkoutService; 
    } 

    @PostMapping("/start") 
    public CheckoutSummaryDto start(HttpSession session) { 
        Long userId = SessionAuth.requireUser(session); 
        return checkoutService.startCheckout(userId, session.getId()); 
    } 

    @PutMapping("/{checkoutId}/shipping") 
    @ResponseStatus(HttpStatus.NO_CONTENT) 
    public void shipping(@PathVariable @NotBlank @Size(max = 64) String checkoutId, 
                         @Valid @RequestBody AddressDto address, HttpSession session) { 
        checkoutService.confirmShipping(SessionAuth.requireUser(session), checkoutId, address); 
    } 

    // 200 with "Order Successfully Completed." or 402 with "Credit Card Authorization Failed." 
    @PostMapping("/{checkoutId}/place-order") 
    public ResponseEntity<OrderResultDto> placeOrder(@PathVariable @NotBlank @Size(max = 64) String checkoutId, 
                                                     @Valid @RequestBody PlaceOrderRequest request, 
                                                     HttpSession session) { 
        OrderResultDto result = checkoutService.placeOrder(SessionAuth.requireUser(session), checkoutId, request.card()); 
        return ResponseEntity.status(result.success() ? HttpStatus.OK : HttpStatus.PAYMENT_REQUIRED).body(result); 
    } 
} 
