package com.bmsedge.asset.dto;

import com.bmsedge.asset.model.AssetStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO used when creating an Asset.
 */
public class AssetCreateRequest {

    // ============================================================
    // BASIC INFORMATION
    // ============================================================

    @NotBlank(message = "Asset name is required")
    @Size(
            min = 3,
            max = 100,
            message = "Asset name must be between 3 and 100 characters"
    )
    private String assetName;

    @NotBlank(message = "Category is required")
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

    public AssetCreateRequest() {
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
}