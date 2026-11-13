package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// A free test-drive slot (UC-11). 
public record SlotDto( 
        Long id, Long vehicleId, DriveType driveType, LocalDateTime start, int minutes) { 
} 
