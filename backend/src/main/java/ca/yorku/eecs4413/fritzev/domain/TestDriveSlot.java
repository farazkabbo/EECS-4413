package ca.yorku.eecs4413.fritzev.domain; 

import jakarta.persistence.*; 

import java.math.BigDecimal; 
import java.time.LocalDate; 
import java.time.LocalDateTime; 
import java.util.*; 

// A bookable at-home or virtual test-drive time slot (M11, UC-11). 
@Entity 
@Table(name = "test_drive_slots") 
public class TestDriveSlot { 

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "vehicle_id") 
    private Vehicle vehicle; 

    @Enumerated(EnumType.STRING) 
    private DriveType driveType; 

    private LocalDateTime slotStart; 

    private int slotMinutes = 60; 

    public TestDriveSlot() { } 

    public Long getId() { return id; } 
    public void setId(Long id) { this.id = id; } 

    public Vehicle getVehicle() { return vehicle; } 
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; } 

    public DriveType getDriveType() { return driveType; } 
    public void setDriveType(DriveType driveType) { this.driveType = driveType; } 

    public LocalDateTime getSlotStart() { return slotStart; } 
    public void setSlotStart(LocalDateTime slotStart) { this.slotStart = slotStart; } 

    public int getSlotMinutes() { return slotMinutes; } 
    public void setSlotMinutes(int slotMinutes) { this.slotMinutes = slotMinutes; } 
} 
