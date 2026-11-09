package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Body of POST /api/auth/login (UC-02).
public record LoginRequest(
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Size(max = 72) String password) {
}
