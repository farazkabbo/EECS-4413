package ca.yorku.eecs4413.fritzev.dao; 

import ca.yorku.eecs4413.fritzev.domain.CartItem; 
import org.springframework.data.jpa.repository.JpaRepository; 

// DAO for ca.yorku.eecs4413.fritzev.domain.CartItem (M17 Data Access Layer). 
public interface CartItemDAO extends JpaRepository<CartItem, Long> { 

} 
