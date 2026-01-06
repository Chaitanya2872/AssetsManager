package com.bmsedge.asset.dto;

import com.bmsedge.asset.model.AssetStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Data Transfer Object for updating an Asset
 * Contains all updatable fields with validation constraints
 */
public class AssetUpdateRequest {

    // ============================================================
    // BASIC ASSET INFORMATION
    // ============================================================

    @Size(min = 3, max = 100, message = "Asset name must be between 3 and 100 characters")
    private String assetName;

    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
    private String category;

    @Size(max = 50, message = "Asset type must not exceed 50 characters")
    private String assetType;

    private AssetStatus status;

    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @Size(max = 100, message = "Manufacturer must not exceed 100 characters")
    private String manufacturer;

    @Size(max = 100, message = "Branch must not exceed 100 characters")
    private String branch;

    @Size(max = 100, message = "Serial number must not exceed 100 characters")
    private String serialNumber;

    @Size(max = 100, message = "Model number must not exceed 100 characters")
    private String modelNumber;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private LocalDate dateOfInstallation;

    @Min(value = 0, message = "Quantity must be at least 0")
    private Integer quantity;

    // ============================================================
    // VENDOR INFORMATION
    // ============================================================

    @Size(max = 50, message = "Vendor ID must not exceed 50 characters")
    private String vendorId;

    @Size(max = 100, message = "Vendor name must not exceed 100 characters")
    private String vendorName;

    @Email(message = "Vendor email must be a valid email address")
    @Size(max = 100, message = "Vendor email must not exceed 100 characters")
    private String vendorEmail;

    @Size(max = 20, message = "Vendor phone must not exceed 20 characters")
    private String vendorPhone;

    // ============================================================
    // DLP (DEFECT LIABILITY PERIOD)
    // ============================================================

    private LocalDate dlpEndDate;

    // ============================================================
    // WARRANTY INFORMATION
    // ============================================================

    private LocalDate warrantyStartDate;

    private LocalDate warrantyEndDate;

    // ============================================================
    // VENDOR CONTRACT INFORMATION
    // ============================================================

    private LocalDate vendorContractStart;

    private LocalDate vendorContractEnd;

    // ============================================================
    // CONSTRUCTORS
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

    public void setDateOfInstallation(LocalDate dateOfInstallation) {
        this.dateOfInstallation = dateOfInstallation;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public void setWarrantyStartDate(LocalDate warrantyStartDate) {
        this.warrantyStartDate = warrantyStartDate;
    }

    public LocalDate getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(LocalDate warrantyEndDate) {
        this.warrantyEndDate = warrantyEndDate;
    }

    public LocalDate getVendorContractStart() {
        return vendorContractStart;
    }

    public void setVendorContractStart(LocalDate vendorContractStart) {
        this.vendorContractStart = vendorContractStart;
    }

    public LocalDate getVendorContractEnd() {
        return vendorContractEnd;
    }

    public void setVendorContractEnd(LocalDate vendorContractEnd) {
        this.vendorContractEnd = vendorContractEnd;
    }

    // ============================================================
    // UTILITY METHODS
    // ============================================================

    /**
     * Check if any field is set (not null)
     */
    public boolean hasUpdates() {
        return assetName != null || category != null || assetType != null ||
                status != null || location != null || manufacturer != null ||
                branch != null || serialNumber != null || modelNumber != null ||
                description != null || dateOfInstallation != null || quantity != null ||
                vendorId != null || vendorName != null || vendorEmail != null ||
                vendorPhone != null || dlpEndDate != null || warrantyStartDate != null ||
                warrantyEndDate != null || vendorContractStart != null || vendorContractEnd != null;
    }

    /**
     * Check if basic information is being updated
     */
    public boolean hasBasicInfoUpdates() {
        return assetName != null || category != null || assetType != null ||
                location != null || manufacturer != null || branch != null ||
                serialNumber != null || modelNumber != null || description != null;
    }

    /**
     * Check if vendor information is being updated
     */
    public boolean hasVendorUpdates() {
        return vendorId != null || vendorName != null ||
                vendorEmail != null || vendorPhone != null;
    }

    /**
     * Check if date-related fields are being updated
     */
    public boolean hasDateUpdates() {
        return dateOfInstallation != null || dlpEndDate != null ||
                warrantyStartDate != null || warrantyEndDate != null ||
                vendorContractStart != null || vendorContractEnd != null;
    }

    /**
     * Validate warranty dates
     */
    public boolean hasValidWarrantyDates() {
        if (warrantyStartDate != null && warrantyEndDate != null) {
            return !warrantyEndDate.isBefore(warrantyStartDate);
        }
        return true;
    }

    /**
     * Validate contract dates
     */
    public boolean hasValidContractDates() {
        if (vendorContractStart != null && vendorContractEnd != null) {
            return !vendorContractEnd.isBefore(vendorContractStart);
        }
        return true;
    }

    /**
     * Get count of non-null fields
     */
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
    // BUILDER PATTERN (OPTIONAL)
    // ============================================================

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AssetUpdateRequest request = new AssetUpdateRequest();

        public Builder assetName(String assetName) {
            request.setAssetName(assetName);
            return this;
        }

        public Builder category(String category) {
            request.setCategory(category);
            return this;
        }

        public Builder assetType(String assetType) {
            request.setAssetType(assetType);
            return this;
        }

        public Builder status(AssetStatus status) {
            request.setStatus(status);
            return this;
        }

        public Builder location(String location) {
            request.setLocation(location);
            return this;
        }

        public Builder manufacturer(String manufacturer) {
            request.setManufacturer(manufacturer);
            return this;
        }

        public Builder branch(String branch) {
            request.setBranch(branch);
            return this;
        }

        public Builder serialNumber(String serialNumber) {
            request.setSerialNumber(serialNumber);
            return this;
        }

        public Builder modelNumber(String modelNumber) {
            request.setModelNumber(modelNumber);
            return this;
        }

        public Builder description(String description) {
            request.setDescription(description);
            return this;
        }

        public Builder dateOfInstallation(LocalDate dateOfInstallation) {
            request.setDateOfInstallation(dateOfInstallation);
            return this;
        }

        public Builder quantity(Integer quantity) {
            request.setQuantity(quantity);
            return this;
        }

        public Builder vendorId(String vendorId) {
            request.setVendorId(vendorId);
            return this;
        }

        public Builder vendorName(String vendorName) {
            request.setVendorName(vendorName);
            return this;
        }

        public Builder vendorEmail(String vendorEmail) {
            request.setVendorEmail(vendorEmail);
            return this;
        }

        public Builder vendorPhone(String vendorPhone) {
            request.setVendorPhone(vendorPhone);
            return this;
        }

        public Builder dlpEndDate(LocalDate dlpEndDate) {
            request.setDlpEndDate(dlpEndDate);
            return this;
        }

        public Builder warrantyStartDate(LocalDate warrantyStartDate) {
            request.setWarrantyStartDate(warrantyStartDate);
            return this;
        }

        public Builder warrantyEndDate(LocalDate warrantyEndDate) {
            request.setWarrantyEndDate(warrantyEndDate);
            return this;
        }

        public Builder vendorContractStart(LocalDate vendorContractStart) {
            request.setVendorContractStart(vendorContractStart);
            return this;
        }

        public Builder vendorContractEnd(LocalDate vendorContractEnd) {
            request.setVendorContractEnd(vendorContractEnd);
            return this;
        }

        public AssetUpdateRequest build() {
            return request;
        }
    }

    // ============================================================
    // OBJECT METHODS
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
                ", dateOfInstallation=" + dateOfInstallation +
                ", quantity=" + quantity +
                ", vendorId='" + vendorId + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", dlpEndDate=" + dlpEndDate +
                ", warrantyStartDate=" + warrantyStartDate +
                ", warrantyEndDate=" + warrantyEndDate +
                ", vendorContractStart=" + vendorContractStart +
                ", vendorContractEnd=" + vendorContractEnd +
                ", updateCount=" + getUpdateCount() +
                '}';
    }
}