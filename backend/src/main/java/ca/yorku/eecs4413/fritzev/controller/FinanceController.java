package ca.yorku.eecs4413.fritzev.controller;

import ca.yorku.eecs4413.fritzev.controller.dto.LoanQuoteDto;
import ca.yorku.eecs4413.fritzev.controller.dto.LoanRequest;
import ca.yorku.eecs4413.fritzev.controller.dto.SavingsReportDto;
import ca.yorku.eecs4413.fritzev.controller.dto.SavingsRequest;
import ca.yorku.eecs4413.fritzev.service.finance.FinanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;

// UC-09 Loan Calculator and UC-20 Fuel Savings / CO2 estimator (distinguished use case).
@RestController
@RequestMapping("/api")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @PostMapping("/calculators/loan")
    public LoanQuoteDto loan(@Valid @RequestBody LoanRequest request) {
        return financeService.calculateMonthlyPayment(request);
    }

    @PostMapping("/vehicles/{id}/savings-estimate")
    public SavingsReportDto savings(@PathVariable @Positive Long id, @Valid @RequestBody SavingsRequest request) {
        return financeService.estimateSavings(id, request);
    }
}
