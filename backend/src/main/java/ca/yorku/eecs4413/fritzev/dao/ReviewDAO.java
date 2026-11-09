package ca.yorku.eecs4413.fritzev.dao;

import ca.yorku.eecs4413.fritzev.domain.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// DAO for ca.yorku.eecs4413.fritzev.domain.Review (M17 Data Access Layer).
public interface ReviewDAO extends JpaRepository<Review, Long> {

    List<Review> findByVehicleIdOrderByCreatedAtDesc(Long vehicleId);

    boolean existsByVehicleIdAndUserId(Long vehicleId, Long userId);

    // Average star rating, or null when the vehicle has no reviews.
    @Query("select avg(r.rating) from Review r where r.vehicle.id = :vehicleId")
    Double averageRating(@Param("vehicleId") Long vehicleId);

    long countByVehicleId(Long vehicleId);
}
