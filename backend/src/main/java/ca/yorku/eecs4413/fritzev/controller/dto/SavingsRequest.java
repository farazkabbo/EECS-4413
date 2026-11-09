package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Body of POST /api/vehicles/{id}/savings-estimate (UC-20).
public record SavingsRequest(
        @NotNull @DecimalMin("1") @DecimalMax("200000") BigDecimal yearlyDistanceKm,
        @NotNull @DecimalMin("1") @DecimalMax("50") BigDecimal gasolineLitresPer100km,
        @NotNull @DecimalMin("0.01") @DecimalMax("10") BigDecimal gasolinePricePerLitre,
        @NotNull @DecimalMin("0.01") @DecimalMax("5") BigDecimal electricityPricePerKwh) {
}
