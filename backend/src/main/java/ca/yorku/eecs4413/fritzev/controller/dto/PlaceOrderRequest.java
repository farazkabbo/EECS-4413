package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// Body of POST /api/checkout/{checkoutId}/place-order (UC-14). 
public record PlaceOrderRequest( 
        @NotNull @Valid CardInfo card) { 
} 
