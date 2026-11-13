package ca.yorku.eecs4413.fritzev.domain; 

import jakarta.persistence.*; 

import java.math.BigDecimal; 
import java.time.LocalDate; 
import java.time.LocalDateTime; 
import java.util.*; 

// A new or pre-owned EV in the catalogue (M5). 
@Entity 
@Table(name = "vehicles") 
public class Vehicle { 

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    private String brand; 

    private String model; 

    private String trimName; 

    private int modelYear; 

    @Enumerated(EnumType.STRING) 
    private Shape shape; 

    @Enumerated(EnumType.STRING) 
    private VehicleCondition vehicleCondition; 

    private BigDecimal price; 

    private BigDecimal dealPrice; 

    private int mileageKm; 

    private int rangeKm; 

    private BigDecimal batteryKwh; 

    @Column(name = "energy_kwh_per_100km") 
    private BigDecimal energyKwhPer100km; 

    private Integer batteryHealthPct; 

    private String drivetrain; 

    private Integer seats; 

    private String description; 

    private String imageUrl; 

    private int quantity; 

    @Enumerated(EnumType.STRING) 
    private VehicleStatus status = VehicleStatus.AVAILABLE; 

    @OneToOne(mappedBy = "vehicle", fetch = FetchType.LAZY) 
    private VehicleHistory history; 

    @ManyToMany(fetch = FetchType.LAZY) 
    @JoinTable(name = "vehicle_options", 
            joinColumns = @JoinColumn(name = "vehicle_id"), 
            inverseJoinColumns = @JoinColumn(name = "option_id")) 
    private Set<CustomizationOption> options = new HashSet<>(); 

    @Column(insertable = false, updatable = false) 
    private LocalDateTime createdAt; 

    public Vehicle() { } 

    public Long getId() { return id; } 
    public void setId(Long id) { this.id = id; } 

    public String getBrand() { return brand; } 
    public void setBrand(String brand) { this.brand = brand; } 

    public String getModel() { return model; } 
    public void setModel(String model) { this.model = model; } 

    public String getTrimName() { return trimName; } 
    public void setTrimName(String trimName) { this.trimName = trimName; } 

    public int getModelYear() { return modelYear; } 
    public void setModelYear(int modelYear) { this.modelYear = modelYear; } 

    public Shape getShape() { return shape; } 
    public void setShape(Shape shape) { this.shape = shape; } 

    public VehicleCondition getVehicleCondition() { return vehicleCondition; } 
    public void setVehicleCondition(VehicleCondition vehicleCondition) { this.vehicleCondition = vehicleCondition; } 

    public BigDecimal getPrice() { return price; } 
    public void setPrice(BigDecimal price) { this.price = price; } 

    public BigDecimal getDealPrice() { return dealPrice; } 
    public void setDealPrice(BigDecimal dealPrice) { this.dealPrice = dealPrice; } 

    public int getMileageKm() { return mileageKm; } 
    public void setMileageKm(int mileageKm) { this.mileageKm = mileageKm; } 

    public int getRangeKm() { return rangeKm; } 
    public void setRangeKm(int rangeKm) { this.rangeKm = rangeKm; } 

    public BigDecimal getBatteryKwh() { return batteryKwh; } 
    public void setBatteryKwh(BigDecimal batteryKwh) { this.batteryKwh = batteryKwh; } 

    public BigDecimal getEnergyKwhPer100km() { return energyKwhPer100km; } 
    public void setEnergyKwhPer100km(BigDecimal energyKwhPer100km) { this.energyKwhPer100km = energyKwhPer100km; } 

    public Integer getBatteryHealthPct() { return batteryHealthPct; } 
    public void setBatteryHealthPct(Integer batteryHealthPct) { this.batteryHealthPct = batteryHealthPct; } 

    public String getDrivetrain() { return drivetrain; } 
    public void setDrivetrain(String drivetrain) { this.drivetrain = drivetrain; } 

    public Integer getSeats() { return seats; } 
    public void setSeats(Integer seats) { this.seats = seats; } 

    public String getDescription() { return description; } 
    public void setDescription(String description) { this.description = description; } 

    public String getImageUrl() { return imageUrl; } 
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; } 

    public int getQuantity() { return quantity; } 
    public void setQuantity(int quantity) { this.quantity = quantity; } 

    public VehicleStatus getStatus() { return status; } 
    public void setStatus(VehicleStatus status) { this.status = status; } 

    public VehicleHistory getHistory() { return history; } 
    public void setHistory(VehicleHistory history) { this.history = history; } 

    public Set<CustomizationOption> getOptions() { return options; } 
    public void setOptions(Set<CustomizationOption> options) { this.options = options; } 

    public LocalDateTime getCreatedAt() { return createdAt; } 
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; } 
} 
