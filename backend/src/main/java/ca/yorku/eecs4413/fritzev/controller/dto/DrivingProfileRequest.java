package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Body of POST /api/recommendations (UC-10).
public record DrivingProfileRequest(
        @Min(0) @Max(1000) int dailyCommuteKm,
        boolean homeCharging,
        @Min(1) @Max(9) Integer seatsNeeded,
        @DecimalMin("0") BigDecimal maxBudget,
        Shape preferredShape) {
}
