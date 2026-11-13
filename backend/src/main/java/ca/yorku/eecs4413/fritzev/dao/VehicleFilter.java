package ca.yorku.eecs4413.fritzev.dao; 

import ca.yorku.eecs4413.fritzev.domain.Shape; 
import ca.yorku.eecs4413.fritzev.domain.VehicleCondition; 

import java.math.BigDecimal; 

// Optional catalogue filter criteria (UC-05). 
public record VehicleFilter( 
        String brand, 
        Shape shape, 
        VehicleCondition condition, 
        Integer modelYear, 
        Integer minYear, 
        Integer maxYear, 
        BigDecimal minPrice, 
        BigDecimal maxPrice, 
        Boolean accidentFree) { 
} 
