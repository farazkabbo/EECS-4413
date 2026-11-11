package ca.yorku.eecs4413.fritzev.service.analytics;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// M14 Analytics Manager (event tracking + admin reports + dashboard).
@Service
@Transactional(readOnly = true)
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
    @Transactional
    public void logEvent(String sessionId, Long userId, LogEventRequest request) {
        if (request.vehicleId() != null && !vehicleDAO.existsById(request.vehicleId())) {
            throw new ResourceNotFoundException("Vehicle " + request.vehicleId() + " not found");
        }
        VisitEvent e = new VisitEvent();
        e.setSessionId(sessionId);
        e.setUserId(userId);
        e.setVehicleId(request.vehicleId());
        e.setEventType(request.eventType());
        eventDAO.save(e);
    }

    @Override
    public SalesReportDto getSalesReport(int year, int month) {
        LocalDateTime from = LocalDate.of(year, month, 1).atStartOfDay();
        List<Order> orders = orderDAO.findByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                OrderStatus.COMPLETED, from, from.plusMonths(1));
        Map<Long, SalesLineDto> byVehicle = new LinkedHashMap<>();
        int sold = 0;
        BigDecimal revenue = BigDecimal.ZERO;
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                Vehicle v = item.getVehicle();
                BigDecimal line = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                byVehicle.merge(v.getId(), new SalesLineDto(v.getId(), title(v), item.getQuantity(), line),
                        (a, b) -> new SalesLineDto(a.vehicleId(), a.title(), a.quantity() + b.quantity(),
                                a.revenue().add(b.revenue())));
                sold += item.getQuantity();
                revenue = revenue.add(line);
            }
        }
        return new SalesReportDto(year, month, sold, revenue, new ArrayList<>(byVehicle.values()));
    }

    @Override
    public UsageReportDto getUsageReport(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) throw new BadRequestException("from must not be after to");
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();   // 'to' is inclusive
        List<VisitEvent> events = eventDAO.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(start, end);
        return new UsageReportDto(from, to,
                count(events, EventType.VIEW), count(events, EventType.CART), count(events, EventType.PURCHASE),
                viewsByVehicle(events));
    }

    @Override
    public DashboardDto getDashboardSnapshot() {
        LocalDateTime now = LocalDateTime.now();
        List<VisitEvent> last30 = eventDAO.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                now.minusDays(30), now.plusMinutes(1));
        List<VisitEvent> last24h = eventDAO.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                now.minusHours(24), now.plusMinutes(1));

        Map<String, Long> funnel = new LinkedHashMap<>();
        for (EventType t : EventType.values()) funnel.put(t.name(), count(last30, t));

        Map<String, Integer> battery = new LinkedHashMap<>();
        vehicleDAO.findAll().stream()
                .filter(v -> v.getBatteryHealthPct() != null)
                .sorted(Comparator.comparing(Vehicle::getId))
                .forEach(v -> battery.put(title(v), v.getBatteryHealthPct()));

        // Simple demand heuristic: view = 1 point, cart = 2, purchase = 3, summed per model over 30 days.
        Map<Long, Vehicle> vehicles = vehiclesById(last30);
        Map<String, Long> demand = new TreeMap<>();
        for (VisitEvent e : last30) {
            Vehicle v = e.getVehicleId() == null ? null : vehicles.get(e.getVehicleId());
            if (v == null) continue;
            long points = switch (e.getEventType()) { case VIEW -> 1; case CART -> 2; case PURCHASE -> 3; };
            demand.merge(v.getBrand() + " " + v.getModel(), points, Long::sum);
        }
        return new DashboardDto(Instant.now(), funnel, battery, demand, viewsByVehicle(last24h));
    }

    // ---------- helpers ----------
    private static long count(List<VisitEvent> events, EventType type) {
        return events.stream().filter(e -> e.getEventType() == type).count();
    }

    private Map<Long, Vehicle> vehiclesById(List<VisitEvent> events) {
        List<Long> ids = events.stream().map(VisitEvent::getVehicleId).filter(Objects::nonNull).distinct().toList();
        Map<Long, Vehicle> map = new HashMap<>();
        if (!ids.isEmpty()) vehicleDAO.findByIdIn(ids).forEach(v -> map.put(v.getId(), v));
        return map;
    }

    private Map<String, Long> viewsByVehicle(List<VisitEvent> events) {
        List<VisitEvent> views = events.stream().filter(e -> e.getEventType() == EventType.VIEW).toList();
        Map<Long, Vehicle> vehicles = vehiclesById(views);
        Map<String, Long> counts = new HashMap<>();
        for (VisitEvent e : views) {
            Vehicle v = e.getVehicleId() == null ? null : vehicles.get(e.getVehicleId());
            if (v != null) counts.merge(title(v), 1L, Long::sum);
        }
        Map<String, Long> sorted = new LinkedHashMap<>();
        counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .forEach(en -> sorted.put(en.getKey(), en.getValue()));
        return sorted;
    }

    private static String title(Vehicle v) {
        return v.getModelYear() + " " + v.getBrand() + " " + v.getModel();
    }
}