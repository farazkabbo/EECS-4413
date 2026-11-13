package ca.yorku.eecs4413.fritzev.domain; 

import jakarta.persistence.Entity; 
import jakarta.persistence.Id; 
import jakarta.persistence.Table; 

@Entity 
@Table(name = "payment_attempt_counter") 
public class PaymentAttemptCounter { 
    @Id 
    private Long id; 
    private long attempts; 

    public PaymentAttemptCounter() { } 
    public PaymentAttemptCounter(Long id, long attempts) { this.id = id; this.attempts = attempts; } 
    public Long getId() { return id; } 
    public void setId(Long id) { this.id = id; } 
    public long getAttempts() { return attempts; } 
    public void setAttempts(long attempts) { this.attempts = attempts; } 
} 
