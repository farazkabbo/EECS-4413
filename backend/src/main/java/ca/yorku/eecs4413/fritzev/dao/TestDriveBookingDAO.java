package ca.yorku.eecs4413.fritzev.dao;

import ca.yorku.eecs4413.fritzev.domain.TestDriveBooking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// DAO for ca.yorku.eecs4413.fritzev.domain.TestDriveBooking (M17 Data Access Layer).
public interface TestDriveBookingDAO extends JpaRepository<TestDriveBooking, Long> {

    boolean existsBySlotId(Long slotId);

    List<TestDriveBooking> findByUserId(Long userId);
}
