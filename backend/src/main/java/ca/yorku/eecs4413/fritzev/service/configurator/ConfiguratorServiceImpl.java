package ca.yorku.eecs4413.fritzev.service.configurator;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import org.springframework.stereotype.Service;

// STUB - M6 Vehicle Configurator (D1 interface IConfigurator).
@Service
public class ConfiguratorServiceImpl implements ConfiguratorService {

    private final VehicleDAO vehicleDAO;
    private final CustomizationOptionDAO optionDAO;

    public ConfiguratorServiceImpl(VehicleDAO vehicleDAO, CustomizationOptionDAO optionDAO) {
        this.vehicleDAO = vehicleDAO;
        this.optionDAO = optionDAO;
    }

    @Override
    public Map<OptionCategory, List<OptionDto>> getOptions(Long vehicleId) {
        Vehicle vehicle = vehicleDAO.findDetailedById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        Map<OptionCategory, List<OptionDto>> result = new EnumMap<>(OptionCategory.class);
        vehicle.getOptions().stream().sorted(Comparator.comparing(CustomizationOption::getId))
                .forEach(o -> result.computeIfAbsent(o.getCategory(), ignored -> new ArrayList<>()).add(OptionDto.from(o)));
        return result;
    }

    @Override
    public ConfigurationPriceDto priceConfiguration(ConfigurationRequest request) {
        Vehicle vehicle = vehicleDAO.findDetailedById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        Set<Long> ids = new HashSet<>(request.optionIds());
        if (ids.size() != request.optionIds().size()) throw new BadRequestException("Duplicate option selected");
        Map<Long, CustomizationOption> allowed = new HashMap<>();
        vehicle.getOptions().forEach(o -> allowed.put(o.getId(), o));
        List<OptionDto> selected = new ArrayList<>();
        Set<OptionCategory> categories = EnumSet.noneOf(OptionCategory.class);
        for (Long id : request.optionIds()) {
            CustomizationOption option = allowed.get(id);
            if (option == null) throw new BadRequestException("Option is not available for this vehicle");
            if (!categories.add(option.getCategory())) throw new BadRequestException("Only one option per category is allowed");
            selected.add(OptionDto.from(option));
        }
        BigDecimal base = vehicle.getDealPrice() != null ? vehicle.getDealPrice() : vehicle.getPrice();
        BigDecimal optionsTotal = selected.stream().map(OptionDto::priceDelta).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ConfigurationPriceDto(vehicle.getId(), base, selected, optionsTotal, base.add(optionsTotal));
    }
}
