package ca.yorku.eecs4413.fritzev.service.finance;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import org.springframework.stereotype.Service;

// STUB - M9 Finance & Savings Calculator (D1 interfaces ILoanCalculator + ISavingsEstimator).
@Service
public class FinanceServiceImpl implements FinanceService {

    private final VehicleDAO vehicleDAO;

    public FinanceServiceImpl(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    @Override
    public LoanQuoteDto calculateMonthlyPayment(LoanRequest request) {
        // TODO(Tamjeed): downPayment > price -> BadRequestException
        throw new NotImplementedYetException("UC-09 FinanceService.calculateMonthlyPayment is not implemented yet (owner: Tamjeed, PB-23)");
    }

    @Override
    public SavingsReportDto estimateSavings(Long vehicleId, SavingsRequest request) {
        // TODO(Tamjeed): use vehicle.getEnergyKwhPer100km(); list the assumptions in the report
        throw new NotImplementedYetException("UC-20 FinanceService.estimateSavings is not implemented yet (owner: Tamjeed, PB-23)");
    }
}
