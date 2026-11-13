package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// Body of POST /api/vehicles/{id}/reviews (UC-15). 
public record ReviewRequest( 
        @Min(1) @Max(5) int rating, 
        @Min(0) @Max(2000) Integer winterRangeKm, 
        @NotBlank @Size(max = 500) String winterRangeNotes, 
        @NotBlank @Size(max = 500) String homeChargingNotes, 
        @NotBlank @Size(max = 500) String softwareUpdateNotes, 
        @Min(1) @Max(5) int overallSatisfaction, 
        @Size(max = 1000) String comment) { 
} 
