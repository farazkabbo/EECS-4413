package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonAlias;

// Body of POST /api/vehicles/{id}/savings (UC-20).
public record SavingsRequest(
        @JsonAlias("yearlyDistanceKm")
        @NotNull @DecimalMin(value = "0", inclusive = false) @DecimalMax("200000") BigDecimal annualKm,
        @JsonAlias("gasolineLitresPer100km")
        @NotNull @DecimalMin(value = "0", inclusive = false) @DecimalMax("30") BigDecimal gasolineLPer100km,
        @JsonAlias("gasolinePricePerLitre")
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal gasolinePricePerL,
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal electricityPricePerKwh) {
}
