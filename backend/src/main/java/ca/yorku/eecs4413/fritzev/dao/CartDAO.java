package ca.yorku.eecs4413.fritzev.dao; 

import ca.yorku.eecs4413.fritzev.domain.Cart; 
import java.util.Optional; 
import org.springframework.data.jpa.repository.JpaRepository; 

// DAO for ca.yorku.eecs4413.fritzev.domain.Cart (M17 Data Access Layer). 
public interface CartDAO extends JpaRepository<Cart, Long> { 

    Optional<Cart> findByUserId(Long userId); 

    Optional<Cart> findBySessionToken(String sessionToken); 
} 
