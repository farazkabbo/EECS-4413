package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Credit-card details typed for ONE purchase.
public record CardInfo(
        @NotBlank @Size(max = 80) String holderName,
        @NotBlank @Pattern(regexp = "^[0-9]{13,19}$", message = "must be 13-19 digits") String cardNumber,
        @NotBlank @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "must be MM/YY") String expiry,
        @NotBlank @Pattern(regexp = "^[0-9]{3,4}$", message = "must be 3-4 digits") String cvv) {

    // Masks the number so an accidental log line can never leak it.
    @Override
    public String toString() {
        return "CardInfo[holderName=" + holderName + ", cardNumber=****" + (cardNumber == null || cardNumber.length() < 4
                ? "" : cardNumber.substring(cardNumber.length() - 4)) + "]";
    }
}
