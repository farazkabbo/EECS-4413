package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Billing or shipping address, used in requests and responses.
public record AddressDto(
        @NotBlank @Size(max = 120) String street,
        @NotBlank @Size(max = 60) String city,
        @NotBlank @Size(max = 40) String province,
        @Size(max = 40) String country,
        @NotBlank @Pattern(regexp = "^[A-Za-z0-9 -]{3,12}$", message = "is not a valid postal code") String postalCode,
        @Pattern(regexp = "^[0-9+() -]{7,20}$", message = "is not a valid phone number") String phone) {
}
