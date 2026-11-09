package ca.yorku.eecs4413.fritzev.dao;

import ca.yorku.eecs4413.fritzev.domain.Address;
import ca.yorku.eecs4413.fritzev.domain.AddressType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// DAO for ca.yorku.eecs4413.fritzev.domain.Address (M17 Data Access Layer).
public interface AddressDAO extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);

    Optional<Address> findByUserIdAndType(Long userId, AddressType type);
}
