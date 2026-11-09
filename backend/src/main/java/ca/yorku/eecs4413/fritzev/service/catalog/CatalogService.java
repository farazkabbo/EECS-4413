package ca.yorku.eecs4413.fritzev.service.catalog;

import ca.yorku.eecs4413.fritzev.controller.dto.HotDealDto;
import ca.yorku.eecs4413.fritzev.controller.dto.VehicleDetailDto;
import ca.yorku.eecs4413.fritzev.controller.dto.VehicleSummaryDto;
import ca.yorku.eecs4413.fritzev.dao.VehicleFilter;

import java.util.List;

// M5 Catalog Manager (D1 interfaces ICatalog + IInventory).
public interface CatalogService {

    // UC-04 / UC-05
    List<VehicleSummaryDto> listVehicles(VehicleFilter filter, String sort);

    // UC-06
    VehicleDetailDto getVehicleDetails(Long vehicleId);

    // UC-07
    List<VehicleDetailDto> compareVehicles(List<Long> vehicleIds);

    // Hot deals (D3 demo item 15)
    List<HotDealDto> getHotDeals(int limit);

    // IInventory
    boolean isAvailable(Long vehicleId);

    // IInventory
    void markSold(Long vehicleId, int quantity);
}
