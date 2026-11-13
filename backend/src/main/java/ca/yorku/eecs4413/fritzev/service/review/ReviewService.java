package ca.yorku.eecs4413.fritzev.service.review; 

import ca.yorku.eecs4413.fritzev.controller.dto.ReviewDto; 
import ca.yorku.eecs4413.fritzev.controller.dto.ReviewRequest; 

import java.util.List; 

// M12 Review Manager (D1 interface IReview). 
public interface ReviewService { 

    // UC-15 
    ReviewDto submitReview(Long userId, Long vehicleId, ReviewRequest request); 

    // Published reviews of a vehicle, newest first. 
    List<ReviewDto> getReviews(Long vehicleId); 

    // Average star rating rounded to one decimal, or null when there are no reviews. 
    Double getAverageRating(Long vehicleId); 

    long countReviews(Long vehicleId); 
} 
