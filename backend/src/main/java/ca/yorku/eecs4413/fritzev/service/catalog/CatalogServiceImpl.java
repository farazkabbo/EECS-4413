package ca.yorku.eecs4413.fritzev.service.catalog;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.VehicleDAO;
import ca.yorku.eecs4413.fritzev.dao.VehicleFilter;
import ca.yorku.eecs4413.fritzev.dao.VehicleSpecifications;
import ca.yorku.eecs4413.fritzev.domain.CustomizationOption;
import ca.yorku.eecs4413.fritzev.domain.OptionCategory;
import ca.yorku.eecs4413.fritzev.domain.Vehicle;
import ca.yorku.eecs4413.fritzev.domain.VehicleCondition;
import ca.yorku.eecs4413.fritzev.domain.VehicleStatus;
import ca.yorku.eecs4413.fritzev.exception.BadRequestException;
import ca.yorku.eecs4413.fritzev.exception.ConflictException;
import ca.yorku.eecs4413.fritzev.exception.ResourceNotFoundException;
import ca.yorku.eecs4413.fritzev.service.review.ReviewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

// Catalogue queries and inventory updates.
@Service
@Transactional(readOnly = true)
public class CatalogServiceImpl implements CatalogService {

    private static final int MAX_HOT_DEALS = 50;

    private final VehicleDAO vehicleDAO;
    private final ReviewService reviewService;

    public CatalogServiceImpl(VehicleDAO vehicleDAO, ReviewService reviewService) {
        this.vehicleDAO = vehicleDAO;
        this.reviewService = reviewService;
    }

    @Override
    public List<VehicleSummaryDto> listVehicles(VehicleFilter filter, String sort) {
        return listVehicles(filter, sort, null);
    }

    @Override
    public List<VehicleSummaryDto> listVehicles(VehicleFilter filter, String sort, String direction) {
        if (filter.minPrice() != null && filter.maxPrice() != null
                && filter.minPrice().compareTo(filter.maxPrice()) > 0) {
            throw new BadRequestException("minPrice must not be greater than maxPrice");
        }
        if (filter.minYear() != null && filter.maxYear() != null && filter.minYear() > filter.maxYear()) {
            throw new BadRequestException("minYear must not be greater than maxYear");
        }
        Sort ordering = direction == null ? VehicleSpecifications.sortFor(sort)
                : VehicleSpecifications.sortFor(sort, direction);
        List<VehicleSummaryDto> results = vehicleDAO.findAll(VehicleSpecifications.matching(filter), ordering)
                .stream().map(VehicleSummaryDto::from).toList();
        if (isMileageSort(sort)) {
            Comparator<VehicleSummaryDto> byEffectiveMileage = Comparator.comparingInt(vehicle ->
                    vehicle.condition() == VehicleCondition.NEW ? 0 : vehicle.mileageKm());
            if (isDescendingMileageSort(sort, direction)) byEffectiveMileage = byEffectiveMileage.reversed();
            results = results.stream().sorted(byEffectiveMileage.thenComparing(VehicleSummaryDto::id)).toList();
        }
        return results;
    }

    private static boolean isMileageSort(String sort) {
        return "mileage".equalsIgnoreCase(sort) || "mileage_asc".equalsIgnoreCase(sort)
                || "mileage_desc".equalsIgnoreCase(sort);
    }

    private static boolean isDescendingMileageSort(String sort, String direction) {
        if (direction != null && !direction.isBlank()) return "desc".equalsIgnoreCase(direction);
        return "mileage_desc".equalsIgnoreCase(sort);
    }

    @Override
    public VehicleDetailDto getVehicleDetails(Long vehicleId) {
        Vehicle v = vehicleDAO.findDetailedById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle " + vehicleId + " not found"));

        // Options grouped by category, in a stable order (colour, interior, fabric, wheels).
        Map<OptionCategory, List<OptionDto>> options = new EnumMap<>(OptionCategory.class);
        v.getOptions().stream()
                .sorted(Comparator.comparing(CustomizationOption::getCategory)
                        .thenComparing(CustomizationOption::getPriceDelta)
                        .thenComparing(CustomizationOption::getName))
                .forEach(o -> options.computeIfAbsent(o.getCategory(), k -> new ArrayList<>()).add(OptionDto.from(o)));

        return new VehicleDetailDto(v.getId(), v.getBrand(), v.getModel(), v.getTrimName(), v.getModelYear(),
                v.getShape(), v.getVehicleCondition(), v.getPrice(), v.getDealPrice(), v.getMileageKm(),
                v.getRangeKm(), v.getBatteryKwh(), v.getEnergyKwhPer100km(), v.getBatteryHealthPct(),
                v.getDrivetrain(), v.getSeats(), v.getDescription(), v.getImageUrl(), v.getQuantity(),
                v.getStatus(), VehicleHistoryDto.from(v.getHistory()), options,
                reviewService.getAverageRating(vehicleId), reviewService.countReviews(vehicleId));
    }

    @Override
    public ComparisonTableDto compareVehicles(List<Long> vehicleIds) {
        if (vehicleIds == null || vehicleIds.size() < 2 || vehicleIds.size() > 4) {
            throw new BadRequestException("Compare requires between 2 and 4 vehicle ids");
        }
        if (new HashSet<>(vehicleIds).size() != vehicleIds.size()) {
            throw new BadRequestException("Vehicle ids must not contain duplicates");
        }
        Map<Long, Vehicle> found = new HashMap<>();
        vehicleDAO.findAllById(vehicleIds).forEach(vehicle -> found.put(vehicle.getId(), vehicle));
        if (found.size() != vehicleIds.size()) {
            Long missingId = vehicleIds.stream().filter(id -> !found.containsKey(id)).findFirst().orElseThrow();
            throw new ResourceNotFoundException("Vehicle " + missingId + " not found");
        }
        List<VehicleComparisonDto> rows = vehicleIds.stream()
                .map(id -> VehicleComparisonDto.from(found.get(id), reviewService.getAverageRating(id)))
                .toList();
        return new ComparisonTableDto(rows);
    }

    @Override
    public List<String> getBrands() {
        return vehicleDAO.findAll(VehicleSpecifications.matching(new VehicleFilter(null, null, null, null,
                        null, null, null, null, null)), Sort.by("brand"))
                .stream().map(Vehicle::getBrand).distinct().toList();
    }

    @Override
    public List<ca.yorku.eecs4413.fritzev.domain.Shape> getShapes() {
        return vehicleDAO.findAll(VehicleSpecifications.matching(new VehicleFilter(null, null, null, null,
                        null, null, null, null, null)), Sort.by("shape"))
                .stream().map(Vehicle::getShape).distinct().toList();
    }

    @Override
    public List<HotDealDto> getHotDeals(int limit) {
        int size = Math.max(1, Math.min(limit, MAX_HOT_DEALS));
        return vehicleDAO.findHotDeals(VehicleStatus.AVAILABLE, PageRequest.of(0, size)).stream()
                .map(CatalogServiceImpl::toHotDeal)
                .toList();
    }

    private static HotDealDto toHotDeal(Vehicle v) {
        BigDecimal savings = v.getPrice().subtract(v.getDealPrice());
        int pct = savings.multiply(BigDecimal.valueOf(100))
                .divide(v.getPrice(), 0, RoundingMode.HALF_UP).intValue();
        return new HotDealDto(v.getId(), v.getBrand(), v.getModel(), v.getTrimName(), v.getModelYear(),
                v.getVehicleCondition(), v.getMileageKm(), v.getPrice(), v.getDealPrice(), savings, pct,
                v.getImageUrl());
    }

    @Override
    public boolean isAvailable(Long vehicleId) {
        return vehicleDAO.findById(vehicleId)
                .map(v -> v.getStatus() == VehicleStatus.AVAILABLE && v.getQuantity() > 0)
                .orElse(false);
    }

    @Override
    @Transactional
    public void markSold(Long vehicleId, int quantity) {
        Vehicle v = vehicleDAO.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle " + vehicleId + " not found"));
        if (quantity < 1 || v.getQuantity() < quantity) {
            throw new ConflictException("Vehicle " + vehicleId + " does not have " + quantity + " unit(s) in stock");
        }
        v.setQuantity(v.getQuantity() - quantity);
        if (v.getQuantity() == 0) v.setStatus(VehicleStatus.SOLD);
    }
}
