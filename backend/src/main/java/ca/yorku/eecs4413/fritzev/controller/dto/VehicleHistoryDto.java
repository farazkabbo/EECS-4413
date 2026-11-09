package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Vehicle history report shown on the details page of a used EV (UC-06).
public record VehicleHistoryDto(
        int accidentCount, boolean damageReported, int previousOwners, int serviceRecords,
        String reportSummary, LocalDate reportDate) {

    public static VehicleHistoryDto from(VehicleHistory h) {
        return h == null ? null : new VehicleHistoryDto(h.getAccidentCount(), h.isDamageReported(),
                h.getPreviousOwners(), h.getServiceRecords(), h.getReportSummary(), h.getReportDate());
    }
}
