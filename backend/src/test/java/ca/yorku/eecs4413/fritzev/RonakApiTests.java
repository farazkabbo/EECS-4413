package ca.yorku.eecs4413.fritzev;

import ca.yorku.eecs4413.fritzev.controller.SessionAuth;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Ronak-owned tests: test-drive booking, event tracking, admin-only reports and dashboard. */
@SpringBootTest
@AutoConfigureMockMvc
class RonakApiTests {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("UC-11 free slots exclude already-booked slots")
    void freeSlotsExcludeBookedOnes() throws Exception {
        // seed: vehicle 1 has AT_HOME slots 1 and 2 two days ahead; slot 2 is booked by bob
        mvc.perform(get("/api/vehicles/1/test-drive-slots").param("type", "AT_HOME")
                        .param("date", LocalDate.now().plusDays(2).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
        mvc.perform(get("/api/vehicles/1/test-drive-slots").param("type", "BOAT")
                        .param("date", LocalDate.now().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("UC-11 a slot cannot be double booked (409), and cancelling frees it")
    void doubleBookingIsRejected() throws Exception {
        MockHttpSession alice = session(2L, "CUSTOMER");
        MockHttpSession carol = session(4L, "CUSTOMER");
        String body = "{\"slotId\":3}";   // seed: VIRTUAL slot tomorrow, no address needed
        String json = mvc.perform(post("/api/test-drives").session(alice).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long bookingId = ((Number) JsonPath.read(json, "$.id")).longValue();
        try {
            mvc.perform(post("/api/test-drives").session(carol).with(csrf())
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isConflict());
            mvc.perform(delete("/api/test-drives/{id}", bookingId).session(carol).with(csrf()))
                    .andExpect(status().isForbidden());
        } finally {
            mvc.perform(delete("/api/test-drives/{id}", bookingId).session(alice).with(csrf()))
                    .andExpect(status().isNoContent());
        }
        mvc.perform(post("/api/test-drives").session(carol).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());   // slot is free again
        // clean up so other tests keep a predictable seed state
        mvc.perform(get("/api/test-drives/mine").session(carol)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("UC-11 validation: at-home needs an address, anonymous -> 401, unknown slot -> 404")
    void bookingValidation() throws Exception {
        mvc.perform(post("/api/test-drives").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slotId\":1,\"address\":\"1 Main St\"}"))
                .andExpect(status().isUnauthorized());
        MockHttpSession alice = session(2L, "CUSTOMER");
        mvc.perform(post("/api/test-drives").session(alice).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"slotId\":1}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/test-drives").session(alice).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"slotId\":99999}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("UC-17/18/19 reports and dashboard are admin-only and return data for an admin")
    void adminReportsAndDashboard() throws Exception {
        String sales = "/api/admin/reports/sales?year=" + LocalDate.now().getYear()
                + "&month=" + LocalDate.now().getMonthValue();
        mvc.perform(get(sales)).andExpect(status().isUnauthorized());
        mvc.perform(get(sales).session(session(2L, "CUSTOMER"))).andExpect(status().isForbidden());

        MockHttpSession admin = session(1L, "ADMIN");
        mvc.perform(get(sales).session(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lines").isArray())
                .andExpect(jsonPath("$.vehiclesSold").isNumber());
        mvc.perform(get("/api/admin/reports/usage").session(admin)
                        .param("from", LocalDate.now().minusDays(60).toString())
                        .param("to", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.views", greaterThan(0)));
        mvc.perform(get("/api/admin/reports/usage").session(admin)
                        .param("from", "2026-12-31").param("to", "2026-01-01"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/dashboard").session(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.funnel.VIEW").exists())
                .andExpect(jsonPath("$.batteryHealthByVehicle").exists());
        mvc.perform(get("/api/admin/dashboard").session(session(2L, "CUSTOMER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("UC-18 event tracking accepts valid events and rejects bad ones")
    void eventTracking() throws Exception {
        mvc.perform(post("/api/events").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventType\":\"VIEW\",\"vehicleId\":1}"))
                .andExpect(status().isAccepted());
        mvc.perform(post("/api/events").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventType\":\"VIEW\",\"vehicleId\":99999}"))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/events").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventType\":\"DANCE\"}"))
                .andExpect(status().isBadRequest());
    }

    private static MockHttpSession session(Long userId, String role) {
        MockHttpSession s = new MockHttpSession();
        s.setAttribute(SessionAuth.USER_ID, userId);
        s.setAttribute(SessionAuth.ROLE, role);
        return s;
    }
}