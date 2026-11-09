package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Body of POST /api/calculators/loan (UC-09).
public record LoanRequest(
        @NotNull @DecimalMin("0.01") @DecimalMax("10000000") BigDecimal price,
        @NotNull @DecimalMin("0") BigDecimal downPayment,
        @NotNull @DecimalMin("0") @DecimalMax("50") BigDecimal annualRatePct,
        @Min(1) @Max(120) int months) {
}
