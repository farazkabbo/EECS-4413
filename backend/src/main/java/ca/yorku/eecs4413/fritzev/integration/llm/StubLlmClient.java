package ca.yorku.eecs4413.fritzev.integration.llm; 

import org.springframework.stereotype.Component; 

// Placeholder LLM adapter used until the real provider is wired in Deliverable 3. 
@Component 
public class StubLlmClient implements LlmClient { 

    @Override 
    public String complete(String prompt) { 
        return "The Fritz EV assistant is not connected yet. Please browse the catalogue or visit the support page."; 
    } 
} 
