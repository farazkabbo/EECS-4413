package ca.yorku.eecs4413.fritzev; 

import ca.yorku.eecs4413.fritzev.controller.SessionAuth; 
import ca.yorku.eecs4413.fritzev.dao.ReviewDAO; 
import org.junit.jupiter.api.AfterEach; 
import org.junit.jupiter.api.DisplayName; 
import org.junit.jupiter.api.Test; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.boot.test.context.SpringBootTest; 
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; 
import org.springframework.http.MediaType; 
import org.springframework.mock.web.MockHttpSession; 
import org.springframework.test.web.servlet.MockMvc; 

import java.io.IOException; 
import java.nio.file.Files; 
import java.nio.file.Path; 
import java.util.List; 
import java.util.stream.Stream; 

import static org.junit.jupiter.api.Assertions.assertTrue; 
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; 

// Mohammed's D1 test case T11: review text containing HTML/script is stored as data and never rendered as markup. 
@SpringBootTest 
@AutoConfigureMockMvc 
class MohammedSecurityTests { 

    private static final long CAROL = 4L;      // verified EV owner in data.sql 
    private static final long VEHICLE = 9L;    // not reviewed by carol in the seed data 
    private static final String PAYLOAD = "<script>alert('xss')</script><img src=x onerror=alert(1)>"; 

    @Autowired MockMvc mvc; 
    @Autowired ReviewDAO reviewDAO; 

    @AfterEach 
    void removeTestReview() { 
        reviewDAO.findByVehicleIdOrderByCreatedAtDesc(VEHICLE).stream() 
                .filter(r -> r.getUser().getId().equals(CAROL)) 
                .forEach(reviewDAO::delete); 
    } 

    @Test 
    @DisplayName("T11 script-like review text is returned as JSON data, never as executable HTML") 
    void t11_reviewPayloadIsDataNotMarkup() throws Exception { 
        MockHttpSession carol = new MockHttpSession(); 
        carol.setAttribute(SessionAuth.USER_ID, CAROL); 
        carol.setAttribute(SessionAuth.ROLE, "CUSTOMER"); 

        String body = """ 
                {"rating":5,"winterRangeKm":400,"winterRangeNotes":%s,"homeChargingNotes":"Level 2 at home", 
                 "softwareUpdateNotes":"Monthly","overallSatisfaction":5,"comment":%s}""" 
                .formatted(json(PAYLOAD), json(PAYLOAD)); 

        mvc.perform(post("/api/vehicles/{id}/reviews", VEHICLE).session(carol).with(csrf()) 
                        .contentType(MediaType.APPLICATION_JSON).content(body)) 
                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.comment").value(PAYLOAD)); 

        // Served as JSON with nosniff, so a browser can never interpret the text as a page. 
        mvc.perform(get("/api/vehicles/{id}/reviews", VEHICLE)) 
                .andExpect(status().isOk()) 
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) 
                .andExpect(header().string("X-Content-Type-Options", "nosniff")) 
                .andExpect(jsonPath("$[0].comment").value(PAYLOAD)) 
                .andExpect(jsonPath("$[0].winterRangeNotes").value(PAYLOAD)); 
    } 

    @Test 
    @DisplayName("T11 no page renders server data as raw HTML (React and Thymeleaf escape by default)") 
    void t11_noRawHtmlRendering() throws IOException { 
        List<String> offenders = scan(Path.of("..", "frontend", "src"), "dangerouslySetInnerHTML"); 
        offenders.addAll(scan(Path.of("src", "main", "resources", "templates"), "th:utext")); 
        assertTrue(offenders.isEmpty(), "Raw HTML rendering found in: " + offenders); 
    } 

    private static List<String> scan(Path dir, String needle) throws IOException { 
        if (!Files.isDirectory(dir)) return new java.util.ArrayList<>(); 
        try (Stream<Path> files = Files.walk(dir)) { 
            return new java.util.ArrayList<>(files.filter(Files::isRegularFile) 
                    .filter(p -> { 
                        try { return Files.readString(p).contains(needle); } catch (IOException e) { return false; } 
                    }) 
                    .map(Path::toString).toList()); 
        } 
    } 

    private static String json(String s) { 
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""; 
    } 
} 
