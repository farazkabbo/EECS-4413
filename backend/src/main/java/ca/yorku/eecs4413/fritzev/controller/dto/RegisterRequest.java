package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Body of POST /api/auth/register (UC-01).
public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) @Pattern(regexp = "^[A-Za-z0-9_.-]+$", message = "may only contain letters, digits, _ . -") String username,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(min = 8, max = 72, message = "must be 8-72 characters") String password,
        @Size(max = 60) String firstName,
        @Size(max = 60) String lastName,
        @NotNull @Valid AddressDto billing,
        @NotNull @Valid AddressDto shipping) {
}
