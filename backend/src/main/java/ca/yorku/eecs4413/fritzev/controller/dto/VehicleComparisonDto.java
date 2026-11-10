package ca.yorku.eecs4413.fritzev.controller.dto;

import ca.yorku.eecs4413.fritzev.domain.Vehicle;

import java.math.BigDecimal;

public record VehicleComparisonDto(
        Long id,
        String title,
        BigDecimal price,
        int year,
        int rangeKm,
        BigDecimal batteryKwh,
        java.math.BigDecimal energyKwhPer100km,
        int mileageKm,
        Double averageRating) {

    public static VehicleComparisonDto from(Vehicle vehicle, Double rating) {
        return new VehicleComparisonDto(vehicle.getId(),
                vehicle.getModelYear() + " " + vehicle.getBrand() + " " + vehicle.getModel(),
                vehicle.getDealPrice() == null ? vehicle.getPrice() : vehicle.getDealPrice(),
                vehicle.getModelYear(), vehicle.getRangeKm(), vehicle.getBatteryKwh(),
                vehicle.getEnergyKwhPer100km(), vehicle.getMileageKm(), rating);
    }
}
