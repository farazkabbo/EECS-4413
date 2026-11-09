package ca.yorku.eecs4413.fritzev.controller;

import ca.yorku.eecs4413.fritzev.controller.dto.DrivingProfileRequest;
import ca.yorku.eecs4413.fritzev.controller.dto.VehicleMatchDto;
import ca.yorku.eecs4413.fritzev.service.recommendation.RecommendationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// UC-10 AI-Powered Recommendations (Deliverable 3).
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public List<VehicleMatchDto> recommend(@Valid @RequestBody DrivingProfileRequest profile,
                                           @RequestParam(defaultValue = "5") @Min(1) @Max(20) int max) {
        return recommendationService.recommend(profile, max);
    }
}
