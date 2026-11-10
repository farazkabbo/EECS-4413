package ca.yorku.eecs4413.fritzev.dao;

import ca.yorku.eecs4413.fritzev.domain.PaymentAttemptCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface PaymentAttemptCounterDAO extends JpaRepository<PaymentAttemptCounter, Long> {
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PaymentAttemptCounter> findById(Long id);
}
