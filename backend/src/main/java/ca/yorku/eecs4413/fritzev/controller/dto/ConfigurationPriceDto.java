package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// Base price plus chosen options (UC-08). 
public record ConfigurationPriceDto( 
        Long vehicleId, BigDecimal basePrice, List<OptionDto> options, BigDecimal optionsTotal, BigDecimal total) { 
} 
