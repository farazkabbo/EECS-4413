package ca.yorku.eecs4413.fritzev.service.order; 

import ca.yorku.eecs4413.fritzev.controller.dto.*; 
import ca.yorku.eecs4413.fritzev.dao.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 
import ca.yorku.eecs4413.fritzev.exception.*; 

import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 

// M8 Order Manager (D1 interfaces ICheckout + IOrderQuery). 
public interface CheckoutService { 

    // UC-14 
    CheckoutSummaryDto startCheckout(Long userId, String sessionToken); 

    // UC-14 
    void confirmShipping(Long userId, String checkoutId, AddressDto address); 

    // UC-14 
    OrderResultDto placeOrder(Long userId, String checkoutId, CardInfo card); 

    // UC-17 
    List<Order> getOrdersByMonth(int year, int month); 
} 
