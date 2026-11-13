package ca.yorku.eecs4413.fritzev.integration.payment; 

// Outcome of one authorisation request. 
public record PaymentResult(boolean approved, String reference) { 
} 
