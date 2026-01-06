package com.bmsedge.asset.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "vendors")
public class Vendor {

    @Id
    @Column(name = "vendor_id", nullable = false, unique = true, updatable = false)
    private String vendorId;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    @Column(name = "vendor_email", nullable = false, unique = true)
    private String vendorEmail;

    @Column(name = "vendor_phone")
    private String vendorPhone;

    @Column(name = "vendor_address", length = 500)
    private String vendorAddress;

    @Column(name = "vendor_contact_person")
    private String contactPerson;

    @Column(name = "vendor_website")
    private String website;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VendorStatus status = VendorStatus.ACTIVE;

    @Column(name = "specialization", length = 500)
    private String specialization;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "notes", length = 1000)
    private String notes;

    // ✅ CORRECT MANY-TO-MANY RELATIONSHIP
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "vendor_assets",
            joinColumns = @JoinColumn(name = "vendor_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private Set<Asset> assets = new HashSet<>();

    // For backward compatibility - store asset IDs as well
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "vendor_asset_ids", joinColumns = @JoinColumn(name = "vendor_id"))
    @Column(name = "asset_id")
    private Set<String> assetIds = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ======================
    // Constructors
    // ======================

    public Vendor() {
    }

    public Vendor(String vendorName, String vendorEmail) {
        this.vendorName = vendorName;
        this.vendorEmail = vendorEmail;
    }

    // ======================
    // Lifecycle hooks
    // ======================

    @PrePersist
    protected void onCreate() {
        if (this.vendorId == null) {
            this.vendorId = "VND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = VendorStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ======================
    // Getters & Setters
    // ======================

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

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public VendorStatus getStatus() {
        return status;
    }

    public void setStatus(VendorStatus status) {
        this.status = status;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<Asset> getAssets() {
        return assets;
    }

    public void setAssets(Set<Asset> assets) {
        this.assets = assets;
    }

    public Set<String> getAssetIds() {
        return assetIds;
    }

    public void setAssetIds(Set<String> assetIds) {
        this.assetIds = assetIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ======================
    // Convenience helpers (VERY IMPORTANT)
    // ======================

    /**
     * Add an asset to this vendor's assets collection
     * Maintains bidirectional relationship
     */
    public void addAsset(Asset asset) {
        this.assets.add(asset);
        this.assetIds.add(asset.getAssetId());
        asset.getVendors().add(this);
    }

    /**
     * Remove an asset from this vendor's assets collection
     * Maintains bidirectional relationship
     */
    public void removeAsset(Asset asset) {
        this.assets.remove(asset);
        this.assetIds.remove(asset.getAssetId());
        asset.getVendors().remove(this);
    }

    /**
     * Clear all assets from this vendor
     */
    public void clearAssets() {
        // Remove vendor from all assets
        for (Asset asset : new HashSet<>(this.assets)) {
            asset.getVendors().remove(this);
        }
        this.assets.clear();
        this.assetIds.clear();
    }

    /**
     * Check if vendor has any assets
     */
    public boolean hasAssets() {
        return !this.assets.isEmpty();
    }

    /**
     * Get asset count
     */
    public int getAssetCount() {
        return this.assets.size();
    }

    /**
     * Check if vendor has a specific asset
     */
    public boolean hasAsset(String assetId) {
        return this.assetIds.contains(assetId);
    }

    // ======================
    // Business Logic Methods
    // ======================

    /**
     * Check if vendor is active
     */
    public boolean isActive() {
        return this.status == VendorStatus.ACTIVE;
    }

    /**
     * Check if vendor is inactive
     */
    public boolean isInactive() {
        return this.status == VendorStatus.INACTIVE;
    }

    /**
     * Check if vendor is suspended
     */
    public boolean isSuspended() {
        return this.status == VendorStatus.SUSPENDED;
    }

    /**
     * Check if vendor is blacklisted
     */
    public boolean isBlacklisted() {
        return this.status == VendorStatus.BLACKLISTED;
    }

    /**
     * Check if vendor can be deleted (no associated assets)
     */
    public boolean canDelete() {
        return this.assets.isEmpty();
    }

    /**
     * Validate vendor data
     */
    public boolean isValid() {
        return this.vendorName != null && !this.vendorName.isBlank()
                && this.vendorEmail != null && !this.vendorEmail.isBlank();
    }

    // ======================
    // Object Methods
    // ======================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vendor)) return false;
        Vendor vendor = (Vendor) o;
        return vendorId != null && vendorId.equals(vendor.vendorId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Vendor{" +
                "vendorId='" + vendorId + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", vendorEmail='" + vendorEmail + '\'' +
                ", status=" + status +
                ", assetCount=" + assets.size() +
                '}';
    }
}