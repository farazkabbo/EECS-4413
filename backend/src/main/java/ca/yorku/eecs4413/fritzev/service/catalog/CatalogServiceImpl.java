package ca.yorku.eecs4413.fritzev.service.catalog;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.VehicleDAO;
import ca.yorku.eecs4413.fritzev.dao.VehicleFilter;
import ca.yorku.eecs4413.fritzev.dao.VehicleSpecifications;
import ca.yorku.eecs4413.fritzev.domain.CustomizationOption;
import ca.yorku.eecs4413.fritzev.domain.OptionCategory;
import ca.yorku.eecs4413.fritzev.domain.Vehicle;
import ca.yorku.eecs4413.fritzev.domain.VehicleStatus;
import ca.yorku.eecs4413.fritzev.exception.BadRequestException;
import ca.yorku.eecs4413.fritzev.exception.ConflictException;
import ca.yorku.eecs4413.fritzev.exception.NotImplementedYetException;
import ca.yorku.eecs4413.fritzev.exception.ResourceNotFoundException;
import ca.yorku.eecs4413.fritzev.service.review.ReviewService;
import org.springframework.data.domain.PageRequest;
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
        if (filter.minPrice() != null && filter.maxPrice() != null
                && filter.minPrice().compareTo(filter.maxPrice()) > 0) {
            throw new BadRequestException("minPrice must not be greater than maxPrice");
        }
        return vehicleDAO.findAll(VehicleSpecifications.matching(filter), VehicleSpecifications.sortFor(sort))
                .stream().map(VehicleSummaryDto::from).toList();
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
    public List<VehicleDetailDto> compareVehicles(List<Long> vehicleIds) {
        // TODO(Tamjeed, PB-22): validate 2..4 distinct ids, then return ids.stream().map(this::getVehicleDetails).toList()
        throw new NotImplementedYetException("UC-07 CatalogService.compareVehicles is not implemented yet (owner: Tamjeed, PB-22)");
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
