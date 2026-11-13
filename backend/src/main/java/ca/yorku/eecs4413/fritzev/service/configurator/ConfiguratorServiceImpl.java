package ca.yorku.eecs4413.fritzev.service.configurator; 

import ca.yorku.eecs4413.fritzev.controller.dto.*; 
import ca.yorku.eecs4413.fritzev.dao.VehicleDAO; 
import ca.yorku.eecs4413.fritzev.domain.CustomizationOption; 
import ca.yorku.eecs4413.fritzev.domain.OptionCategory; 
import ca.yorku.eecs4413.fritzev.domain.Vehicle; 
import ca.yorku.eecs4413.fritzev.exception.BadRequestException; 
import ca.yorku.eecs4413.fritzev.exception.ResourceNotFoundException; 

import java.math.BigDecimal; 
import java.util.*; 

import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; 

@Service 
@Transactional(readOnly = true) 
public class ConfiguratorServiceImpl implements ConfiguratorService { 

    private final VehicleDAO vehicleDAO; 

    public ConfiguratorServiceImpl(VehicleDAO vehicleDAO) { 
        this.vehicleDAO = vehicleDAO; 
    } 

    @Override 
    public Map<OptionCategory, List<OptionDto>> getOptions(Long vehicleId) { 
        Vehicle vehicle = vehicleDAO.findDetailedById(vehicleId) 
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle " + vehicleId + " not found")); 
        Map<OptionCategory, List<OptionDto>> grouped = new EnumMap<>(OptionCategory.class); 
        vehicle.getOptions().stream() 
                .sorted(Comparator.comparing(CustomizationOption::getCategory) 
                        .thenComparing(CustomizationOption::getPriceDelta) 
                        .thenComparing(CustomizationOption::getName)) 
                .forEach(option -> grouped.computeIfAbsent(option.getCategory(), ignored -> new ArrayList<>()) 
                        .add(OptionDto.from(option))); 
        return grouped; 
    } 

    @Override 
    public ConfigurationPriceDto priceConfiguration(ConfigurationRequest request) { 
        return priceConfiguration(request.vehicleId(), request.optionIds()); 
    } 

    @Override 
    @Transactional(readOnly = true) 
    public ConfigurationPriceDto priceConfiguration(Long vehicleId, List<Long> optionIds) { 
        Vehicle vehicle = vehicleDAO.findDetailedById(vehicleId) 
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle " + vehicleId + " not found")); 
        if (optionIds == null || new HashSet<>(optionIds).size() != optionIds.size()) { 
            throw new BadRequestException("Option ids must not contain duplicates"); 
        } 

        Map<Long, CustomizationOption> optionsById = new HashMap<>(); 
        vehicle.getOptions().forEach(option -> optionsById.put(option.getId(), option)); 
        List<OptionDto> selected = new ArrayList<>(); 
        Set<OptionCategory> selectedCategories = EnumSet.noneOf(OptionCategory.class); 
        BigDecimal optionsTotal = BigDecimal.ZERO; 
        for (Long optionId : optionIds) { 
            CustomizationOption option = optionsById.get(optionId); 
            if (option == null) { 
                throw new BadRequestException("Option " + optionId + " does not belong to vehicle " + vehicleId); 
            } 
            if (!selectedCategories.add(option.getCategory())) { 
                throw new BadRequestException("Choose at most one option from " + option.getCategory()); 
            } 
            selected.add(OptionDto.from(option)); 
            optionsTotal = optionsTotal.add(option.getPriceDelta()); 
        } 
        BigDecimal basePrice = vehicle.getDealPrice() == null ? vehicle.getPrice() : vehicle.getDealPrice(); 
        return new ConfigurationPriceDto(vehicleId, basePrice, List.copyOf(selected), optionsTotal, 
                basePrice.add(optionsTotal)); 
    } 
} 
