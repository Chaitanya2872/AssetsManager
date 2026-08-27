package com.bmsedge.asset.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "vendors",
        indexes = {
                @Index(name = "idx_vendor_name", columnList = "vendor_name"),
                @Index(name = "idx_vendor_status", columnList = "status")
        }
)
@JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
})
public class Vendor {

    // ============================================================
    // PRIMARY KEY
    // ============================================================

    @Id
    @Column(
            name = "vendor_id",
            nullable = false,
            unique = true,
            updatable = false,
            length = 50
    )
    private String vendorId;

    // ============================================================
    // BASIC INFORMATION
    // ============================================================

    @Column(name = "vendor_name", nullable = false, length = 100)
    private String vendorName;

    @Column(
            name = "vendor_email",
            nullable = false,
            unique = true,
            length = 100
    )
    private String vendorEmail;

    @Column(name = "vendor_phone", length = 20)
    private String vendorPhone;

    @Column(name = "vendor_address", length = 500)
    private String vendorAddress;

    @Column(name = "vendor_contact_person", length = 100)
    private String contactPerson;

    @Column(name = "vendor_website", length = 255)
    private String website;

    // ============================================================
    // STATUS
    // ============================================================

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private VendorStatus status = VendorStatus.ACTIVE;

    // ============================================================
    // ADDITIONAL INFORMATION
    // ============================================================

    @Column(name = "specialization", length = 500)
    private String specialization;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "notes", length = 1000)
    private String notes;

    // ============================================================
    // ASSET RELATIONSHIP
    // ============================================================

    /**
     * Owner side of Asset <-> Vendor relationship.
     *
     * Cascade only persist/merge.
     *
     * REMOVE is intentionally NOT used because deleting a vendor
     * should never delete assets.
     */
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @JoinTable(
            name = "vendor_assets",
            joinColumns = @JoinColumn(name = "vendor_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    @JsonIgnore
    private Set<Asset> assets = new HashSet<>();

    // ============================================================
    // BACKWARD-COMPATIBILITY ASSET IDS
    // ============================================================

    /**
     * Kept because your existing backend may already use
     * vendor_asset_ids.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "vendor_asset_ids",
            joinColumns = @JoinColumn(name = "vendor_id")
    )
    @Column(name = "asset_id")
    @JsonIgnore
    private Set<String> assetIds = new HashSet<>();

    // ============================================================
    // AUDIT
    // ============================================================

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    public Vendor() {
    }

    public Vendor(
            String vendorName,
            String vendorEmail
    ) {
        this.vendorName = vendorName;
        this.vendorEmail = vendorEmail;
    }

    // ============================================================
    // JPA LIFECYCLE
    // ============================================================

    @PrePersist
    protected void onCreate() {

        if (vendorId == null || vendorId.isBlank()) {

            vendorId =
                    "VND-" +
                    UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();
        }

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = VendorStatus.ACTIVE;
        }

        if (assets == null) {
            assets = new HashSet<>();
        }

        if (assetIds == null) {
            assetIds = new HashSet<>();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

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

    @JsonIgnore
    public Set<Asset> getAssets() {
        return assets;
    }

    public void setAssets(Set<Asset> assets) {
        this.assets =
                assets != null
                        ? assets
                        : new HashSet<>();
    }

    @JsonIgnore
    public Set<String> getAssetIds() {
        return assetIds;
    }

    public void setAssetIds(Set<String> assetIds) {
        this.assetIds =
                assetIds != null
                        ? assetIds
                        : new HashSet<>();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ============================================================
    // RELATIONSHIP HELPERS
    // ============================================================

    public void addAsset(Asset asset) {

        if (asset == null) {
            return;
        }

        if (assets == null) {
            assets = new HashSet<>();
        }

        if (assetIds == null) {
            assetIds = new HashSet<>();
        }

        assets.add(asset);

        if (asset.getAssetId() != null) {
            assetIds.add(asset.getAssetId());
        }

        if (asset.getVendors() != null) {
            asset.getVendors().add(this);
        }
    }

    public void removeAsset(Asset asset) {

        if (asset == null) {
            return;
        }

        if (assets != null) {
            assets.remove(asset);
        }

        if (assetIds != null && asset.getAssetId() != null) {
            assetIds.remove(asset.getAssetId());
        }

        if (asset.getVendors() != null) {
            asset.getVendors().remove(this);
        }
    }

    public void clearAssets() {

        if (assets == null) {
            assets = new HashSet<>();
        }

        if (assetIds == null) {
            assetIds = new HashSet<>();
        }

        for (Asset asset : new HashSet<>(assets)) {

            if (asset.getVendors() != null) {
                asset.getVendors().remove(this);
            }
        }

        assets.clear();
        assetIds.clear();
    }

    // ============================================================
    // ASSET HELPERS
    // ============================================================

    public boolean hasAssets() {
        return assets != null && !assets.isEmpty();
    }

    public int getAssetCount() {
        return assets == null ? 0 : assets.size();
    }

    public boolean hasAsset(String assetId) {

        return assetIds != null
                && assetId != null
                && assetIds.contains(assetId);
    }

    // ============================================================
    // BUSINESS LOGIC
    // ============================================================

    public boolean isActive() {
        return status == VendorStatus.ACTIVE;
    }

    public boolean isInactive() {
        return status == VendorStatus.INACTIVE;
    }

    public boolean isSuspended() {
        return status == VendorStatus.SUSPENDED;
    }

    public boolean isBlacklisted() {
        return status == VendorStatus.BLACKLISTED;
    }

    public boolean canDelete() {
        return assets == null || assets.isEmpty();
    }

    public boolean isValid() {

        return vendorName != null
                && !vendorName.isBlank()
                && vendorEmail != null
                && !vendorEmail.isBlank();
    }

    // ============================================================
    // OBJECT METHODS
    // ============================================================

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Vendor)) {
            return false;
        }

        Vendor vendor = (Vendor) o;

        return vendorId != null
                && vendorId.equals(vendor.vendorId);
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
                ", assetCount=" +
                getAssetCount() +
                '}';
    }
}