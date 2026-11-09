package ca.yorku.eecs4413.fritzev.service.configurator;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

// M6 Vehicle Configurator (D1 interface IConfigurator).
public interface ConfiguratorService {

    // UC-08
    Map<OptionCategory, List<OptionDto>> getOptions(Long vehicleId);

    // UC-08
    ConfigurationPriceDto priceConfiguration(ConfigurationRequest request);
}
