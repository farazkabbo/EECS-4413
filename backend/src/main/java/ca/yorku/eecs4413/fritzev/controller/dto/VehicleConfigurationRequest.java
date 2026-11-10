package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record VehicleConfigurationRequest(
        @NotNull @Size(max = 10) List<@NotNull Long> optionIds) {
}
