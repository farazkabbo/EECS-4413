package ca.yorku.eecs4413.fritzev.exception; 

// Input is well formed but not acceptable, maps to 400. 
public class BadRequestException extends RuntimeException { 
    public BadRequestException(String message) { 
        super(message); 
    } 
} 
