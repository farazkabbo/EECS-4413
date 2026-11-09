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
        // TODO(Tamjeed): vehicleDAO.findDetailedById(...).getOptions()
        throw new NotImplementedYetException("UC-08 ConfiguratorService.getOptions is not implemented yet (owner: Tamjeed, PB-22)");
    }

    @Override
    public ConfigurationPriceDto priceConfiguration(ConfigurationRequest request) {
        // TODO(Tamjeed): every option must belong to the vehicle and at most one per category; base = dealPrice if set, else price
        throw new NotImplementedYetException("UC-08 ConfiguratorService.priceConfiguration is not implemented yet (owner: Tamjeed, PB-22)");
    }
}
