package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// The whole cart with its total bill.
public record CartDto(
        Long cartId, List<CartLineDto> items, BigDecimal total) {
}
