package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// Real-time analytics snapshot (UC-19). 
public record DashboardDto( 
        Instant generatedAt, Map<String, Long> funnel, Map<String, Integer> batteryHealthByVehicle, 
        Map<String, Long> demandForecastByModel, Map<String, Long> browsingLast24h) { 
} 
