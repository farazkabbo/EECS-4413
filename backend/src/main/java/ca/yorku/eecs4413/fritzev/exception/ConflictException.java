package ca.yorku.eecs4413.fritzev.exception; 

// The request conflicts with current state 
public class ConflictException extends RuntimeException { 
    public ConflictException(String message) { 
        super(message); 
    } 
} 
