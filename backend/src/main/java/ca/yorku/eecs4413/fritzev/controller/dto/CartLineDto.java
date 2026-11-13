package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// One line of the cart. 
public record CartLineDto( 
        Long itemId, Long vehicleId, String title, List<String> options, int quantity, 
        BigDecimal unitPrice, BigDecimal lineTotal) { 
} 
