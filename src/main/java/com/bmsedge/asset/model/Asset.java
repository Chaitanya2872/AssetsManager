package com.bmsedge.asset.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "assets")
public class Asset {

    // ================= BASIC FIELDS =================

    @Id
    @Column(name = "asset_id", nullable = false, updatable = false)
    private String assetId;

    @Column(name = "asset_name", nullable = false)
    private String assetName;

    @Column(name = "asset_category", nullable = false)
    private String assetCategory;

    @Column(name = "asset_type")
    private String assetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;

    @Column
    private String location;

    @Column
    private String manufacturer;

    @Column
    private String branch;

    @Column(unique = true)
    private String serialNumber;

    @Column(name = "model_number")
    private String modelNumber;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "date_of_installation")
    private LocalDate dateOfInstallation;

    @Column(name = "quantity")
    private Integer quantity;

    // ================= VENDOR FIELDS (for backward compatibility) =================
    // Note: Primary vendor relationship is Many-to-Many, but keeping these for quick access

    @Column(name = "vendor_id")
    private String vendorId;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "vendor_email")
    private String vendorEmail;

    @Column(name = "vendor_phone")
    private String vendorPhone;

    // ================= RELATIONSHIPS =================

    @ManyToMany(mappedBy = "assets", fetch = FetchType.LAZY)
    private Set<Vendor> vendors = new HashSet<>();

    // ================= DLP / WARRANTY / CONTRACT =================

    @Column(name = "dlp_end_date")
    private LocalDate dlpEndDate;

    @Column(name = "warranty_start_date")
    private LocalDate warrantyStartDate;

    @Column(name = "warranty_end_date")
    private LocalDate warrantyEndDate;

    @Column(name = "vendor_contract_start")
    private LocalDate vendorContractStart;

    @Column(name = "vendor_contract_end")
    private LocalDate vendorContractEnd;

    // ================= AUDIT =================

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ================= ENUM =================

    public enum DlpAlertLevel {
        EXPIRED, CRITICAL, WARNING, NONE
    }

    // ================= JPA CALLBACKS =================

    @PrePersist
    protected void onCreate() {
        if (this.assetId == null) {
            this.assetId = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = AssetStatus.AVAILABLE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ================= GETTERS & SETTERS =================

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

    public Set<Vendor> getVendors() {
        return vendors;
    }

    public void setVendors(Set<Vendor> vendors) {
        this.vendors = vendors;
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

    // ================= BUSINESS LOGIC =================

    public boolean isDlpExpired() {
        return dlpEndDate != null && LocalDate.now().isAfter(dlpEndDate);
    }

    public DlpAlertLevel getDlpAlertLevel() {
        if (dlpEndDate == null) return DlpAlertLevel.NONE;

        long days = ChronoUnit.DAYS.between(LocalDate.now(), dlpEndDate);

        if (days < 0) return DlpAlertLevel.EXPIRED;
        if (days <= 7) return DlpAlertLevel.CRITICAL;
        if (days <= 30) return DlpAlertLevel.WARNING;

        return DlpAlertLevel.NONE;
    }

    public boolean isWarrantyExpired() {
        return warrantyEndDate != null && LocalDate.now().isAfter(warrantyEndDate);
    }

    public boolean isWarrantyExpiringSoon(int days) {
        if (warrantyEndDate == null) return false;
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), warrantyEndDate);
        return daysUntilExpiry >= 0 && daysUntilExpiry <= days;
    }

    public boolean isVendorContractExpired() {
        return vendorContractEnd != null && LocalDate.now().isAfter(vendorContractEnd);
    }

    public boolean isVendorContractExpiringSoon(int days) {
        if (vendorContractEnd == null) return false;
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), vendorContractEnd);
        return daysUntilExpiry >= 0 && daysUntilExpiry <= days;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "assetId='" + assetId + '\'' +
                ", assetName='" + assetName + '\'' +
                ", assetCategory='" + assetCategory + '\'' +
                ", status=" + status +
                ", location='" + location + '\'' +
                '}';
    }
}