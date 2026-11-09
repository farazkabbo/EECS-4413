package ca.yorku.eecs4413.fritzev.dao;

import ca.yorku.eecs4413.fritzev.domain.EventType;
import ca.yorku.eecs4413.fritzev.domain.VisitEvent;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// DAO for ca.yorku.eecs4413.fritzev.domain.VisitEvent (M17 Data Access Layer).
public interface VisitEventDAO extends JpaRepository<VisitEvent, Long> {

    List<VisitEvent> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(LocalDateTime from, LocalDateTime to);

    long countByEventTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            EventType type, LocalDateTime from, LocalDateTime to);
}
