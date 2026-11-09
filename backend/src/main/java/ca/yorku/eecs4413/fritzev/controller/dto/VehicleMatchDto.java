package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// A recommended vehicle with its match score (UC-10).
public record VehicleMatchDto(
        VehicleSummaryDto vehicle, int score, String reason) {
}
