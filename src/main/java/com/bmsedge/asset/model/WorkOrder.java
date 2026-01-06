package com.bmsedge.asset.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_orders")
public class WorkOrder {

    @Id
    @Column(name = "work_order_id", nullable = false, unique = true, updatable = false)
    private String workOrderId;

    @Column(name = "asset_id", nullable = false)
    private String assetId;

    @Column(name = "vendor_id")
    private String vendorId;

    @Column(name = "maintenance_id")
    private String maintenanceId;

    @Column(name = "work_order_number", unique = true)
    private String workOrderNumber;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkOrderStatus status = WorkOrderStatus.OPEN;

    @Column(name = "priority", nullable = false)
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "work_type", nullable = false)
    private String workType; // REPAIR, MAINTENANCE, INSTALLATION, INSPECTION, UPGRADE

    @Column(name = "assigned_to")
    private String assignedTo;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Column(name = "estimated_hours")
    private Double estimatedHours;

    @Column(name = "actual_hours")
    private Double actualHours;

    @Column(name = "estimated_cost")
    private Double estimatedCost;

    @Column(name = "actual_cost")
    private Double actualCost;

    @Column(name = "labor_cost")
    private Double laborCost;

    @Column(name = "parts_cost")
    private Double partsCost;

    @Column(name = "work_performed", length = 2000)
    private String workPerformed;

    @Column(name = "parts_used", length = 1000)
    private String partsUsed;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "completion_notes", length = 1000)
    private String completionNotes;

    @Column(name = "requires_followup")
    private Boolean requiresFollowup = false;

    @Column(name = "followup_date")
    private LocalDate followupDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ======================
    // Constructors
    // ======================
    public WorkOrder() {
    }

    public WorkOrder(String assetId, String title, String workType) {
        this.assetId = assetId;
        this.title = title;
        this.workType = workType;
    }

    // ======================
    // Lifecycle hooks
    // ======================
    @PrePersist
    protected void onCreate() {
        this.workOrderId = "WO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.workOrderNumber = generateWorkOrderNumber();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = WorkOrderStatus.OPEN;
        }
        if (this.requiresFollowup == null) {
            this.requiresFollowup = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private String generateWorkOrderNumber() {
        // Format: WO-YYYYMMDD-XXXX
        String date = LocalDateTime.now().toString().substring(0, 10).replace("-", "");
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "WO-" + date + "-" + random;
    }

    // ======================
    // Getters & Setters
    // ======================
    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
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

    public String getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(String maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(String workOrderNumber) {
        this.workOrderNumber = workOrderNumber;
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

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Double getActualHours() {
        return actualHours;
    }

    public void setActualHours(Double actualHours) {
        this.actualHours = actualHours;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Double getActualCost() {
        return actualCost;
    }

    public void setActualCost(Double actualCost) {
        this.actualCost = actualCost;
    }

    public Double getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(Double laborCost) {
        this.laborCost = laborCost;
    }

    public Double getPartsCost() {
        return partsCost;
    }

    public void setPartsCost(Double partsCost) {
        this.partsCost = partsCost;
    }

    public String getWorkPerformed() {
        return workPerformed;
    }

    public void setWorkPerformed(String workPerformed) {
        this.workPerformed = workPerformed;
    }

    public String getPartsUsed() {
        return partsUsed;
    }

    public void setPartsUsed(String partsUsed) {
        this.partsUsed = partsUsed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCompletionNotes() {
        return completionNotes;
    }

    public void setCompletionNotes(String completionNotes) {
        this.completionNotes = completionNotes;
    }

    public Boolean getRequiresFollowup() {
        return requiresFollowup;
    }

    public void setRequiresFollowup(Boolean requiresFollowup) {
        this.requiresFollowup = requiresFollowup;
    }

    public LocalDate getFollowupDate() {
        return followupDate;
    }

    public void setFollowupDate(LocalDate followupDate) {
        this.followupDate = followupDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "WorkOrder{" +
                "workOrderId='" + workOrderId + '\'' +
                ", workOrderNumber='" + workOrderNumber + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                '}';
    }
}