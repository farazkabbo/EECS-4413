package ca.yorku.eecs4413.fritzev.service.finance; 

import ca.yorku.eecs4413.fritzev.controller.dto.*; 
import ca.yorku.eecs4413.fritzev.dao.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 
import ca.yorku.eecs4413.fritzev.exception.*; 

import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 

// M9 Finance & Savings Calculator (D1 interfaces ILoanCalculator + ISavingsEstimator). 
public interface FinanceService { 

    // UC-09 
    LoanQuoteDto calculateMonthlyPayment(LoanRequest request); 

    // UC-20 
    SavingsReportDto estimateSavings(Long vehicleId, SavingsRequest request); 
} 
