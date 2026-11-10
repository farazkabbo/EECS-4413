package ca.yorku.eecs4413.fritzev.controller;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.VehicleFilter;
import ca.yorku.eecs4413.fritzev.domain.OptionCategory;
import ca.yorku.eecs4413.fritzev.domain.Shape;
import ca.yorku.eecs4413.fritzev.domain.VehicleCondition;
import ca.yorku.eecs4413.fritzev.exception.BadRequestException;
import ca.yorku.eecs4413.fritzev.service.catalog.CatalogService;
import ca.yorku.eecs4413.fritzev.service.configurator.ConfiguratorService;
import ca.yorku.eecs4413.fritzev.service.finance.FinanceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class VehiclePagesController {

    private final CatalogService catalogService;
    private final ConfiguratorService configuratorService;
    private final FinanceService financeService;

    public VehiclePagesController(CatalogService catalogService, ConfiguratorService configuratorService,
                                  FinanceService financeService) {
        this.catalogService = catalogService;
        this.configuratorService = configuratorService;
        this.financeService = financeService;
    }

    @GetMapping("/catalog")
    public String catalog(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String shape,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(required = false) Boolean noAccidents,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir,
            Model model) {
        Shape bodyShape = shape == null || shape.isBlank() ? null : Shape.valueOf(shape.toUpperCase(Locale.ROOT));
        VehicleCondition condition = category == null || category.isBlank() ? null
                : VehicleCondition.valueOf(category.toUpperCase(Locale.ROOT));
        model.addAttribute("vehicles", catalogService.listVehicles(
                new VehicleFilter(brand, bodyShape, condition, null, minYear, maxYear,
                        null, null, noAccidents), sort, dir));
        model.addAttribute("brands", catalogService.getBrands());
        model.addAttribute("shapes", catalogService.getShapes());
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedShape", shape);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("minYear", minYear);
        model.addAttribute("maxYear", maxYear);
        model.addAttribute("noAccidents", noAccidents);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        return "catalog";
    }

    @GetMapping("/vehicles/{id}")
    public String vehicle(@PathVariable Long id, Model model) {
        model.addAttribute("vehicle", catalogService.getVehicleDetails(id));
        return "vehicle";
    }

    @GetMapping("/compare")
    public String compare(@RequestParam(required = false) List<Long> ids, Model model) {
        if (ids == null || ids.isEmpty()) {
            model.addAttribute("error", "Select two to four vehicles to compare.");
            return "compare";
        }
        try {
            model.addAttribute("comparison", catalogService.compareVehicles(ids));
        } catch (BadRequestException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "compare";
    }

    @GetMapping("/vehicles/{id}/configure")
    public String configure(@PathVariable Long id, Model model) {
        model.addAttribute("vehicle", catalogService.getVehicleDetails(id));
        model.addAttribute("options", configuratorService.getOptions(id));
        return "configure";
    }

    @PostMapping("/vehicles/{id}/configure")
    public String configure(@PathVariable Long id,
                            @RequestParam(required = false) List<Long> optionIds, Model model) {
        model.addAttribute("vehicle", catalogService.getVehicleDetails(id));
        Map<OptionCategory, List<OptionDto>> options = configuratorService.getOptions(id);
        model.addAttribute("options", options);
        try {
            model.addAttribute("configuration",
                    configuratorService.priceConfiguration(id, optionIds == null ? List.of() : optionIds));
        } catch (BadRequestException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "configure";
    }

    @GetMapping("/vehicles/{id}/loan")
    public String loan(@PathVariable Long id, Model model) {
        addLoanPage(id, model);
        return "loan";
    }

    @PostMapping("/vehicles/{id}/loan")
    public String loan(@PathVariable Long id,
                       @RequestParam BigDecimal downPayment,
                       @RequestParam BigDecimal annualRatePercent,
                       @RequestParam int months,
                       Model model) {
        VehicleDetailDto vehicle = catalogService.getVehicleDetails(id);
        model.addAttribute("vehicle", vehicle);
        try {
            BigDecimal price = vehicle.dealPrice() == null ? vehicle.price() : vehicle.dealPrice();
            model.addAttribute("quote", financeService.calculateMonthlyPayment(
                    new LoanRequest(price, downPayment, annualRatePercent, months)));
        } catch (BadRequestException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "loan";
    }

    @GetMapping("/vehicles/{id}/savings")
    public String savings(@PathVariable Long id, Model model) {
        model.addAttribute("vehicle", catalogService.getVehicleDetails(id));
        return "savings";
    }

    @PostMapping("/vehicles/{id}/savings")
    public String savings(@PathVariable Long id,
                          @RequestParam BigDecimal annualKm,
                          @RequestParam BigDecimal gasolineLPer100km,
                          @RequestParam BigDecimal gasolinePricePerL,
                          @RequestParam BigDecimal electricityPricePerKwh,
                          Model model) {
        model.addAttribute("vehicle", catalogService.getVehicleDetails(id));
        try {
            model.addAttribute("report", financeService.estimateSavings(id,
                    new SavingsRequest(annualKm, gasolineLPer100km, gasolinePricePerL, electricityPricePerKwh)));
        } catch (BadRequestException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "savings";
    }

    private void addLoanPage(Long id, Model model) {
        model.addAttribute("vehicle", catalogService.getVehicleDetails(id));
    }
}
