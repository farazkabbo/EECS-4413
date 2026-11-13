package ca.yorku.eecs4413.fritzev.integration.payment; 

import ca.yorku.eecs4413.fritzev.controller.dto.CardInfo; 
import org.springframework.stereotype.Component; 
import org.springframework.transaction.annotation.Transactional; 

import java.math.BigDecimal; 

// Deliberately small payment adapter used for checkout demonstrations and tests. 
@Component 
public class MockPaymentGateway implements PaymentGateway { 

    private final ca.yorku.eecs4413.fritzev.dao.PaymentAttemptCounterDAO counterDAO; 

    public MockPaymentGateway(ca.yorku.eecs4413.fritzev.dao.PaymentAttemptCounterDAO counterDAO) { 
        this.counterDAO = counterDAO; 
    } 

    @Override 
    @Transactional 
    public PaymentResult authorize(CardInfo card, BigDecimal amount) { 
        // The counter lives in the database so the deterministic sequence survives restarts. 
        var counter = counterDAO.findById(1L).orElseGet(() -> new ca.yorku.eecs4413.fritzev.domain.PaymentAttemptCounter(1L, 0)); 
        long attempt = counter.getAttempts() + 1; 
        counter.setAttempts(attempt); 
        counterDAO.save(counter); 
        // Attempts 1 and 2 approve; every third attempt is denied. 
        boolean approved = attempt % 3 != 0; 
        return new PaymentResult(approved, approved ? "MOCK-" + attempt : null); 
    } 
} 
