package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Body of POST /api/cart/items (UC-12).
public record AddCartItemRequest(
        @NotNull Long vehicleId,
        @Size(max = 10) List<@NotNull Long> optionIds,
        @Min(1) @Max(5) int quantity) {
}
