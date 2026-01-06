package com.bmsedge.asset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class MaintenanceCreateRequest {

    @NotBlank(message = "Asset ID is required")
    private String assetId;

    private String vendorId;

    @NotBlank(message = "Maintenance type is required")
    private String maintenanceType; // PREVENTIVE, CORRECTIVE, PREDICTIVE, EMERGENCY

    private LocalDate scheduledDate;

    @NotBlank(message = "Priority is required")
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

    private String description;
    private String technicianName;
    private String notes;

    // Constructors
    public MaintenanceCreateRequest() {
    }

    public MaintenanceCreateRequest(String assetId, String maintenanceType, String priority) {
        this.assetId = assetId;
        this.maintenanceType = maintenanceType;
        this.priority = priority;
    }

    // Getters and Setters
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
}