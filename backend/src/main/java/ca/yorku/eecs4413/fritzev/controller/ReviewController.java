package ca.yorku.eecs4413.fritzev.controller; 

import ca.yorku.eecs4413.fritzev.controller.dto.ReviewDto; 
import ca.yorku.eecs4413.fritzev.controller.dto.ReviewRequest; 
import ca.yorku.eecs4413.fritzev.service.review.ReviewService; 
import jakarta.servlet.http.HttpSession; 
import jakarta.validation.Valid; 
import jakarta.validation.constraints.Positive; 
import org.springframework.http.HttpStatus; 
import org.springframework.web.bind.annotation.*; 

import java.util.List; 

// UC-15 Write Review and Rating (verified EV owners only). 
@RestController 
@RequestMapping("/api/vehicles/{id}/reviews") 
public class ReviewController { 

    private final ReviewService reviewService; 

    public ReviewController(ReviewService reviewService) { 
        this.reviewService = reviewService; 
    } 

    @GetMapping 
    public List<ReviewDto> list(@PathVariable @Positive Long id) { 
        return reviewService.getReviews(id); 
    } 

    @PostMapping 
    @ResponseStatus(HttpStatus.CREATED) 
    public ReviewDto submit(@PathVariable @Positive Long id, @Valid @RequestBody ReviewRequest request, 
                            HttpSession session) { 
        return reviewService.submitReview(SessionAuth.requireUser(session), id, request); 
    } 
} 
