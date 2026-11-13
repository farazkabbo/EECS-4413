package ca.yorku.eecs4413.fritzev.controller.dto; 

import jakarta.validation.constraints.*; 
import jakarta.validation.Valid; 
import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 

// Chatbot answer, optionally linking a vehicle or support page (UC-16). 
public record ChatReplyDto( 
        String reply, String linkUrl) { 
} 
