package ca.yorku.eecs4413.fritzev.dao;

import ca.yorku.eecs4413.fritzev.domain.Vehicle;
import ca.yorku.eecs4413.fritzev.domain.VehicleStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// DAO for ca.yorku.eecs4413.fritzev.domain.Vehicle (M17 Data Access Layer).
public interface VehicleDAO extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    // Loads a vehicle together with its history report and options (UC-06).
    @EntityGraph(attributePaths = {"history", "options"})
    @Query("select v from Vehicle v where v.id = :id")
    Optional<Vehicle> findDetailedById(@Param("id") Long id);

    // Hot deals
    @Query("""
            select v from Vehicle v
            where v.dealPrice is not null and v.dealPrice < v.price and v.status = :status
            order by (v.price - v.dealPrice) / v.price desc, v.id""")
    List<Vehicle> findHotDeals(@Param("status") VehicleStatus status, Pageable page);

    List<Vehicle> findByIdIn(List<Long> ids);
}
