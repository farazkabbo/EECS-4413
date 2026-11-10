package ca.yorku.eecs4413.fritzev;

import ca.yorku.eecs4413.fritzev.dao.VehicleDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TamjeedCatalogTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    VehicleDAO vehicleDAO;

    @Test
    void catalogFiltersSortAndDropdowns() throws Exception {
        mvc.perform(get("/api/vehicles").param("category", "USED").param("minYear", "2022")
                        .param("maxYear", "2024").param("noAccidents", "true").param("sort", "mileage")
                        .param("dir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].condition", everyItem(is("USED"))))
                .andExpect(jsonPath("$[*].mileageKm", contains(21500, 25600, 38200, 54000)));
        mvc.perform(get("/api/vehicles").param("sort", "price").param("dir", "sideways"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/vehicles/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItem("Tesla")));
        mvc.perform(get("/api/vehicles/shapes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItem("SUV")));
    }

    @Test
    @Transactional
    void newVehicleMileageSortTreatsMileageAsZero() throws Exception {
        var newVehicle = vehicleDAO.findById(1L).orElseThrow();
        newVehicle.setMileageKm(98000);
        vehicleDAO.saveAndFlush(newVehicle);
        mvc.perform(get("/api/vehicles").param("sort", "mileage").param("dir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void compareRequiresTwoToFourExistingDistinctVehicles() throws Exception {
        mvc.perform(get("/api/vehicles/compare").param("ids", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicles", hasSize(2)))
                .andExpect(jsonPath("$.vehicles[0].rangeKm").value(584))
                .andExpect(jsonPath("$.vehicles[1].mileageKm").value(21500));
        mvc.perform(get("/api/vehicles/compare").param("ids", "1"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/vehicles/compare").param("ids", "1,99999"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/vehicles/compare").param("ids", "1,1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void configurationValidatesBelongingDuplicatesAndExclusiveCategories() throws Exception {
        mvc.perform(post("/api/vehicles/1/configuration").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"optionIds\":[1,6]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basePrice").value(54990))
                .andExpect(jsonPath("$.total").value(54990));
        mvc.perform(post("/api/vehicles/1/configuration").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"optionIds\":[1,2]}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/vehicles/1/configuration").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"optionIds\":[1,1]}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/vehicles/1/configuration").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"optionIds\":[5]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loanAndSavingsCalculationsReturnRoundedValues() throws Exception {
        mvc.perform(post("/api/calculators/loan").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"price\":12000,\"downPayment\":0,\"annualRatePercent\":0,\"months\":12}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPayment").value(1000.00))
                .andExpect(jsonPath("$.totalPaid").value(12000.00))
                .andExpect(jsonPath("$.totalInterest").value(0.00));
        mvc.perform(post("/api/calculators/loan").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"price\":12000,\"downPayment\":12000,\"annualRatePercent\":0,\"months\":12}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyPayment").value(0.00));
        mvc.perform(post("/api/calculators/loan").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"price\":12000,\"downPayment\":13000,\"annualRatePercent\":0,\"months\":12}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/vehicles/1/savings").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"annualKm\":20000,\"gasolineLPer100km\":8,\"gasolinePricePerL\":1.5,\"electricityPricePerKwh\":0.2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yearlyGasolineCost").value(2400.00))
                .andExpect(jsonPath("$.yearlyChargingCost").value(596.00))
                .andExpect(jsonPath("$.yearlySavings").value(1804.00))
                .andExpect(jsonPath("$.fiveYearSavings").value(9020.00))
                .andExpect(jsonPath("$.co2AvoidedKg").value(3606.60))
                .andExpect(jsonPath("$.assumptions", hasSize(5)));
        mvc.perform(post("/api/vehicles/99999/savings").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"annualKm\":10000,\"gasolineLPer100km\":8,\"gasolinePricePerL\":1.5,\"electricityPricePerKwh\":0.2}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void plainHtmlPagesRender() throws Exception {
        mvc.perform(get("/catalog")).andExpect(status().isOk()).andExpect(view().name("catalog"));
        mvc.perform(get("/vehicles/10")).andExpect(status().isOk()).andExpect(view().name("vehicle"));
        mvc.perform(get("/compare").param("ids", "1,2"))
                .andExpect(status().isOk()).andExpect(view().name("compare"));
        mvc.perform(get("/vehicles/1/configure")).andExpect(status().isOk()).andExpect(view().name("configure"));
        mvc.perform(get("/vehicles/1/loan")).andExpect(status().isOk()).andExpect(view().name("loan"));
        mvc.perform(get("/vehicles/1/savings")).andExpect(status().isOk()).andExpect(view().name("savings"));
    }

    @Test
    void plainHtmlCalculatorAndConfigurationFormsShowResults() throws Exception {
        mvc.perform(post("/vehicles/1/configure").with(csrf()).param("optionIds", "1", "6"))
                .andExpect(status().isOk())
                .andExpect(view().name("configure"))
                .andExpect(model().attributeExists("configuration"));
        mvc.perform(post("/vehicles/1/loan").with(csrf()).param("downPayment", "1000")
                        .param("annualRatePercent", "0").param("months", "12"))
                .andExpect(status().isOk())
                .andExpect(view().name("loan"))
                .andExpect(model().attributeExists("quote"));
        mvc.perform(post("/vehicles/1/savings").with(csrf()).param("annualKm", "12000")
                        .param("gasolineLPer100km", "8").param("gasolinePricePerL", "1.5")
                        .param("electricityPricePerKwh", "0.2"))
                .andExpect(status().isOk())
                .andExpect(view().name("savings"))
                .andExpect(model().attributeExists("report"));
    }
}
