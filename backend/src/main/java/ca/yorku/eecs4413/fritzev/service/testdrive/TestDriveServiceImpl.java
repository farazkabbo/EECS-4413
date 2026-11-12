package ca.yorku.eecs4413.fritzev.service.testdrive;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;

import java.time.*;
import java.util.*;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// M11 Test Drive Scheduler. Double booking is blocked twice: an explicit check here,
// and UNIQUE(slot_id) on test_drive_bookings in schema.sql as the database-level guard.
@Service
@Transactional(readOnly = true)
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
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        return slotDAO.findFreeSlots(vehicleId, type, from, from.plusDays(1)).stream()
                .filter(s -> s.getSlotStart().isAfter(now))
                .map(s -> new SlotDto(s.getId(), s.getVehicle().getId(), s.getDriveType(),
                        s.getSlotStart(), s.getSlotMinutes()))
                .toList();
    }

    @Override
    @Transactional
    public BookingDto book(Long userId, BookTestDriveRequest request) {
        TestDriveSlot slot = slotDAO.findById(request.slotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot " + request.slotId() + " not found"));
        if (!slot.getSlotStart().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("This slot is in the past");
        }
        boolean atHome = slot.getDriveType() == DriveType.AT_HOME;
        String address = request.address() == null ? "" : request.address().trim();
        if (atHome && address.isEmpty()) {
            throw new BadRequestException("An address is required for at-home test drives");
        }
        if (bookingDAO.existsBySlotId(slot.getId())) {
            throw new ConflictException("This slot has already been booked");
        }
        User user = userDAO.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        TestDriveBooking booking = new TestDriveBooking();
        booking.setSlot(slot);
        booking.setUser(user);
        booking.setAddress(atHome ? address : null);
        try {
            booking = bookingDAO.saveAndFlush(booking);   // two simultaneous requests: the DB rejects the loser
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("This slot has already been booked");
        }
        return toDto(booking);
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long bookingId) {
        TestDriveBooking booking = bookingDAO.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking " + bookingId + " not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new ForbiddenException("This booking belongs to another user");
        }
        bookingDAO.delete(booking);   // frees the slot again
    }

    @Override
    public List<BookingDto> myBookings(Long userId) {
        return bookingDAO.findByUserId(userId).stream()
                .sorted(Comparator.comparing(b -> b.getSlot().getSlotStart()))
                .map(TestDriveServiceImpl::toDto)
                .toList();
    }

    private static BookingDto toDto(TestDriveBooking b) {
        TestDriveSlot s = b.getSlot();
        return new BookingDto(b.getId(), s.getId(), s.getVehicle().getId(), s.getDriveType(),
                s.getSlotStart(), b.getAddress(), b.getStatus());
    }
}