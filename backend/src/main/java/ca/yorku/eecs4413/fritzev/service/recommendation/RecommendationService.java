package ca.yorku.eecs4413.fritzev.service.recommendation;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

// M10 Recommendation Engine (D1 interface IRecommendation) - Deliverable 3.
public interface RecommendationService {

    // UC-10
    List<VehicleMatchDto> recommend(DrivingProfileRequest profile, int max);
}
