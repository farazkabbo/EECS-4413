package ca.yorku.eecs4413.fritzev.service.testdrive;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import org.springframework.stereotype.Service;

// STUB - M11 Test Drive Scheduler (D1 interface ITestDrive).
@Service
public class TestDriveServiceImpl implements TestDriveService {

    private final TestDriveSlotDAO slotDAO;
    private final TestDriveBookingDAO bookingDAO;
    private final UserDAO userDAO;

    public TestDriveServiceImpl(TestDriveSlotDAO slotDAO, TestDriveBookingDAO bookingDAO, UserDAO userDAO) {
        this.slotDAO = slotDAO;
        this.bookingDAO = bookingDAO;
        this.userDAO = userDAO;
    }

    @Override
    public List<SlotDto> getAvailableSlots(Long vehicleId, DriveType type, LocalDate date) {
        // TODO(Ronak): slotDAO.findFreeSlots(vehicleId, type, date.atStartOfDay(), date.plusDays(1).atStartOfDay())
        throw new NotImplementedYetException("UC-11 TestDriveService.getAvailableSlots is not implemented yet (owner: Ronak, PB-28)");
    }

    @Override
    public BookingDto book(Long userId, BookTestDriveRequest request) {
        // TODO(Ronak): already booked -> ConflictException; AT_HOME without address -> BadRequestException; past slot -> BadRequestException
        throw new NotImplementedYetException("UC-11 TestDriveService.book is not implemented yet (owner: Ronak, PB-28)");
    }

    @Override
    public void cancel(Long userId, Long bookingId) {
        // TODO(Ronak): delete the booking row; other users -> ForbiddenException
        throw new NotImplementedYetException("UC-11 TestDriveService.cancel is not implemented yet (owner: Ronak, PB-28)");
    }

    @Override
    public List<BookingDto> myBookings(Long userId) {
        // TODO(Ronak): implement
        throw new NotImplementedYetException("UC-11 TestDriveService.myBookings is not implemented yet (owner: Ronak, PB-28)");
    }
}
