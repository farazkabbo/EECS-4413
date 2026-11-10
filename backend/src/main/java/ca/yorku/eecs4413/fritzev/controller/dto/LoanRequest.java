package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonAlias;

// Body of POST /api/calculators/loan (UC-09).
public record LoanRequest(
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal price,
        @NotNull @DecimalMin("0") BigDecimal downPayment,
        @JsonAlias("annualRatePct")
        @NotNull @DecimalMin("0") @DecimalMax("30") BigDecimal annualRatePercent,
        @Min(1) @Max(120) int months) {
}
