package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// "Order Successfully Completed." or "Credit Card Authorization Failed."
public record OrderResultDto(
        boolean success, String message, Long orderId) {
}
