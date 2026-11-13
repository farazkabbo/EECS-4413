package ca.yorku.eecs4413.fritzev.service.chatbot; 

import ca.yorku.eecs4413.fritzev.controller.dto.*; 
import ca.yorku.eecs4413.fritzev.dao.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 
import ca.yorku.eecs4413.fritzev.exception.*; 

import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 

// M13 Chatbot Service (D1 interface IChat) - Deliverable 3. 
public interface ChatbotService { 

    // UC-16 
    ChatReplyDto sendMessage(String sessionId, String text); 
} 
