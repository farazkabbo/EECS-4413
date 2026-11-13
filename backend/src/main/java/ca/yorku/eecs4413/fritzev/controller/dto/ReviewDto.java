package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// A published review (UC-15). 
public record ReviewDto( 
        Long id, Long vehicleId, String author, int rating, Integer winterRangeKm, String winterRangeNotes, 
        String homeChargingNotes, String softwareUpdateNotes, int overallSatisfaction, String comment, 
        LocalDateTime createdAt) { 
} 
