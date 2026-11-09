package ca.yorku.eecs4413.fritzev.integration.llm;

// M16 LLM Adapter (D1 interface ILLM).
public interface LlmClient {

    // Sends a prompt to the external LLM and returns its text answer.
    String complete(String prompt);
}
