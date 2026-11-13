package ca.yorku.eecs4413.fritzev.service.chatbot; 

import ca.yorku.eecs4413.fritzev.controller.dto.*; 
import ca.yorku.eecs4413.fritzev.dao.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 
import ca.yorku.eecs4413.fritzev.exception.*; 

import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 

import org.springframework.stereotype.Service; 

// STUB - M13 Chatbot Service (D1 interface IChat) - Deliverable 3. 
@Service 
public class ChatbotServiceImpl implements ChatbotService { 

    private final VehicleDAO vehicleDAO; 
    private final ca.yorku.eecs4413.fritzev.integration.llm.LlmClient llmClient; 

    public ChatbotServiceImpl(VehicleDAO vehicleDAO, ca.yorku.eecs4413.fritzev.integration.llm.LlmClient llmClient) { 
        this.vehicleDAO = vehicleDAO; 
        this.llmClient = llmClient; 
    } 

    @Override 
    public ChatReplyDto sendMessage(String sessionId, String text) { 
        // TODO(Mohammed): build prompt with catalogue summary, call llmClient.complete(prompt) 
        throw new NotImplementedYetException("UC-16 ChatbotService.sendMessage is not implemented yet (owner: Mohammed, PB-39)"); 
    } 
} 
