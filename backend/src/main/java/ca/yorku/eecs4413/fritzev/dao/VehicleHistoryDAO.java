package ca.yorku.eecs4413.fritzev.dao; 

import ca.yorku.eecs4413.fritzev.domain.VehicleHistory; 
import java.util.Optional; 
import org.springframework.data.jpa.repository.JpaRepository; 

// DAO for ca.yorku.eecs4413.fritzev.domain.VehicleHistory (M17 Data Access Layer). 
public interface VehicleHistoryDAO extends JpaRepository<VehicleHistory, Long> { 

    Optional<VehicleHistory> findByVehicleId(Long vehicleId); 
} 
