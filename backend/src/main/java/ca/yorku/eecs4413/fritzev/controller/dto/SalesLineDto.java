package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// One vehicle line of the monthly sales report. 
public record SalesLineDto( 
        Long vehicleId, String title, int quantity, BigDecimal revenue) { 
} 
