package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// Row of the catalogue list (UC-04). 
public record VehicleSummaryDto( 
        Long id, String brand, String model, String trimName, int modelYear, Shape shape, 
        VehicleCondition condition, BigDecimal price, BigDecimal dealPrice, int mileageKm, 
        int rangeKm, String imageUrl, VehicleStatus status) { 

    public static VehicleSummaryDto from(Vehicle v) { 
        return new VehicleSummaryDto(v.getId(), v.getBrand(), v.getModel(), v.getTrimName(), v.getModelYear(), 
                v.getShape(), v.getVehicleCondition(), v.getPrice(), v.getDealPrice(), v.getMileageKm(), 
                v.getRangeKm(), v.getImageUrl(), v.getStatus()); 
    } 
} 
