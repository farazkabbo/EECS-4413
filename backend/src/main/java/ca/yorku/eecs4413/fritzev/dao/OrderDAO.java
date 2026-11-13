package ca.yorku.eecs4413.fritzev.dao; 

import ca.yorku.eecs4413.fritzev.domain.Order; 
import ca.yorku.eecs4413.fritzev.domain.OrderStatus; 
import java.time.LocalDateTime; 
import java.util.List; 
import org.springframework.data.jpa.repository.JpaRepository; 

// DAO for ca.yorku.eecs4413.fritzev.domain.Order (M17 Data Access Layer). 
public interface OrderDAO extends JpaRepository<Order, Long> { 

    // Used by the monthly sales report (UC-17) 
    List<Order> findByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan( 
            OrderStatus status, LocalDateTime fromInclusive, LocalDateTime toExclusive); 

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId); 
} 
