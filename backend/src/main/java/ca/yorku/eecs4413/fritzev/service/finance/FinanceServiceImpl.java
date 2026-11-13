package ca.yorku.eecs4413.fritzev.service.finance; 

import ca.yorku.eecs4413.fritzev.controller.dto.*; 
import ca.yorku.eecs4413.fritzev.dao.VehicleDAO; 
import ca.yorku.eecs4413.fritzev.domain.Vehicle; 
import ca.yorku.eecs4413.fritzev.exception.BadRequestException; 
import ca.yorku.eecs4413.fritzev.exception.ResourceNotFoundException; 

import java.math.BigDecimal; 
import java.math.RoundingMode; 
import java.util.List; 

import org.springframework.beans.factory.annotation.Value; 
import org.springframework.stereotype.Service; 

@Service 
public class FinanceServiceImpl implements FinanceService { 

    private static final BigDecimal ZERO = BigDecimal.ZERO; 
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100); 
    private static final BigDecimal GASOLINE_CO2_KG_PER_LITRE = new BigDecimal("2.31"); 
    private static final int MONEY_SCALE = 2; 
    private static final int CALCULATION_SCALE = 12; 

    private final VehicleDAO vehicleDAO; 
    private final BigDecimal gridCo2KgPerKwh; 

    public FinanceServiceImpl(VehicleDAO vehicleDAO, 
                              @Value("${fritzev.grid-co2-kg-per-kwh:0.03}") BigDecimal gridCo2KgPerKwh) { 
        this.vehicleDAO = vehicleDAO; 
        this.gridCo2KgPerKwh = gridCo2KgPerKwh; 
    } 

    @Override 
    public LoanQuoteDto calculateMonthlyPayment(LoanRequest request) { 
        if (request.downPayment().compareTo(request.price()) > 0) { 
            throw new BadRequestException("downPayment must not be greater than price"); 
        } 
        BigDecimal principal = request.price().subtract(request.downPayment()); 
        BigDecimal payment; 
        if (principal.signum() == 0) { 
            payment = ZERO; 
        } else { 
            BigDecimal monthlyRate = request.annualRatePercent() 
                    .divide(ONE_HUNDRED.multiply(BigDecimal.valueOf(12)), CALCULATION_SCALE, RoundingMode.HALF_UP); 
            if (monthlyRate.signum() == 0) { 
                payment = principal.divide(BigDecimal.valueOf(request.months()), CALCULATION_SCALE, 
                        RoundingMode.HALF_UP); 
            } else { 
                BigDecimal factor = BigDecimal.ONE.add(monthlyRate).pow(request.months()); 
                payment = principal.multiply(monthlyRate) 
                        .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.divide(factor, CALCULATION_SCALE, 
                                RoundingMode.HALF_UP)), CALCULATION_SCALE, RoundingMode.HALF_UP); 
            } 
        } 
        payment = money(payment); 
        BigDecimal totalPaid = money(payment.multiply(BigDecimal.valueOf(request.months()))); 
        BigDecimal totalInterest = money(totalPaid.subtract(principal)); 
        return new LoanQuoteDto(money(principal), request.months(), payment, totalInterest, totalPaid); 
    } 

    @Override 
    // AI-use disclosure: AI assisted with drafting this method; it was reviewed and integrated into this project.
    // Reconstructed prompt (approximate, not the original wording): "Implement UC-20 savings estimation:
    // compare annual gasoline and EV charging costs, estimate gasoline and EV CO2 emissions, and return
    // yearly/five-year savings and the assumptions using the vehicle's energy consumption."
    // This section of AI generated code was by Tamjeed Shariff
    public SavingsReportDto estimateSavings(Long vehicleId, SavingsRequest request) { 
        // Load the EV so the estimate uses its stored energy consumption, not a value supplied by the caller.
        Vehicle vehicle = vehicleDAO.findById(vehicleId) 
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle " + vehicleId + " not found")); 

        // Convert the annual distance into the number of 100-km units used by the consumption ratings.
        BigDecimal kmBlocks = request.annualKm().divide(ONE_HUNDRED, CALCULATION_SCALE, RoundingMode.HALF_UP); 

        // Gasoline cost = distance units * gasoline litres per 100 km * gasoline price per litre.
        BigDecimal gasolineCost = money(kmBlocks.multiply(request.gasolineLPer100km()) 
                .multiply(request.gasolinePricePerL())); 

        // EV charging cost uses the vehicle's kWh per 100 km and the supplied electricity price.
        BigDecimal electricityCost = money(kmBlocks.multiply(vehicle.getEnergyKwhPer100km()) 
                .multiply(request.electricityPricePerKwh())); 

        // A positive result means the EV costs less to run; a negative result means it costs more.
        BigDecimal yearlySavings = money(gasolineCost.subtract(electricityCost)); 

        // Estimate gasoline emissions with the project's fixed 2.31 kg CO2 per litre factor.
        BigDecimal co2GasolineKg = money(kmBlocks.multiply(request.gasolineLPer100km()) 
                .multiply(GASOLINE_CO2_KG_PER_LITRE)); 

        // Estimate EV emissions using the configurable electricity-grid emissions factor.
        BigDecimal co2ElectricKg = money(kmBlocks.multiply(vehicle.getEnergyKwhPer100km()) 
                .multiply(gridCo2KgPerKwh)); 

        // Subtract EV emissions from gasoline emissions to estimate the CO2 avoided each year.
        BigDecimal co2AvoidedKg = money(co2GasolineKg.subtract(co2ElectricKg)); 

        // Include the formulas and the five-year constant-use assumption with the result for transparency.
        List<String> assumptions = List.of( 
                "Gasoline cost = annualKm / 100 * gasolineLPer100km * gasolinePricePerL.", 
                "Electricity cost = annualKm / 100 * vehicle energy kWh/100km * electricityPricePerKwh.", 
                "Yearly savings = gasoline cost - electricity cost; five-year savings assumes unchanged prices and use.", 
                "Gasoline emissions = annualKm / 100 * gasolineLPer100km * 2.31 kg CO2/L.", 
                "EV emissions = annualKm / 100 * vehicle energy kWh/100km * " + gridCo2KgPerKwh + " kg CO2/kWh."); 
        return new SavingsReportDto(vehicleId, gasolineCost, electricityCost, yearlySavings, 
                money(yearlySavings.multiply(BigDecimal.valueOf(5))), co2GasolineKg, co2ElectricKg, 
                co2AvoidedKg, assumptions); 
    } 
    // End of the AI-assisted UC-20 savings-estimation method.

    private static BigDecimal money(BigDecimal amount) { 
        return amount.setScale(MONEY_SCALE, RoundingMode.HALF_UP); 
    } 
} 
