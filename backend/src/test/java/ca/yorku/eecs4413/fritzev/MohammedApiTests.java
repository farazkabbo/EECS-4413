package ca.yorku.eecs4413.fritzev;

import ca.yorku.eecs4413.fritzev.controller.SessionAuth;
import ca.yorku.eecs4413.fritzev.dao.VehicleDAO;
import ca.yorku.eecs4413.fritzev.domain.VehicleStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Mohammed's D1 test cases T09, T10, T12 run as real tests (D2 "server-side test coverage"), plus hot deals and input validation.
@SpringBootTest
@AutoConfigureMockMvc
class MohammedApiTests {

    @Autowired MockMvc mvc;
    @Autowired VehicleDAO vehicleDAO;

    // ---------------------------------------------------------------- T09
    @Test
    @DisplayName("T09 vehicle list endpoint returns exactly the sellable vehicles stored in the DB")
    void t09_listMatchesDatabase() throws Exception {
        long sellable = vehicleDAO.findAll().stream().filter(v -> v.getStatus() != VehicleStatus.SOLD).count();
        mvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize((int) sellable)))
                .andExpect(jsonPath("$[*].status", everyItem(not("SOLD"))));
    }

    @Test
    @DisplayName("T09 vehicle detail endpoint matches the stored record, incl. history and options")
    void t09_detailMatchesDatabase() throws Exception {
        var v = vehicleDAO.findById(10L).orElseThrow();
        mvc.perform(get("/api/vehicles/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value(v.getBrand()))
                .andExpect(jsonPath("$.model").value(v.getModel()))
                .andExpect(jsonPath("$.mileageKm").value(v.getMileageKm()))
                .andExpect(jsonPath("$.history.damageReported").value(true))
                .andExpect(jsonPath("$.options.EXTERIOR_COLOUR", hasSize(1)));
    }

    @Test
    @DisplayName("T09 controlled errors: unknown id -> 404, bad id -> 400, unknown route -> 404 (JSON, no stack trace)")
    void t09_controlledErrors() throws Exception {
        mvc.perform(get("/api/vehicles/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("not found")));
        mvc.perform(get("/api/vehicles/abc")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/vehicles/-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/does-not-exist")).andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------------- T10
    @Test
    @DisplayName("T10 SQL-injection-like filter input is rejected and the inventory is unchanged")
    void t10_sqlInjectionIsHarmless() throws Exception {
        long before = vehicleDAO.count();
        mvc.perform(get("/api/vehicles").param("brand", "Tesla' OR '1'='1"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/vehicles").param("brand", "x'; DROP TABLE vehicles; --"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/vehicles").param("sort", "price; DELETE FROM vehicles"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/vehicles").param("year", "2024 OR 1=1"))
                .andExpect(status().isBadRequest());
        org.junit.jupiter.api.Assertions.assertEquals(before, vehicleDAO.count());
        // and a legitimate filter still works afterwards
        mvc.perform(get("/api/vehicles").param("brand", "tesla").param("sort", "price_asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price").value(52900.00));
    }

    @Test
    @DisplayName("UC-05 filters: shape (case-insensitive), condition and accident-free history")
    void filters() throws Exception {
        mvc.perform(get("/api/vehicles").param("shape", "suv").param("condition", "USED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].shape", everyItem(is("SUV"))))
                .andExpect(jsonPath("$[*].condition", everyItem(is("USED"))));
        mvc.perform(get("/api/vehicles").param("condition", "USED").param("accidentFree", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", not(hasItems(8, 10))));
        mvc.perform(get("/api/vehicles").param("shape", "BOAT")).andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------------- T12
    @Test
    @DisplayName("T12 admin report endpoints: anonymous -> 401, customer -> 403, admin passes the guard")
    void t12_adminOnly() throws Exception {
        String url = "/api/admin/reports/sales";
        mvc.perform(get(url).param("year", "2026").param("month", "10"))
                .andExpect(status().isUnauthorized());

        MockHttpSession customer = session(2L, "CUSTOMER");
        mvc.perform(get(url).param("year", "2026").param("month", "10").session(customer))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/dashboard").session(customer)).andExpect(status().isForbidden());

        // The admin gets past the guard. The report itself is Ronak's (PB-29): 501 until implemented, then 200.
        MockHttpSession admin = session(1L, "ADMIN");
        mvc.perform(get(url).param("year", "2026").param("month", "10").session(admin))
                .andExpect(status().is(anyOf(is(200), is(501))));
    }

    // ---------------------------------------------------------------- hot deals
    @Test
    @DisplayName("Hot deals: only discounted available vehicles, biggest % discount first, limit respected")
    void hotDeals() throws Exception {
        mvc.perform(get("/api/vehicles/hot-deals").param("limit", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(7))            // Bolt EUV: 12% off
                .andExpect(jsonPath("$[0].discountPercent").value(12))
                .andExpect(jsonPath("$[0].savings").value(3000.00));
        mvc.perform(get("/api/vehicles/hot-deals").param("limit", "0")).andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------------- validation
    @Test
    @DisplayName("Bad input never crashes: invalid JSON and invalid fields give 400 with field errors")
    void validation() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/calculators/loan")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json").content("{not json"))
                .andExpect(status().isBadRequest());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/calculators/loan")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json").content("{\"price\":-5,\"months\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.price").exists())
                .andExpect(jsonPath("$.fieldErrors.months").exists());
    }

    private static MockHttpSession session(Long userId, String role) {
        MockHttpSession s = new MockHttpSession();
        s.setAttribute(SessionAuth.USER_ID, userId);
        s.setAttribute(SessionAuth.ROLE, role);
        return s;
    }
}
