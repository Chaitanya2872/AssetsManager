package com.bmsedge.asset.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "assets",
        indexes = {
                @Index(name = "idx_asset_category", columnList = "asset_category"),
                @Index(name = "idx_asset_status", columnList = "status"),
                @Index(name = "idx_asset_location", columnList = "location"),
                @Index(name = "idx_asset_vendor_id", columnList = "vendor_id"),
                @Index(name = "idx_asset_branch", columnList = "branch"),
                @Index(name = "idx_asset_manufacturer", columnList = "manufacturer"),
                @Index(name = "idx_asset_dlp_end_date", columnList = "dlp_end_date"),
                @Index(name = "idx_asset_warranty_end_date", columnList = "warranty_end_date"),
                @Index(name = "idx_asset_contract_end_date", columnList = "vendor_contract_end")
        }
)
public class Asset {

    // ============================================================
    // PRIMARY KEY
    // ============================================================

    @Id
    @Column(name = "asset_id", nullable = false, updatable = false, length = 100)
    private String assetId;

    // ============================================================
    // BASIC INFORMATION
    // ============================================================

    @Column(name = "asset_name", nullable = false, length = 100)
    private String assetName;

    @Column(name = "asset_category", nullable = false, length = 100)
    private String assetCategory;

    @Column(name = "asset_type", length = 50)
    private String assetType;

    @Column(name = "asset_subcategory", length = 100)
    private String assetSubcategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private AssetStatus status;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    @Column(name = "branch", length = 100)
    private String branch;

    @Column(name = "serial_number", unique = true, length = 100)
    private String serialNumber;

    @Column(name = "model_number", length = 100)
    private String modelNumber;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "asset_image_url", length = 500)
    private String assetImageUrl;

    @Column(name = "date_of_installation")
    private LocalDate dateOfInstallation;

    @Column(name = "quantity")
    private Integer quantity;

    // ============================================================
    // ASSIGNMENT
    // ============================================================

    @Column(name = "assigned_to", length = 255)
    private String assignedTo;

    // ============================================================
    // ASSET VALUE
    // ============================================================

    @Column(name = "value", precision = 15, scale = 2)
    private BigDecimal value;

    // ============================================================
    // VENDOR INFORMATION
    // ============================================================

    @Column(name = "vendor_id", length = 50)
    private String vendorId;

    @Column(name = "vendor_name", length = 100)
    private String vendorName;

    @Column(name = "vendor_email", length = 100)
    private String vendorEmail;

    @Column(name = "vendor_phone", length = 20)
    private String vendorPhone;

    // ============================================================
    // VENDOR RELATIONSHIP
    // ============================================================

    /**
     * The relationship is managed from Vendor.
     *
     * JsonIgnore is important here because otherwise:
     *
     * Asset -> Vendor -> Asset -> Vendor ...
     *
     * can result in recursive JSON serialization.
     */
    @ManyToMany(mappedBy = "assets", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Vendor> vendors = new HashSet<>();

    // ============================================================
    // DLP
    // ============================================================

    @Column(name = "dlp_end_date")
    private LocalDate dlpEndDate;

    // ============================================================
    // WARRANTY
    // ============================================================

    @Column(name = "warranty_start_date")
    private LocalDate warrantyStartDate;

    @Column(name = "warranty_end_date")
    private LocalDate warrantyEndDate;

    // ============================================================
    // VENDOR CONTRACT
    // ============================================================

    @Column(name = "vendor_contract_start")
    private LocalDate vendorContractStart;

    @Column(name = "vendor_contract_end")
    private LocalDate vendorContractEnd;

    // ============================================================
    // AUDIT
    // ============================================================

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ============================================================
    // DLP ALERT LEVEL
    // ============================================================

    public enum DlpAlertLevel {
        EXPIRED,
        CRITICAL,
        WARNING,
        NONE
    }

    // ============================================================
    // JPA LIFECYCLE
    // ============================================================

    @PrePersist
    protected void onCreate() {

        if (assetId == null || assetId.isBlank()) {
            assetId = UUID.randomUUID().toString();
        }

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = AssetStatus.AVAILABLE;
        }

        if (quantity == null) {
            quantity = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(String assetCategory) {
        this.assetCategory = assetCategory;
    }

    public String getAssetSubcategory() {
        return assetSubcategory;
    }

    public void setAssetSubcategory(String assetSubcategory) {
        this.assetSubcategory = assetSubcategory;
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

    public String getAssetImageUrl() {
        return assetImageUrl;
    }

    public void setAssetImageUrl(String assetImageUrl) {
        this.assetImageUrl = assetImageUrl;
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

    @JsonIgnore
    public Set<Vendor> getVendors() {
        return vendors;
    }

    public void setVendors(Set<Vendor> vendors) {
        this.vendors = vendors != null ? vendors : new HashSet<>();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ============================================================
    // DLP BUSINESS LOGIC
    // ============================================================

    public boolean isDlpExpired() {

        return dlpEndDate != null
                && LocalDate.now().isAfter(dlpEndDate);
    }

    public DlpAlertLevel getDlpAlertLevel() {

        if (dlpEndDate == null) {
            return DlpAlertLevel.NONE;
        }

        long days = ChronoUnit.DAYS.between(
                LocalDate.now(),
                dlpEndDate
        );

        if (days < 0) {
            return DlpAlertLevel.EXPIRED;
        }

        if (days <= 7) {
            return DlpAlertLevel.CRITICAL;
        }

        if (days <= 30) {
            return DlpAlertLevel.WARNING;
        }

        return DlpAlertLevel.NONE;
    }

    // ============================================================
    // WARRANTY BUSINESS LOGIC
    // ============================================================

    public boolean isWarrantyExpired() {

        return warrantyEndDate != null
                && LocalDate.now().isAfter(warrantyEndDate);
    }

    public boolean isWarrantyExpiringSoon(int days) {

        if (warrantyEndDate == null || days < 0) {
            return false;
        }

        long daysUntilExpiry = ChronoUnit.DAYS.between(
                LocalDate.now(),
                warrantyEndDate
        );

        return daysUntilExpiry >= 0
                && daysUntilExpiry <= days;
    }

    // ============================================================
    // VENDOR CONTRACT BUSINESS LOGIC
    // ============================================================

    public boolean isVendorContractExpired() {

        return vendorContractEnd != null
                && LocalDate.now().isAfter(vendorContractEnd);
    }

    public boolean isVendorContractExpiringSoon(int days) {

        if (vendorContractEnd == null || days < 0) {
            return false;
        }

        long daysUntilExpiry = ChronoUnit.DAYS.between(
                LocalDate.now(),
                vendorContractEnd
        );

        return daysUntilExpiry >= 0
                && daysUntilExpiry <= days;
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    @JsonProperty("qrCode")
    public String getQrCode() {
        return assetId == null ? null : "ASSET:" + assetId;
    }

    @JsonProperty("id")
    public String getId() {
        return assetId;
    }

    // ============================================================
    // OBJECT METHODS
    // ============================================================

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Asset)) {
            return false;
        }

        Asset asset = (Asset) o;

        return assetId != null
                && assetId.equals(asset.assetId);
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }

    @Override
    public String toString() {

        return "Asset{" +
                "assetId='" + assetId + '\'' +
                ", assetName='" + assetName + '\'' +
                ", assetCategory='" + assetCategory + '\'' +
                ", assetType='" + assetType + '\'' +
                ", status=" + status +
                ", location='" + location + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", branch='" + branch + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", quantity=" + quantity +
                ", assignedTo='" + assignedTo + '\'' +
                ", value=" + value +
                ", vendorId='" + vendorId + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", dlpEndDate=" + dlpEndDate +
                ", warrantyStartDate=" + warrantyStartDate +
                ", warrantyEndDate=" + warrantyEndDate +
                ", vendorContractStart=" + vendorContractStart +
                ", vendorContractEnd=" + vendorContractEnd +
                '}';
    }
}
