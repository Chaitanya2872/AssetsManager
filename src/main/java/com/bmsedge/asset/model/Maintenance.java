package com.bmsedge.asset.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "maintenance_records")
public class Maintenance {

    @Id
    @Column(name = "maintenance_id", nullable = false, unique = true, updatable = false)
    private String maintenanceId;

    @Column(name = "asset_id", nullable = false)
    private String assetId;

    @Column(name = "vendor_id")
    private String vendorId;

    @Column(name = "maintenance_type", nullable = false)
    private String maintenanceType; // PREVENTIVE, CORRECTIVE, PREDICTIVE, EMERGENCY

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MaintenanceStatus status = MaintenanceStatus.SCHEDULED;

    @Column(name = "priority", nullable = false)
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "work_performed", length = 2000)
    private String workPerformed;

    @Column(name = "parts_replaced", length = 1000)
    private String partsReplaced;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "technician_name")
    private String technicianName;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    @Column(name = "downtime_hours")
    private Double downtimeHours;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ======================
    // Constructors
    // ======================
    public Maintenance() {
    }

    public Maintenance(String assetId, String maintenanceType, LocalDate scheduledDate) {
        this.assetId = assetId;
        this.maintenanceType = maintenanceType;
        this.scheduledDate = scheduledDate;
    }

    // ======================
    // Lifecycle hooks
    // ======================
    @PrePersist
    protected void onCreate() {
        this.maintenanceId = "MNT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = MaintenanceStatus.SCHEDULED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ======================
    // Getters & Setters
    // ======================
    public String getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(String maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public MaintenanceStatus getStatus() {
        return status;
    }

    public void setStatus(MaintenanceStatus status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkPerformed() {
        return workPerformed;
    }

    public void setWorkPerformed(String workPerformed) {
        this.workPerformed = workPerformed;
    }

    public String getPartsReplaced() {
        return partsReplaced;
    }

    public void setPartsReplaced(String partsReplaced) {
        this.partsReplaced = partsReplaced;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    public Double getDowntimeHours() {
        return downtimeHours;
    }

    public void setDowntimeHours(Double downtimeHours) {
        this.downtimeHours = downtimeHours;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Maintenance{" +
                "maintenanceId='" + maintenanceId + '\'' +
                ", assetId='" + assetId + '\'' +
                ", status=" + status +
                ", scheduledDate=" + scheduledDate +
                '}';
    }
}