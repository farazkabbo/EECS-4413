package ca.yorku.eecs4413.fritzev.domain; 

import jakarta.persistence.*; 

import java.math.BigDecimal; 
import java.time.LocalDate; 
import java.time.LocalDateTime; 
import java.util.*; 

// Structured review + 1-5 star rating by a verified EV owner (M12, UC-15). 
@Entity 
@Table(name = "reviews") 
public class Review { 

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "vehicle_id") 
    private Vehicle vehicle; 

    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "user_id") 
    private User user; 

    private int rating; 

    private Integer winterRangeKm; 

    private String winterRangeNotes; 

    private String homeChargingNotes; 

    private String softwareUpdateNotes; 

    private int overallSatisfaction; 

    private String comment; 

    @Column(insertable = false, updatable = false) 
    private LocalDateTime createdAt; 

    public Review() { } 

    public Long getId() { return id; } 
    public void setId(Long id) { this.id = id; } 

    public Vehicle getVehicle() { return vehicle; } 
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; } 

    public User getUser() { return user; } 
    public void setUser(User user) { this.user = user; } 

    public int getRating() { return rating; } 
    public void setRating(int rating) { this.rating = rating; } 

    public Integer getWinterRangeKm() { return winterRangeKm; } 
    public void setWinterRangeKm(Integer winterRangeKm) { this.winterRangeKm = winterRangeKm; } 

    public String getWinterRangeNotes() { return winterRangeNotes; } 
    public void setWinterRangeNotes(String winterRangeNotes) { this.winterRangeNotes = winterRangeNotes; } 

    public String getHomeChargingNotes() { return homeChargingNotes; } 
    public void setHomeChargingNotes(String homeChargingNotes) { this.homeChargingNotes = homeChargingNotes; } 

    public String getSoftwareUpdateNotes() { return softwareUpdateNotes; } 
    public void setSoftwareUpdateNotes(String softwareUpdateNotes) { this.softwareUpdateNotes = softwareUpdateNotes; } 

    public int getOverallSatisfaction() { return overallSatisfaction; } 
    public void setOverallSatisfaction(int overallSatisfaction) { this.overallSatisfaction = overallSatisfaction; } 

    public String getComment() { return comment; } 
    public void setComment(String comment) { this.comment = comment; } 

    public LocalDateTime getCreatedAt() { return createdAt; } 
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; } 
} 
