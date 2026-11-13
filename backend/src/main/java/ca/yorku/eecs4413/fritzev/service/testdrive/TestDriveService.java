package ca.yorku.eecs4413.fritzev.service.testdrive; 

import ca.yorku.eecs4413.fritzev.controller.dto.*; 
import ca.yorku.eecs4413.fritzev.dao.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 
import ca.yorku.eecs4413.fritzev.exception.*; 

import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 

// M11 Test Drive Scheduler (D1 interface ITestDrive). 
public interface TestDriveService { 

    // UC-11 
    List<SlotDto> getAvailableSlots(Long vehicleId, DriveType type, LocalDate date); 

    // UC-11 
    BookingDto book(Long userId, BookTestDriveRequest request); 

    // UC-11 
    void cancel(Long userId, Long bookingId); 

    // UC-11 
    List<BookingDto> myBookings(Long userId); 
} 
