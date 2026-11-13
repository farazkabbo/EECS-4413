package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// Body of POST /api/configurations/price (UC-08). 
public record ConfigurationRequest( 
        @NotNull Long vehicleId, 
        @NotNull @Size(max = 10) List<@NotNull Long> optionIds) { 
} 
