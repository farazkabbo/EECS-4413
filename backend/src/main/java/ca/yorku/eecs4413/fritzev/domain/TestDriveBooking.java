package ca.yorku.eecs4413.fritzev.domain; 

import jakarta.persistence.*; 

import java.math.BigDecimal; 
import java.time.LocalDate; 
import java.time.LocalDateTime; 
import java.util.*; 

// A booking of one slot. 
@Entity 
@Table(name = "test_drive_bookings") 
public class TestDriveBooking { 

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    @OneToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "slot_id") 
    private TestDriveSlot slot; 

    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "user_id") 
    private User user; 

    private String address; 

    @Enumerated(EnumType.STRING) 
    private BookingStatus status = BookingStatus.BOOKED; 

    @Column(insertable = false, updatable = false) 
    private LocalDateTime createdAt; 

    public TestDriveBooking() { } 

    public Long getId() { return id; } 
    public void setId(Long id) { this.id = id; } 

    public TestDriveSlot getSlot() { return slot; } 
    public void setSlot(TestDriveSlot slot) { this.slot = slot; } 

    public User getUser() { return user; } 
    public void setUser(User user) { this.user = user; } 

    public String getAddress() { return address; } 
    public void setAddress(String address) { this.address = address; } 

    public BookingStatus getStatus() { return status; } 
    public void setStatus(BookingStatus status) { this.status = status; } 

    public LocalDateTime getCreatedAt() { return createdAt; } 
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; } 
} 
