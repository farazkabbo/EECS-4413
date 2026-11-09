package ca.yorku.eecs4413.fritzev.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

// Exterior colour, interior colour, interior fabric or wheel option (M6, UC-08).
@Entity
@Table(name = "customization_options")
public class CustomizationOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OptionCategory category;

    private String name;

    private String hexCode;

    private BigDecimal priceDelta;

    public CustomizationOption() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OptionCategory getCategory() { return category; }
    public void setCategory(OptionCategory category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHexCode() { return hexCode; }
    public void setHexCode(String hexCode) { this.hexCode = hexCode; }

    public BigDecimal getPriceDelta() { return priceDelta; }
    public void setPriceDelta(BigDecimal priceDelta) { this.priceDelta = priceDelta; }
}
