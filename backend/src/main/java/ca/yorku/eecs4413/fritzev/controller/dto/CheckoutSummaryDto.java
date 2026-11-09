package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Returned when checkout starts
public record CheckoutSummaryDto(
        String checkoutId, CartDto cart, AddressDto defaultShipping) {
}
