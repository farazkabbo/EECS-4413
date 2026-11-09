package ca.yorku.eecs4413.fritzev.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

// Vehicle history report of a pre-owned EV (UC-05 filter, UC-06 details).
@Entity
@Table(name = "vehicle_history")
public class VehicleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private int accidentCount;

    private boolean damageReported;

    private int previousOwners;

    private int serviceRecords;

    private String reportSummary;

    private LocalDate reportDate;

    public VehicleHistory() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public int getAccidentCount() { return accidentCount; }
    public void setAccidentCount(int accidentCount) { this.accidentCount = accidentCount; }

    public boolean isDamageReported() { return damageReported; }
    public void setDamageReported(boolean damageReported) { this.damageReported = damageReported; }

    public int getPreviousOwners() { return previousOwners; }
    public void setPreviousOwners(int previousOwners) { this.previousOwners = previousOwners; }

    public int getServiceRecords() { return serviceRecords; }
    public void setServiceRecords(int serviceRecords) { this.serviceRecords = serviceRecords; }

    public String getReportSummary() { return reportSummary; }
    public void setReportSummary(String reportSummary) { this.reportSummary = reportSummary; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
}
