package ca.yorku.eecs4413.fritzev.service.review;

import ca.yorku.eecs4413.fritzev.controller.dto.ReviewDto;
import ca.yorku.eecs4413.fritzev.controller.dto.ReviewRequest;
import ca.yorku.eecs4413.fritzev.dao.ReviewDAO;
import ca.yorku.eecs4413.fritzev.dao.UserDAO;
import ca.yorku.eecs4413.fritzev.dao.VehicleDAO;
import ca.yorku.eecs4413.fritzev.domain.Review;
import ca.yorku.eecs4413.fritzev.exception.*;
import ca.yorku.eecs4413.fritzev.service.account.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Only verified owners can publish a review, and each owner may review a vehicle once.
@Service
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDAO reviewDAO;
    private final VehicleDAO vehicleDAO;
    private final UserDAO userDAO;
    private final AccountService accountService;

    public ReviewServiceImpl(ReviewDAO reviewDAO, VehicleDAO vehicleDAO, UserDAO userDAO,
                             AccountService accountService) {
        this.reviewDAO = reviewDAO;
        this.vehicleDAO = vehicleDAO;
        this.userDAO = userDAO;
        this.accountService = accountService;
    }

    @Override
    @Transactional
    public ReviewDto submitReview(Long userId, Long vehicleId, ReviewRequest request) {
        // Check ownership before saving anything so an unverified account cannot create a partial review.
        if (!accountService.isVerifiedOwner(userId)) throw new ForbiddenException("Verified EV ownership is required");
        var vehicle = vehicleDAO.findById(vehicleId).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        var user = userDAO.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (reviewDAO.existsByVehicleIdAndUserId(vehicleId, userId)) throw new ConflictException("You have already reviewed this vehicle");
        Review review = new Review();
        review.setVehicle(vehicle); review.setUser(user); review.setRating(request.rating()); review.setWinterRangeKm(request.winterRangeKm());
        review.setWinterRangeNotes(request.winterRangeNotes()); review.setHomeChargingNotes(request.homeChargingNotes());
        review.setSoftwareUpdateNotes(request.softwareUpdateNotes()); review.setOverallSatisfaction(request.overallSatisfaction()); review.setComment(request.comment());
        return toDto(reviewDAO.save(review));
    }

    @Override
    public List<ReviewDto> getReviews(Long vehicleId) {
        return reviewDAO.findByVehicleIdOrderByCreatedAtDesc(vehicleId).stream().map(ReviewServiceImpl::toDto).toList();
    }

    @Override
    public Double getAverageRating(Long vehicleId) {
        Double avg = reviewDAO.averageRating(vehicleId);
        return avg == null ? null : Math.round(avg * 10) / 10.0;
    }

    @Override
    public long countReviews(Long vehicleId) {
        return reviewDAO.countByVehicleId(vehicleId);
    }

    static ReviewDto toDto(Review r) {
        return new ReviewDto(r.getId(), r.getVehicle().getId(), r.getUser().getUsername(), r.getRating(),
                r.getWinterRangeKm(), r.getWinterRangeNotes(), r.getHomeChargingNotes(), r.getSoftwareUpdateNotes(),
                r.getOverallSatisfaction(), r.getComment(), r.getCreatedAt());
    }
}
