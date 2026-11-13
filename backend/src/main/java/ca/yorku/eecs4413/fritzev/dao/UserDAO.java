package ca.yorku.eecs4413.fritzev.dao; 

import ca.yorku.eecs4413.fritzev.domain.User; 
import java.util.Optional; 
import org.springframework.data.jpa.repository.JpaRepository; 

// DAO for ca.yorku.eecs4413.fritzev.domain.User (M17 Data Access Layer). 
public interface UserDAO extends JpaRepository<User, Long> { 

    Optional<User> findByUsername(String username); 

    boolean existsByUsername(String username); 

    boolean existsByEmail(String email); 
} 
