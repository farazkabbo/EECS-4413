package ca.yorku.eecs4413.fritzev.domain; 

import jakarta.persistence.*; 

import java.math.BigDecimal; 
import java.time.LocalDate; 
import java.time.LocalDateTime; 
import java.util.*; 

// One configured vehicle line in a cart. 
@Entity 
@Table(name = "cart_items") 
public class CartItem { 

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "cart_id") 
    private Cart cart; 

    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "vehicle_id") 
    private Vehicle vehicle; 

    private int quantity; 

    private BigDecimal unitPrice; 

    @ManyToMany(fetch = FetchType.LAZY) 
    @JoinTable(name = "cart_item_options", 
            joinColumns = @JoinColumn(name = "cart_item_id"), 
            inverseJoinColumns = @JoinColumn(name = "option_id")) 
    private Set<CustomizationOption> options = new HashSet<>(); 

    public CartItem() { } 

    public Long getId() { return id; } 
    public void setId(Long id) { this.id = id; } 

    public Cart getCart() { return cart; } 
    public void setCart(Cart cart) { this.cart = cart; } 

    public Vehicle getVehicle() { return vehicle; } 
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; } 

    public int getQuantity() { return quantity; } 
    public void setQuantity(int quantity) { this.quantity = quantity; } 

    public BigDecimal getUnitPrice() { return unitPrice; } 
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; } 

    public Set<CustomizationOption> getOptions() { return options; } 
    public void setOptions(Set<CustomizationOption> options) { this.options = options; } 
} 
