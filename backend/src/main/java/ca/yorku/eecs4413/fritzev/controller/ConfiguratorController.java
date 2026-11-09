package ca.yorku.eecs4413.fritzev.controller;

import ca.yorku.eecs4413.fritzev.controller.dto.ConfigurationPriceDto;
import ca.yorku.eecs4413.fritzev.controller.dto.ConfigurationRequest;
import ca.yorku.eecs4413.fritzev.controller.dto.OptionDto;
import ca.yorku.eecs4413.fritzev.domain.OptionCategory;
import ca.yorku.eecs4413.fritzev.service.configurator.ConfiguratorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// UC-08 Select Customization Options.
@RestController
@RequestMapping("/api")
public class ConfiguratorController {

    private final ConfiguratorService configuratorService;

    public ConfiguratorController(ConfiguratorService configuratorService) {
        this.configuratorService = configuratorService;
    }

    @GetMapping("/vehicles/{id}/options")
    public Map<OptionCategory, List<OptionDto>> options(@PathVariable @Positive Long id) {
        return configuratorService.getOptions(id);
    }

    @PostMapping("/configurations/price")
    public ConfigurationPriceDto price(@Valid @RequestBody ConfigurationRequest request) {
        return configuratorService.priceConfiguration(request);
    }
}
