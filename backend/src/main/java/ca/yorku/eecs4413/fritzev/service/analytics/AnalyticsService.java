package ca.yorku.eecs4413.fritzev.service.analytics; 

import ca.yorku.eecs4413.fritzev.controller.dto.*; 
import ca.yorku.eecs4413.fritzev.dao.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 
import ca.yorku.eecs4413.fritzev.exception.*; 

import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 

// M14 Analytics Manager (D1 interfaces IEventTracking + IReports). 
public interface AnalyticsService { 

    // UC-18 
    void logEvent(String sessionId, Long userId, LogEventRequest request); 

    // UC-17 
    SalesReportDto getSalesReport(int year, int month); 

    // UC-18 
    UsageReportDto getUsageReport(LocalDate from, LocalDate to); 

    // UC-19 
    DashboardDto getDashboardSnapshot(); 
} 
