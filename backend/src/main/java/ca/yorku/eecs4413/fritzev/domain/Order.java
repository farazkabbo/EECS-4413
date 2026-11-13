package ca.yorku.eecs4413.fritzev.domain; 

import jakarta.persistence.*; 

import java.math.BigDecimal; 
import java.time.LocalDate; 
import java.time.LocalDateTime; 
import java.util.*; 

// A placed order (M8). 
@Entity 
@Table(name = "orders") 
public class Order { 

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "user_id") 
    private User user; 

    @Enumerated(EnumType.STRING) 
    private OrderStatus status = OrderStatus.PENDING; 

    private BigDecimal total; 

    private String shipStreet; 

    private String shipCity; 

    private String shipProvince; 

    private String shipCountry; 

    private String shipPostalCode; 

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) 
    private List<OrderItem> items = new ArrayList<>(); 

    @Column(updatable = false) 
    private LocalDateTime createdAt = LocalDateTime.now(); 

    public Order() { } 

    public Long getId() { return id; } 
    public void setId(Long id) { this.id = id; } 

    public User getUser() { return user; } 
    public void setUser(User user) { this.user = user; } 

    public OrderStatus getStatus() { return status; } 
    public void setStatus(OrderStatus status) { this.status = status; } 

    public BigDecimal getTotal() { return total; } 
    public void setTotal(BigDecimal total) { this.total = total; } 

    public String getShipStreet() { return shipStreet; } 
    public void setShipStreet(String shipStreet) { this.shipStreet = shipStreet; } 

    public String getShipCity() { return shipCity; } 
    public void setShipCity(String shipCity) { this.shipCity = shipCity; } 

    public String getShipProvince() { return shipProvince; } 
    public void setShipProvince(String shipProvince) { this.shipProvince = shipProvince; } 

    public String getShipCountry() { return shipCountry; } 
    public void setShipCountry(String shipCountry) { this.shipCountry = shipCountry; } 

    public String getShipPostalCode() { return shipPostalCode; } 
    public void setShipPostalCode(String shipPostalCode) { this.shipPostalCode = shipPostalCode; } 

    public List<OrderItem> getItems() { return items; } 
    public void setItems(List<OrderItem> items) { this.items = items; } 

    public LocalDateTime getCreatedAt() { return createdAt; } 
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; } 
} 
