package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// Full vehicle details (UC-06), including history, options grouped by category and rating. 
public record VehicleDetailDto( 
        Long id, String brand, String model, String trimName, int modelYear, Shape shape, 
        VehicleCondition condition, BigDecimal price, BigDecimal dealPrice, int mileageKm, int rangeKm, 
        BigDecimal batteryKwh, BigDecimal energyKwhPer100km, Integer batteryHealthPct, String drivetrain, 
        Integer seats, String description, String imageUrl, int quantity, VehicleStatus status, 
        VehicleHistoryDto history, Map<OptionCategory, List<OptionDto>> options, 
        Double averageRating, long reviewCount) { 
} 
