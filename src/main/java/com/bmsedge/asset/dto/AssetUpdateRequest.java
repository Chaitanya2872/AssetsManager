package com.bmsedge.asset.dto;

import com.bmsedge.asset.model.AssetStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO used for partial Asset updates.
 *
 * Null values mean:
 * "Do not change this field."
 */
public class AssetUpdateRequest {

    // ============================================================
    // BASIC INFORMATION
    // ============================================================

    @Size(
            min = 3,
            max = 100,
            message = "Asset name must be between 3 and 100 characters"
    )
    private String assetName;

    @Size(
            min = 2,
            max = 50,
            message = "Category must be between 2 and 50 characters"
    )
    private String category;

    @Size(
            max = 50,
            message = "Asset type must not exceed 50 characters"
    )
    private String assetType;

    private AssetStatus status;

    @Size(
            max = 200,
            message = "Location must not exceed 200 characters"
    )
    private String location;

    @Size(
            max = 100,
            message = "Manufacturer must not exceed 100 characters"
    )
    private String manufacturer;

    @Size(
            max = 100,
            message = "Branch must not exceed 100 characters"
    )
    private String branch;

    @Size(
            max = 100,
            message = "Serial number must not exceed 100 characters"
    )
    private String serialNumber;

    @Size(
            max = 100,
            message = "Model number must not exceed 100 characters"
    )
    private String modelNumber;

    @Size(
            max = 1000,
            message = "Description must not exceed 1000 characters"
    )
    private String description;

    private LocalDate dateOfInstallation;

    @Min(
            value = 0,
            message = "Quantity must be at least 0"
    )
    private Integer quantity;

    // ============================================================
    // ASSIGNMENT
    // ============================================================

    @Size(
            max = 150,
            message = "Assigned To must not exceed 150 characters"
    )
    private String assignedTo;

    // ============================================================
    // VALUE
    // ============================================================

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Asset value must be greater than or equal to 0"
    )
    private BigDecimal value;

    // ============================================================
    // VENDOR
    // ============================================================

    @Size(
            max = 50,
            message = "Vendor ID must not exceed 50 characters"
    )
    private String vendorId;

    @Size(
            max = 100,
            message = "Vendor name must not exceed 100 characters"
    )
    private String vendorName;

    @Email(
            message = "Vendor email must be a valid email address"
    )
    @Size(
            max = 100,
            message = "Vendor email must not exceed 100 characters"
    )
    private String vendorEmail;

    @Size(
            max = 20,
            message = "Vendor phone must not exceed 20 characters"
    )
    private String vendorPhone;

    // ============================================================
    // DLP
    // ============================================================

    private LocalDate dlpEndDate;

    // ============================================================
    // WARRANTY
    // ============================================================

    private LocalDate warrantyStartDate;

    private LocalDate warrantyEndDate;

    // ============================================================
    // CONTRACT
    // ============================================================

    private LocalDate vendorContractStart;

    private LocalDate vendorContractEnd;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public AssetUpdateRequest() {
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateOfInstallation() {
        return dateOfInstallation;
    }

    public void setDateOfInstallation(
            LocalDate dateOfInstallation
    ) {
        this.dateOfInstallation = dateOfInstallation;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public String getVendorPhone() {
        return vendorPhone;
    }

    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
    }

    public LocalDate getDlpEndDate() {
        return dlpEndDate;
    }

    public void setDlpEndDate(LocalDate dlpEndDate) {
        this.dlpEndDate = dlpEndDate;
    }

    public LocalDate getWarrantyStartDate() {
        return warrantyStartDate;
    }

    public void setWarrantyStartDate(
            LocalDate warrantyStartDate
    ) {
        this.warrantyStartDate = warrantyStartDate;
    }

    public LocalDate getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(
            LocalDate warrantyEndDate
    ) {
        this.warrantyEndDate = warrantyEndDate;
    }

    public LocalDate getVendorContractStart() {
        return vendorContractStart;
    }

    public void setVendorContractStart(
            LocalDate vendorContractStart
    ) {
        this.vendorContractStart = vendorContractStart;
    }

    public LocalDate getVendorContractEnd() {
        return vendorContractEnd;
    }

    public void setVendorContractEnd(
            LocalDate vendorContractEnd
    ) {
        this.vendorContractEnd = vendorContractEnd;
    }

    // ============================================================
    // CHECK METHODS
    // ============================================================

    public boolean hasUpdates() {

        return assetName != null
                || category != null
                || assetType != null
                || status != null
                || location != null
                || manufacturer != null
                || branch != null
                || serialNumber != null
                || modelNumber != null
                || description != null
                || dateOfInstallation != null
                || quantity != null
                || assignedTo != null
                || value != null
                || vendorId != null
                || vendorName != null
                || vendorEmail != null
                || vendorPhone != null
                || dlpEndDate != null
                || warrantyStartDate != null
                || warrantyEndDate != null
                || vendorContractStart != null
                || vendorContractEnd != null;
    }

    public boolean hasBasicInfoUpdates() {

        return assetName != null
                || category != null
                || assetType != null
                || status != null
                || location != null
                || manufacturer != null
                || branch != null
                || serialNumber != null
                || modelNumber != null
                || description != null
                || quantity != null
                || assignedTo != null
                || value != null;
    }

    public boolean hasVendorUpdates() {

        return vendorId != null
                || vendorName != null
                || vendorEmail != null
                || vendorPhone != null;
    }

    public boolean hasDateUpdates() {

        return dateOfInstallation != null
                || dlpEndDate != null
                || warrantyStartDate != null
                || warrantyEndDate != null
                || vendorContractStart != null
                || vendorContractEnd != null;
    }

    public boolean hasValidWarrantyDates() {

        if (
                warrantyStartDate != null
                        && warrantyEndDate != null
        ) {

            return !warrantyEndDate.isBefore(
                    warrantyStartDate
            );
        }

        return true;
    }

    public boolean hasValidContractDates() {

        if (
                vendorContractStart != null
                        && vendorContractEnd != null
        ) {

            return !vendorContractEnd.isBefore(
                    vendorContractStart
            );
        }

        return true;
    }

    public int getUpdateCount() {

        int count = 0;

        if (assetName != null) count++;
        if (category != null) count++;
        if (assetType != null) count++;
        if (status != null) count++;
        if (location != null) count++;
        if (manufacturer != null) count++;
        if (branch != null) count++;
        if (serialNumber != null) count++;
        if (modelNumber != null) count++;
        if (description != null) count++;
        if (dateOfInstallation != null) count++;
        if (quantity != null) count++;
        if (assignedTo != null) count++;
        if (value != null) count++;
        if (vendorId != null) count++;
        if (vendorName != null) count++;
        if (vendorEmail != null) count++;
        if (vendorPhone != null) count++;
        if (dlpEndDate != null) count++;
        if (warrantyStartDate != null) count++;
        if (warrantyEndDate != null) count++;
        if (vendorContractStart != null) count++;
        if (vendorContractEnd != null) count++;

        return count;
    }

    // ============================================================
    // BUILDER
    // ============================================================

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final AssetUpdateRequest request =
                new AssetUpdateRequest();

        public Builder assetName(String value) {
            request.setAssetName(value);
            return this;
        }

        public Builder category(String value) {
            request.setCategory(value);
            return this;
        }

        public Builder assetType(String value) {
            request.setAssetType(value);
            return this;
        }

        public Builder status(AssetStatus value) {
            request.setStatus(value);
            return this;
        }

        public Builder location(String value) {
            request.setLocation(value);
            return this;
        }

        public Builder manufacturer(String value) {
            request.setManufacturer(value);
            return this;
        }

        public Builder branch(String value) {
            request.setBranch(value);
            return this;
        }

        public Builder serialNumber(String value) {
            request.setSerialNumber(value);
            return this;
        }

        public Builder modelNumber(String value) {
            request.setModelNumber(value);
            return this;
        }

        public Builder description(String value) {
            request.setDescription(value);
            return this;
        }

        public Builder dateOfInstallation(LocalDate value) {
            request.setDateOfInstallation(value);
            return this;
        }

        public Builder quantity(Integer value) {
            request.setQuantity(value);
            return this;
        }

        public Builder assignedTo(String value) {
            request.setAssignedTo(value);
            return this;
        }

        public Builder value(BigDecimal value) {
            request.setValue(value);
            return this;
        }

        public Builder vendorId(String value) {
            request.setVendorId(value);
            return this;
        }

        public Builder vendorName(String value) {
            request.setVendorName(value);
            return this;
        }

        public Builder vendorEmail(String value) {
            request.setVendorEmail(value);
            return this;
        }

        public Builder vendorPhone(String value) {
            request.setVendorPhone(value);
            return this;
        }

        public Builder dlpEndDate(LocalDate value) {
            request.setDlpEndDate(value);
            return this;
        }

        public Builder warrantyStartDate(LocalDate value) {
            request.setWarrantyStartDate(value);
            return this;
        }

        public Builder warrantyEndDate(LocalDate value) {
            request.setWarrantyEndDate(value);
            return this;
        }

        public Builder vendorContractStart(LocalDate value) {
            request.setVendorContractStart(value);
            return this;
        }

        public Builder vendorContractEnd(LocalDate value) {
            request.setVendorContractEnd(value);
            return this;
        }

        public AssetUpdateRequest build() {
            return request;
        }
    }

    // ============================================================
    // TO STRING
    // ============================================================

    @Override
    public String toString() {

        return "AssetUpdateRequest{" +
                "assetName='" + assetName + '\'' +
                ", category='" + category + '\'' +
                ", assetType='" + assetType + '\'' +
                ", status=" + status +
                ", location='" + location + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", branch='" + branch + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", description='" + description + '\'' +
                ", dateOfInstallation=" + dateOfInstallation +
                ", quantity=" + quantity +
                ", assignedTo='" + assignedTo + '\'' +
                ", value=" + value +
                ", vendorId='" + vendorId + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", vendorEmail='" + vendorEmail + '\'' +
                ", vendorPhone='" + vendorPhone + '\'' +
                ", dlpEndDate=" + dlpEndDate +
                ", warrantyStartDate=" + warrantyStartDate +
                ", warrantyEndDate=" + warrantyEndDate +
                ", vendorContractStart=" + vendorContractStart +
                ", vendorContractEnd=" + vendorContractEnd +
                '}';
    }
}