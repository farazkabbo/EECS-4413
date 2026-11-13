package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// Body of POST /api/events (view / cart / purchase tracking). 
public record LogEventRequest( 
        Long vehicleId, 
        @NotNull EventType eventType) { 
} 
