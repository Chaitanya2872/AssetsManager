package com.bmsedge.asset.dto;

import com.bmsedge.asset.model.MaintenanceStatus;

import java.time.LocalDate;

public class MaintenanceUpdateRequest {

    private LocalDate scheduledDate;

    private LocalDate completedDate;

    private MaintenanceStatus status;

    private String priority;

    private String description;

    private String workPerformed;

    private String partsReplaced;

    private Double cost;

    private String technicianName;

    private String notes;

    private LocalDate nextMaintenanceDate;

    private Double downtimeHours;


    public MaintenanceUpdateRequest() {
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
}