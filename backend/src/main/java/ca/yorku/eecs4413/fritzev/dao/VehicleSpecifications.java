package ca.yorku.eecs4413.fritzev.dao;

import ca.yorku.eecs4413.fritzev.domain.Vehicle;
import ca.yorku.eecs4413.fritzev.domain.VehicleHistory;
import ca.yorku.eecs4413.fritzev.domain.VehicleStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

// Builds the catalogue filter (UC-05) as a JPA Criteria query.
public final class VehicleSpecifications {

    private VehicleSpecifications() { }

    // Turns the (already validated) filter into one WHERE clause.
    public static Specification<Vehicle> matching(VehicleFilter f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.notEqual(root.get("status"), VehicleStatus.SOLD));   // only sellable vehicles are listed
            if (f.brand() != null && !f.brand().isBlank()) {
                p.add(cb.equal(cb.lower(root.get("brand")), f.brand().trim().toLowerCase()));
            }
            if (f.shape() != null) p.add(cb.equal(root.get("shape"), f.shape()));
            if (f.condition() != null) p.add(cb.equal(root.get("vehicleCondition"), f.condition()));
            if (f.modelYear() != null) p.add(cb.equal(root.get("modelYear"), f.modelYear()));
            if (f.minPrice() != null) p.add(cb.greaterThanOrEqualTo(root.get("price"), f.minPrice()));
            if (f.maxPrice() != null) p.add(cb.lessThanOrEqualTo(root.get("price"), f.maxPrice()));
            if (Boolean.TRUE.equals(f.accidentFree())) {
                // New vehicles have no history row, so they count as accident free.
                Join<Vehicle, VehicleHistory> h = root.join("history", JoinType.LEFT);
                p.add(cb.or(cb.isNull(h.get("id")),
                        cb.and(cb.equal(h.get("accidentCount"), 0), cb.isFalse(h.get("damageReported")))));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    // Sort keys accepted by GET /api/vehicles?sort=...
    public static Sort sortFor(String key) {
        if (key == null || key.isBlank()) return Sort.by("id");
        return switch (key) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price").and(Sort.by("id"));
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price").and(Sort.by("id"));
            case "mileage_asc" -> Sort.by(Sort.Direction.ASC, "mileageKm").and(Sort.by("id"));
            case "mileage_desc" -> Sort.by(Sort.Direction.DESC, "mileageKm").and(Sort.by("id"));
            case "year_desc" -> Sort.by(Sort.Direction.DESC, "modelYear").and(Sort.by("id"));
            default -> throw new IllegalArgumentException(
                    "Unknown sort '" + key + "'. Use price_asc, price_desc, mileage_asc, mileage_desc or year_desc.");
        };
    }
}
