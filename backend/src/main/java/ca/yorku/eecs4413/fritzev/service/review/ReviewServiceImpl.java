package ca.yorku.eecs4413.fritzev.service.review;

import ca.yorku.eecs4413.fritzev.controller.dto.ReviewDto;
import ca.yorku.eecs4413.fritzev.controller.dto.ReviewRequest;
import ca.yorku.eecs4413.fritzev.dao.ReviewDAO;
import ca.yorku.eecs4413.fritzev.dao.UserDAO;
import ca.yorku.eecs4413.fritzev.dao.VehicleDAO;
import ca.yorku.eecs4413.fritzev.domain.Review;
import ca.yorku.eecs4413.fritzev.exception.NotImplementedYetException;
import ca.yorku.eecs4413.fritzev.service.account.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// See ReviewService.
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
        // TODO(Zaaid, PB-26):
        throw new NotImplementedYetException("UC-15 ReviewService.submitReview is not implemented yet (owner: Zaaid, PB-26)");
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
