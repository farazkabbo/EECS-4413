package ca.yorku.eecs4413.fritzev; 

import ca.yorku.eecs4413.fritzev.controller.SessionAuth; 
import ca.yorku.eecs4413.fritzev.dao.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 
import ca.yorku.eecs4413.fritzev.service.cart.CartService; 
import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.Test; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.boot.test.context.SpringBootTest; 
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; 
import org.springframework.http.MediaType; 
import org.springframework.mock.web.MockHttpSession; 
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors; 
import org.springframework.test.web.servlet.MockMvc; 
import org.springframework.test.web.servlet.MvcResult; 

import static org.hamcrest.Matchers.containsString; 
import static org.hamcrest.Matchers.hasSize; 
import static org.junit.jupiter.api.Assertions.*; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; 

/** Zaaid-owned end-to-end coverage for account/session, cart, checkout and reviews. */ 
@SpringBootTest 
@AutoConfigureMockMvc 
class ZaaidIntegrationTests { 

    @Autowired MockMvc mvc; 
    @Autowired CartDAO cartDAO; 
    @Autowired CartService cartService; 
    @Autowired PaymentAttemptCounterDAO paymentCounterDAO; 
    @Autowired VehicleDAO vehicleDAO; 
    @Autowired OrderDAO orderDAO; 
    @Autowired ReviewDAO reviewDAO; 

    @BeforeEach 
    void isolateMutableSeedRows() { 
        cartDAO.findByUserId(2L).ifPresent(cart -> { 
            cartService.clearCart(cart.getId()); 
        }); 
        cartDAO.findByUserId(4L).ifPresent(cart -> { 
            cartService.clearCart(cart.getId()); 
        }); 
        PaymentAttemptCounter counter = paymentCounterDAO.findById(1L).orElseThrow(); 
        counter.setAttempts(0); 
        paymentCounterDAO.saveAndFlush(counter); 
        Vehicle vehicle = vehicleDAO.findById(5L).orElseThrow(); 
        vehicle.setQuantity(2); 
        vehicle.setStatus(VehicleStatus.AVAILABLE); 
        vehicleDAO.saveAndFlush(vehicle); 
        reviewDAO.findAll().stream() 
                .filter(review -> review.getVehicle().getId().equals(5L) 
                        && review.getUser().getId().equals(4L)) 
                .forEach(reviewDAO::delete); 
        reviewDAO.flush(); 
    } 

    @Test 
    void cartAddUpdateRemoveRecalculatesTotalsAndHonoursCsrf() throws Exception { 
        MockHttpSession session = signedInSession(2L, "CUSTOMER"); 
        long itemId = addVehicle(session, 1L, 1); 
        mvc.perform(patch("/api/cart/items/{id}", itemId).session(session) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) 
                        .contentType(MediaType.APPLICATION_JSON) 
                        .content("{\"quantity\":2}")) 
                .andExpect(status().isOk()).andExpect(jsonPath("$.items[0].quantity").value(2)); 

        mvc.perform(delete("/api/cart/items/{id}", itemId).session(session) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) 
                .andExpect(status().isOk()).andExpect(jsonPath("$.items", hasSize(0))); 

        mvc.perform(post("/api/cart/items").session(session) 
                        .contentType(MediaType.APPLICATION_JSON) 
                        .content("{\"vehicleId\":1,\"optionIds\":[],\"quantity\":1}")) 
                .andExpect(status().isForbidden()); 
    } 

    @Test 
    void deniedPaymentPreservesCartStockAndOrders() throws Exception { 
        PaymentAttemptCounter counter = paymentCounterDAO.findById(1L).orElseThrow(); 
        counter.setAttempts(2); 
        paymentCounterDAO.saveAndFlush(counter); 
        MockHttpSession session = signedInSession(2L, "CUSTOMER"); 
        addVehicle(session, 5L, 1); 
        String checkoutId = startCheckout(session); 
        confirmShipping(session, checkoutId); 
        int stockBefore = vehicleDAO.findById(5L).orElseThrow().getQuantity(); 
        long ordersBefore = orderDAO.count(); 

        mvc.perform(post("/api/checkout/{id}/place-order", checkoutId).session(session) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) 
                        .contentType(MediaType.APPLICATION_JSON).content(card())) 
                .andExpect(status().isPaymentRequired()) 
                .andExpect(jsonPath("$.success").value(false)) 
                .andExpect(jsonPath("$.message").value("Credit Card Authorization Failed.")); 

        assertEquals(stockBefore, vehicleDAO.findById(5L).orElseThrow().getQuantity()); 
        assertEquals(ordersBefore, orderDAO.count()); 
        mvc.perform(get("/api/cart").session(session)) 
                .andExpect(status().isOk()).andExpect(jsonPath("$.items", hasSize(1))); 
    } 

    @Test 
    void duplicateCheckoutSubmissionCreatesOnlyOneOrder() throws Exception { 
        MockHttpSession session = signedInSession(2L, "CUSTOMER"); 
        addVehicle(session, 5L, 1); 
        String checkoutId = startCheckout(session); 
        confirmShipping(session, checkoutId); 
        long ordersBefore = orderDAO.count(); 

        mvc.perform(post("/api/checkout/{id}/place-order", checkoutId).session(session) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) 
                        .contentType(MediaType.APPLICATION_JSON).content(card())) 
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.message").value("Order Successfully Completed.")); 
        mvc.perform(post("/api/checkout/{id}/place-order", checkoutId).session(session) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) 
                        .contentType(MediaType.APPLICATION_JSON).content(card())) 
                .andExpect(status().isConflict()) 
                .andExpect(jsonPath("$.message").value(containsString("already been completed"))); 
        assertEquals(ordersBefore + 1, orderDAO.count()); 
    } 

    @Test 
    void verifiedOwnerCanReviewOnceAndDuplicateIsRejected() throws Exception { 
        MockHttpSession verified = signedInSession(4L, "CUSTOMER"); 
        String review = "{\"rating\":5,\"winterRangeKm\":400,\"winterRangeNotes\":\"Strong range\"," 
                + "\"homeChargingNotes\":\"Level 2 overnight\",\"softwareUpdateNotes\":\"Regular OTA\"," 
                + "\"overallSatisfaction\":5,\"comment\":\"Excellent EV\"}"; 

        mvc.perform(post("/api/vehicles/5/reviews").session(verified) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) 
                        .contentType(MediaType.APPLICATION_JSON).content(review)) 
                .andExpect(status().isCreated()).andExpect(jsonPath("$.vehicleId").value(5)); 
        mvc.perform(post("/api/vehicles/5/reviews").session(verified) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) 
                        .contentType(MediaType.APPLICATION_JSON).content(review)) 
                .andExpect(status().isConflict()); 

        MockHttpSession unverified = signedInSession(3L, "CUSTOMER"); 
        mvc.perform(post("/api/vehicles/6/reviews").session(unverified) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) 
                        .contentType(MediaType.APPLICATION_JSON).content(review)) 
                .andExpect(status().isForbidden()); 
    } 

    @Test 
    void loginLogoutSessionAndAnonymousCheckoutAreEnforced() throws Exception { 
        MockHttpSession anonymous = new MockHttpSession(); 
        mvc.perform(post("/api/checkout/start").session(anonymous) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) 
                .andExpect(status().isUnauthorized()); 

        MvcResult login = mvc.perform(post("/api/auth/login").session(anonymous) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) 
                        .contentType(MediaType.APPLICATION_JSON) 
                        .content("{\"username\":\"alice\",\"password\":\"Password123!\"}")) 
                .andExpect(status().isOk()).andReturn(); 
        MockHttpSession authenticated = (MockHttpSession) login.getRequest().getSession(false); 
        assertNotNull(authenticated); 
        mvc.perform(get("/api/auth/me").session(authenticated)).andExpect(status().isOk()) 
                .andExpect(jsonPath("$.username").value("alice")); 
        mvc.perform(post("/api/auth/logout").session(authenticated) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) 
                .andExpect(status().isNoContent()); 
        mvc.perform(get("/api/auth/me").session(authenticated)).andExpect(status().isUnauthorized()); 
    } 

    private MockHttpSession signedInSession(Long userId, String role) { 
        MockHttpSession session = new MockHttpSession(); 
        session.setAttribute(SessionAuth.USER_ID, userId); 
        session.setAttribute(SessionAuth.ROLE, role); 
        return session; 
    } 

    private long addVehicle(MockHttpSession session, long vehicleId, int quantity) throws Exception { 
        String body = mvc.perform(post("/api/cart/items").session(session) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) 
                        .contentType(MediaType.APPLICATION_JSON) 
                        .content("{\"vehicleId\":" + vehicleId + ",\"optionIds\":[],\"quantity\":" + quantity + "}")) 
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(); 
        java.util.regex.Matcher matcher = java.util.regex.Pattern 
                .compile("\\\"itemId\\\"\\s*:\\s*(\\d+)").matcher(body); 
        assertTrue(matcher.find(), "cart response did not contain itemId: " + body); 
        return Long.parseLong(matcher.group(1)); 
    } 

    private String startCheckout(MockHttpSession session) throws Exception { 
        String body = mvc.perform(post("/api/checkout/start").session(session) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) 
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(); 
        java.util.regex.Matcher matcher = java.util.regex.Pattern 
                .compile("\\\"checkoutId\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"") 
                .matcher(body); 
        assertTrue(matcher.find(), "checkout response did not contain checkoutId: " + body); 
        return matcher.group(1); 
    } 

    private void confirmShipping(MockHttpSession session, String checkoutId) throws Exception { 
        mvc.perform(put("/api/checkout/{id}/shipping", checkoutId).session(session) 
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) 
                        .contentType(MediaType.APPLICATION_JSON) 
                        .content("{\"street\":\"1 Main St\",\"city\":\"Toronto\",\"province\":\"ON\"," 
                                + "\"country\":\"Canada\",\"postalCode\":\"M3J 1P3\",\"phone\":\"416-555-0101\"}")) 
                .andExpect(status().isNoContent()); 
    } 

    private static String card() { 
        return "{\"card\":{\"holderName\":\"Alice Nguyen\",\"cardNumber\":\"4111111111111111\"," 
                + "\"expiry\":\"12/30\",\"cvv\":\"123\"}}"; 
    } 
} 
