package ca.yorku.eecs4413.fritzev.service.recommendation; 

import ca.yorku.eecs4413.fritzev.controller.dto.*; 
import ca.yorku.eecs4413.fritzev.dao.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 
import ca.yorku.eecs4413.fritzev.exception.*; 

import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 

import org.springframework.stereotype.Service; 

// STUB - M10 Recommendation Engine (D1 interface IRecommendation) - Deliverable 3. 
@Service 
public class RecommendationServiceImpl implements RecommendationService { 

    private final VehicleDAO vehicleDAO; 

    public RecommendationServiceImpl(VehicleDAO vehicleDAO) { 
        this.vehicleDAO = vehicleDAO; 
    } 

    @Override 
    public List<VehicleMatchDto> recommend(DrivingProfileRequest profile, int max) { 
        // TODO(Tamjeed): score range vs commute, seats, budget, shape 
        throw new NotImplementedYetException("UC-10 RecommendationService.recommend is not implemented yet (owner: Tamjeed, PB-42)"); 
    } 
} 
