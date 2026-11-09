package ca.yorku.eecs4413.fritzev.controller;

import ca.yorku.eecs4413.fritzev.controller.dto.BookTestDriveRequest;
import ca.yorku.eecs4413.fritzev.controller.dto.BookingDto;
import ca.yorku.eecs4413.fritzev.controller.dto.SlotDto;
import ca.yorku.eecs4413.fritzev.domain.DriveType;
import ca.yorku.eecs4413.fritzev.service.testdrive.TestDriveService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// UC-11 Schedule Test Drive.
@RestController
@RequestMapping("/api")
public class TestDriveController {

    private final TestDriveService testDriveService;

    public TestDriveController(TestDriveService testDriveService) {
        this.testDriveService = testDriveService;
    }

    @GetMapping("/vehicles/{id}/test-drive-slots")
    public List<SlotDto> slots(@PathVariable @Positive Long id,
                               @RequestParam DriveType type,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return testDriveService.getAvailableSlots(id, type, date);
    }

    @PostMapping("/test-drives")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto book(@Valid @RequestBody BookTestDriveRequest request, HttpSession session) {
        return testDriveService.book(SessionAuth.requireUser(session), request);
    }

    @GetMapping("/test-drives/mine")
    public List<BookingDto> mine(HttpSession session) {
        return testDriveService.myBookings(SessionAuth.requireUser(session));
    }

    @DeleteMapping("/test-drives/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable @Positive Long bookingId, HttpSession session) {
        testDriveService.cancel(SessionAuth.requireUser(session), bookingId);
    }
}
