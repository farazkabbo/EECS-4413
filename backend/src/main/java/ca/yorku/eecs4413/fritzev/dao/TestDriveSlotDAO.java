package ca.yorku.eecs4413.fritzev.dao; 

import ca.yorku.eecs4413.fritzev.domain.DriveType; 
import ca.yorku.eecs4413.fritzev.domain.TestDriveSlot; 
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param; 
import java.time.LocalDateTime; 
import java.util.List; 
import org.springframework.data.jpa.repository.JpaRepository; 

// DAO for ca.yorku.eecs4413.fritzev.domain.TestDriveSlot (M17 Data Access Layer). 
public interface TestDriveSlotDAO extends JpaRepository<TestDriveSlot, Long> { 

    // Slots of a vehicle in [from, to) that have no booking yet (UC-11). 
    @Query(""" 
            select s from TestDriveSlot s 
            where s.vehicle.id = :vehicleId and s.driveType = :type 
              and s.slotStart >= :from and s.slotStart < :to 
              and not exists (select b from TestDriveBooking b where b.slot = s) 
            order by s.slotStart""") 
    List<TestDriveSlot> findFreeSlots(@Param("vehicleId") Long vehicleId, 
                                      @Param("type") DriveType type, 
                                      @Param("from") LocalDateTime from, 
                                      @Param("to") LocalDateTime to); 
} 
