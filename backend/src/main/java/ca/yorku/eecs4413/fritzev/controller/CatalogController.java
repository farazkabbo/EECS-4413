package ca.yorku.eecs4413.fritzev.controller; 

import ca.yorku.eecs4413.fritzev.controller.dto.HotDealDto; 
import ca.yorku.eecs4413.fritzev.controller.dto.ComparisonTableDto; 
import ca.yorku.eecs4413.fritzev.controller.dto.VehicleDetailDto; 
import ca.yorku.eecs4413.fritzev.controller.dto.VehicleSummaryDto; 
import ca.yorku.eecs4413.fritzev.dao.VehicleFilter; 
import ca.yorku.eecs4413.fritzev.domain.Shape; 
import ca.yorku.eecs4413.fritzev.domain.VehicleCondition; 
import ca.yorku.eecs4413.fritzev.service.catalog.CatalogService; 
import jakarta.validation.constraints.*; 
import org.springframework.web.bind.annotation.*; 

import java.math.BigDecimal; 
import java.util.List; 

// Catalogue REST endpoints (UC-04..UC-07, hot deals). 
@RestController 
@RequestMapping("/api/vehicles") 
public class CatalogController { 

    private final CatalogService catalogService; 

    public CatalogController(CatalogService catalogService) { 
        this.catalogService = catalogService; 
    } 

    // UC-04 list + UC-05 sort/filter. 
    @GetMapping 
    public List<VehicleSummaryDto> list( 
            @RequestParam(required = false) @Size(max = 40) @Pattern(regexp = "^[\\p{L}0-9 .-]*$", 
                    message = "may only contain letters, digits, spaces, dots and dashes") String brand, 
            @RequestParam(required = false) Shape shape, 
            @RequestParam(required = false) VehicleCondition condition, 
            @RequestParam(required = false) VehicleCondition category, 
            @RequestParam(required = false) @Min(2000) @Max(2100) Integer year, 
            @RequestParam(required = false) @Min(2000) @Max(2100) Integer minYear, 
            @RequestParam(required = false) @Min(2000) @Max(2100) Integer maxYear, 
            @RequestParam(required = false) @DecimalMin("0") BigDecimal minPrice, 
            @RequestParam(required = false) @DecimalMin("0") BigDecimal maxPrice, 
            @RequestParam(required = false) Boolean accidentFree, 
            @RequestParam(required = false) Boolean noAccidents, 
            @RequestParam(required = false) @Size(max = 20) String sort, 
            @RequestParam(required = false) @Size(max = 10) String dir) { 
        return catalogService.listVehicles( 
                new VehicleFilter(brand, shape, category == null ? condition : category, year, minYear, maxYear, minPrice, maxPrice, 
                        noAccidents == null ? accidentFree : noAccidents), sort, dir); 
    } 

    @GetMapping("/brands") 
    public List<String> brands() { 
        return catalogService.getBrands(); 
    } 

    @GetMapping("/shapes") 
    public List<Shape> shapes() { 
        return catalogService.getShapes(); 
    } 

    // Hot deals 
    @GetMapping("/hot-deals") 
    public List<HotDealDto> hotDeals(@RequestParam(defaultValue = "6") @Min(1) @Max(50) int limit) { 
        return catalogService.getHotDeals(limit); 
    } 

    // UC-07 (Tamjeed) 
    @GetMapping("/compare") 
    public ComparisonTableDto compare(@RequestParam @Size(min = 2, max = 4) List<@NotNull @Positive Long> ids) { 
        return catalogService.compareVehicles(ids); 
    } 

    // UC-06 
    @GetMapping("/{id}") 
    public VehicleDetailDto details(@PathVariable @Positive Long id) { 
        return catalogService.getVehicleDetails(id); 
    } 
} 
