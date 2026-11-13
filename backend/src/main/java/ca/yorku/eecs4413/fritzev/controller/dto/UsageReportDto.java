package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// Website usage aggregated from visit events (UC-18). 
public record UsageReportDto( 
        LocalDate from, LocalDate to, long views, long cartAdditions, long purchases, 
        Map<String, Long> viewsByVehicle) { 
} 
