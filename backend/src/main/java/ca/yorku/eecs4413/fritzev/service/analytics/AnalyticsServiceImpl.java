package ca.yorku.eecs4413.fritzev.service.analytics;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import org.springframework.stereotype.Service;

// STUB - M14 Analytics Manager (D1 interfaces IEventTracking + IReports).
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final VisitEventDAO eventDAO;
    private final OrderDAO orderDAO;
    private final VehicleDAO vehicleDAO;

    public AnalyticsServiceImpl(VisitEventDAO eventDAO, OrderDAO orderDAO, VehicleDAO vehicleDAO) {
        this.eventDAO = eventDAO;
        this.orderDAO = orderDAO;
        this.vehicleDAO = vehicleDAO;
    }

    @Override
    public void logEvent(String sessionId, Long userId, LogEventRequest request) {
        // TODO(Ronak): eventDAO.save(...)
        throw new NotImplementedYetException("UC-18 AnalyticsService.logEvent is not implemented yet (owner: Ronak, PB-29 / PB-30)");
    }

    @Override
    public SalesReportDto getSalesReport(int year, int month) {
        // TODO(Ronak): use CheckoutService.getOrdersByMonth or orderDAO directly
        throw new NotImplementedYetException("UC-17 AnalyticsService.getSalesReport is not implemented yet (owner: Ronak, PB-29 / PB-30)");
    }

    @Override
    public UsageReportDto getUsageReport(LocalDate from, LocalDate to) {
        // TODO(Ronak): from after to -> BadRequestException
        throw new NotImplementedYetException("UC-18 AnalyticsService.getUsageReport is not implemented yet (owner: Ronak, PB-29 / PB-30)");
    }

    @Override
    public DashboardDto getDashboardSnapshot() {
        // TODO(Ronak): implement
        throw new NotImplementedYetException("UC-19 AnalyticsService.getDashboardSnapshot is not implemented yet (owner: Ronak, PB-29 / PB-30)");
    }
}
