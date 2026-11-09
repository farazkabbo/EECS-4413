package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Yearly fuel-cost savings and CO2 avoided (UC-20).
public record SavingsReportDto(
        Long vehicleId, BigDecimal yearlyGasolineCost, BigDecimal yearlyChargingCost,
        BigDecimal yearlySavings, BigDecimal co2AvoidedKg, List<String> assumptions) {
}
