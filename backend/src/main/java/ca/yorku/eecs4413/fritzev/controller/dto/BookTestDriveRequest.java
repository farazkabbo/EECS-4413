package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Body of POST /api/test-drives (UC-11).
public record BookTestDriveRequest(
        @NotNull Long slotId,
        @Size(max = 200) String address) {
}
