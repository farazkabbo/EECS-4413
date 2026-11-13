package ca.yorku.eecs4413.fritzev.dao; 

import ca.yorku.eecs4413.fritzev.domain.CustomizationOption; 
import ca.yorku.eecs4413.fritzev.domain.OptionCategory; 
import java.util.List; 
import org.springframework.data.jpa.repository.JpaRepository; 

// DAO for ca.yorku.eecs4413.fritzev.domain.CustomizationOption (M17 Data Access Layer). 
public interface CustomizationOptionDAO extends JpaRepository<CustomizationOption, Long> { 

    List<CustomizationOption> findByCategoryOrderByPriceDelta(OptionCategory category); 
} 
