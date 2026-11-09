package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// A hot deal
public record HotDealDto(
        Long id, String brand, String model, String trimName, int modelYear, VehicleCondition condition,
        int mileageKm, BigDecimal price, BigDecimal dealPrice, BigDecimal savings, int discountPercent,
        String imageUrl) {
}
