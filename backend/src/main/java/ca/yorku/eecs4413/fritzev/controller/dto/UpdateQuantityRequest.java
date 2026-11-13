package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// Body of PATCH /api/cart/items/{itemId} (UC-13). 
public record UpdateQuantityRequest( 
        @Min(0) @Max(5) int quantity) { 
} 
