package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// Vehicles sold in one month (UC-17). 
public record SalesReportDto( 
        int year, int month, int vehiclesSold, BigDecimal revenue, List<SalesLineDto> lines) { 
} 
