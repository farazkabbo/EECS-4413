package ca.yorku.eecs4413.fritzev.controller; 

import ca.yorku.eecs4413.fritzev.controller.dto.DashboardDto; 
import ca.yorku.eecs4413.fritzev.controller.dto.LogEventRequest; 
import ca.yorku.eecs4413.fritzev.controller.dto.SalesReportDto; 
import ca.yorku.eecs4413.fritzev.controller.dto.UsageReportDto; 
import ca.yorku.eecs4413.fritzev.service.analytics.AnalyticsService; 
import jakarta.servlet.http.HttpSession; 
import jakarta.validation.Valid; 
import jakarta.validation.constraints.Max; 
import jakarta.validation.constraints.Min; 
import org.springframework.format.annotation.DateTimeFormat; 
import org.springframework.http.HttpStatus; 
import org.springframework.web.bind.annotation.*; 

import java.time.LocalDate; 

// UC-17 Sales Reports, UC-18 Usage Reports, UC-19 Real-Time Dashboard (ADMIN only), plus public event tracking. 
@RestController 
@RequestMapping("/api") 
public class AnalyticsController { 

    private final AnalyticsService analyticsService; 

    public AnalyticsController(AnalyticsService analyticsService) { 
        this.analyticsService = analyticsService; 
    } 

    @PostMapping("/events") 
    @ResponseStatus(HttpStatus.ACCEPTED) 
    public void logEvent(@Valid @RequestBody LogEventRequest request, HttpSession session) { 
        analyticsService.logEvent(session.getId(), SessionAuth.currentUserId(session), request); 
    } 

    @GetMapping("/admin/reports/sales") 
    public SalesReportDto sales(@RequestParam @Min(2000) @Max(2100) int year, 
                                @RequestParam @Min(1) @Max(12) int month, 
                                HttpSession session) { 
        SessionAuth.requireAdmin(session); 
        return analyticsService.getSalesReport(year, month); 
    } 

    @GetMapping("/admin/reports/usage") 
    public UsageReportDto usage(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, 
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to, 
                                HttpSession session) { 
        SessionAuth.requireAdmin(session); 
        return analyticsService.getUsageReport(from, to); 
    } 

    @GetMapping("/admin/dashboard") 
    public DashboardDto dashboard(HttpSession session) { 
        SessionAuth.requireAdmin(session); 
        return analyticsService.getDashboardSnapshot(); 
    } 
} 
