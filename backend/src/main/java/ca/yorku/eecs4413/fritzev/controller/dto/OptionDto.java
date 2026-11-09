package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// One customisation option (UC-08).
public record OptionDto(
        Long id, OptionCategory category, String name, String hexCode, BigDecimal priceDelta) {

    public static OptionDto from(CustomizationOption o) {
        return new OptionDto(o.getId(), o.getCategory(), o.getName(), o.getHexCode(), o.getPriceDelta());
    }
}
