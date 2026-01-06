package com.bmsedge.asset.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class WorkOrderCreateRequest {

    @NotBlank(message = "Asset ID is required")
    private String assetId;

    private String vendorId;
    private String maintenanceId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Work type is required")
    private String workType; // REPAIR, MAINTENANCE, INSTALLATION, INSPECTION, UPGRADE

    @NotBlank(message = "Priority is required")
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

    private LocalDate dueDate;
    private Double estimatedHours;
    private Double estimatedCost;
    private String assignedTo;
    private String requestedBy;
    private String notes;

    // Constructors
    public WorkOrderCreateRequest() {
    }

    public WorkOrderCreateRequest(String assetId, String title, String workType, String priority) {
        this.assetId = assetId;
        this.title = title;
        this.workType = workType;
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

    public String getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(String maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}