package ca.yorku.eecs4413.fritzev.controller; 

import ca.yorku.eecs4413.fritzev.controller.dto.ChatReplyDto; 
import ca.yorku.eecs4413.fritzev.controller.dto.ChatRequest; 
import ca.yorku.eecs4413.fritzev.service.chatbot.ChatbotService; 
import jakarta.servlet.http.HttpSession; 
import jakarta.validation.Valid; 
import org.springframework.web.bind.annotation.*; 

// UC-16 Ask Chatbot (Deliverable 3). 
@RestController 
@RequestMapping("/api/chat") 
public class ChatController { 

    private final ChatbotService chatbotService; 

    public ChatController(ChatbotService chatbotService) { 
        this.chatbotService = chatbotService; 
    } 

    @PostMapping 
    public ChatReplyDto send(@Valid @RequestBody ChatRequest request, HttpSession session) { 
        return chatbotService.sendMessage(session.getId(), request.message()); 
    } 
} 
