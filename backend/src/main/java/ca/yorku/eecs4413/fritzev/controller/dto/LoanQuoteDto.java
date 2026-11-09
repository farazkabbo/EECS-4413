package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Monthly payment and total interest (UC-09).
public record LoanQuoteDto(
        BigDecimal principal, int months, BigDecimal monthlyPayment, BigDecimal totalInterest, BigDecimal totalPaid) {
}
