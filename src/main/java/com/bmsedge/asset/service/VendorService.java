package com.bmsedge.asset.service;

import com.bmsedge.asset.dto.VendorCreateRequest;
import com.bmsedge.asset.dto.VendorUpdateRequest;
import com.bmsedge.asset.exception.VendorNotFoundException;
import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.Vendor;
import com.bmsedge.asset.model.VendorStatus;
import com.bmsedge.asset.repository.AssetRepository;
import com.bmsedge.asset.repository.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorService.class);

    private final VendorRepository vendorRepository;
    private final AssetRepository assetRepository;

    public VendorService(VendorRepository vendorRepository, AssetRepository assetRepository) {
        this.vendorRepository = vendorRepository;
        this.assetRepository = assetRepository;
    }

    // ===================== VENDOR CRUD =====================

    /**
     * Create a new vendor
     */
    public Vendor create(VendorCreateRequest request) {
        logger.info("Creating vendor: {}", request.getVendorName());

        // Check if email already exists
        if (vendorRepository.existsByVendorEmail(request.getVendorEmail())) {
            throw new IllegalArgumentException("Vendor with email " + request.getVendorEmail() + " already exists");
        }

        Vendor vendor = new Vendor();
        vendor.setVendorName(request.getVendorName());
        vendor.setVendorEmail(request.getVendorEmail());
        vendor.setVendorPhone(request.getVendorPhone());
        vendor.setVendorAddress(request.getVendorAddress());
        vendor.setContactPerson(request.getContactPerson());
        vendor.setWebsite(request.getWebsite());
        vendor.setSpecialization(request.getSpecialization());
        vendor.setNotes(request.getNotes());

        Vendor savedVendor = vendorRepository.save(vendor);
        logger.info("Vendor created successfully with ID: {}", savedVendor.getVendorId());

        return savedVendor;
    }

    /**
     * Get vendor by ID
     */
    @Transactional(readOnly = true)
    public Vendor get(String id) {
        logger.debug("Fetching vendor with ID: {}", id);
        return vendorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Vendor not found with ID: {}", id);
                    return new VendorNotFoundException(id);
                });
    }

    /**
     * Get vendor by email
     */
    @Transactional(readOnly = true)
    public Vendor getByEmail(String email) {
        logger.debug("Fetching vendor with email: {}", email);
        return vendorRepository.findByVendorEmail(email)
                .orElseThrow(() -> new VendorNotFoundException("Email: " + email));
    }

    /**
     * Get all vendors
     */
    @Transactional(readOnly = true)
    public List<Vendor> getAll() {
        logger.debug("Fetching all vendors");
        return vendorRepository.findAll();
    }

    /**
     * Get vendors by status
     */
    @Transactional(readOnly = true)
    public List<Vendor> getByStatus(String status) {
        logger.debug("Fetching vendors with status: {}", status);
        try {
            VendorStatus vendorStatus = VendorStatus.valueOf(status.toUpperCase());
            return vendorRepository.findByStatus(vendorStatus);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid vendor status: {}", status);
            throw new IllegalArgumentException("Invalid vendor status: " + status);
        }
    }

    /**
     * Search vendors
     */
    @Transactional(readOnly = true)
    public List<Vendor> search(String query) {
        logger.debug("Searching vendors with query: {}", query);
        return vendorRepository.searchVendors(query);
    }

    /**
     * Get vendors by asset ID
     */
    @Transactional(readOnly = true)
    public List<Vendor> getByAssetId(String assetId) {
        logger.debug("Fetching vendors for asset: {}", assetId);
        return vendorRepository.findByAssetId(assetId);
    }

    /**
     * Update vendor
     */
    public Vendor update(String id, VendorUpdateRequest request) {
        logger.info("Updating vendor with ID: {}", id);

        Vendor vendor = get(id);

        if (request.getVendorName() != null && !request.getVendorName().isBlank()) {
            vendor.setVendorName(request.getVendorName());
        }

        if (request.getVendorEmail() != null && !request.getVendorEmail().isBlank()) {
            // Check if email is being changed and if new email already exists
            if (!vendor.getVendorEmail().equals(request.getVendorEmail())
                    && vendorRepository.existsByVendorEmail(request.getVendorEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            vendor.setVendorEmail(request.getVendorEmail());
        }

        if (request.getVendorPhone() != null) {
            vendor.setVendorPhone(request.getVendorPhone());
        }

        if (request.getVendorAddress() != null) {
            vendor.setVendorAddress(request.getVendorAddress());
        }

        if (request.getContactPerson() != null) {
            vendor.setContactPerson(request.getContactPerson());
        }

        if (request.getWebsite() != null) {
            vendor.setWebsite(request.getWebsite());
        }

        if (request.getSpecialization() != null) {
            vendor.setSpecialization(request.getSpecialization());
        }

        if (request.getNotes() != null) {
            vendor.setNotes(request.getNotes());
        }

        if (request.getStatus() != null) {
            vendor.setStatus(request.getStatus());
        }

        Vendor savedVendor = vendorRepository.save(vendor);
        logger.info("Vendor updated successfully: {}", id);

        return savedVendor;
    }

    /**
     * Update vendor status
     */
    public Vendor updateStatus(String id, String status) {
        logger.info("Updating status for vendor {}: {}", id, status);

        Vendor vendor = get(id);

        try {
            VendorStatus vendorStatus = VendorStatus.valueOf(status.toUpperCase());
            vendor.setStatus(vendorStatus);
            return vendorRepository.save(vendor);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid vendor status: {}", status);
            throw new IllegalArgumentException("Invalid vendor status: " + status);
        }
    }

    /**
     * Update vendor rating
     */
    public Vendor updateRating(String id, Double rating) {
        logger.info("Updating rating for vendor {}: {}", id, rating);

        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        Vendor vendor = get(id);
        vendor.setRating(rating);

        return vendorRepository.save(vendor);
    }

    /**
     * Delete vendor
     */
    public void delete(String id) {
        logger.info("Deleting vendor with ID: {}", id);

        if (!vendorRepository.existsById(id)) {
            logger.error("Vendor not found with ID: {}", id);
            throw new VendorNotFoundException(id);
        }

        // Optional: Check if vendor has assets and prevent deletion
        Vendor vendor = get(id);
        if (!vendor.getAssets().isEmpty()) {
            throw new IllegalStateException("Cannot delete vendor with associated assets. Please remove all asset associations first.");
        }

        vendorRepository.deleteById(id);
        logger.info("Vendor deleted successfully: {}", id);
    }

    // ===================== ASSET RELATIONSHIP MANAGEMENT =====================

    /**
     * Add asset to vendor (Many-to-Many relationship)
     */
    public Vendor addAsset(String vendorId, String assetId) {
        logger.info("Adding asset {} to vendor {}", assetId, vendorId);

        Vendor vendor = get(vendorId);
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + assetId));

        vendor.addAsset(asset);

        Vendor savedVendor = vendorRepository.save(vendor);
        logger.info("Asset added to vendor successfully");

        return savedVendor;
    }

    /**
     * Remove asset from vendor
     */
    public Vendor removeAsset(String vendorId, String assetId) {
        logger.info("Removing asset {} from vendor {}", assetId, vendorId);

        Vendor vendor = get(vendorId);
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + assetId));

        vendor.removeAsset(asset);

        Vendor savedVendor = vendorRepository.save(vendor);
        logger.info("Asset removed from vendor successfully");

        return savedVendor;
    }

    /**
     * Bulk add assets to vendor
     */
    public Vendor bulkAddAssets(String vendorId, List<String> assetIds) {
        logger.info("Bulk adding {} assets to vendor {}", assetIds.size(), vendorId);

        Vendor vendor = get(vendorId);
        int added = 0;

        for (String assetId : assetIds) {
            try {
                Asset asset = assetRepository.findById(assetId)
                        .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + assetId));
                vendor.addAsset(asset);
                added++;
            } catch (Exception e) {
                logger.error("Failed to add asset {} to vendor: {}", assetId, e.getMessage());
            }
        }

        Vendor savedVendor = vendorRepository.save(vendor);
        logger.info("Bulk added {} assets to vendor successfully", added);

        return savedVendor;
    }

    /**
     * Bulk remove assets from vendor
     */
    public Vendor bulkRemoveAssets(String vendorId, List<String> assetIds) {
        logger.info("Bulk removing {} assets from vendor {}", assetIds.size(), vendorId);

        Vendor vendor = get(vendorId);
        int removed = 0;

        for (String assetId : assetIds) {
            try {
                Asset asset = assetRepository.findById(assetId)
                        .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + assetId));
                vendor.removeAsset(asset);
                removed++;
            } catch (Exception e) {
                logger.error("Failed to remove asset {} from vendor: {}", assetId, e.getMessage());
            }
        }

        Vendor savedVendor = vendorRepository.save(vendor);
        logger.info("Bulk removed {} assets from vendor successfully", removed);

        return savedVendor;
    }

    /**
     * Get all assets for a vendor
     */
    @Transactional(readOnly = true)
    public Set<Asset> getVendorAssets(String vendorId) {
        logger.debug("Fetching assets for vendor: {}", vendorId);
        Vendor vendor = get(vendorId);
        return vendor.getAssets();
    }

    /**
     * Get asset count for a vendor
     */
    @Transactional(readOnly = true)
    public int getAssetCount(String vendorId) {
        Vendor vendor = get(vendorId);
        return vendor.getAssets().size();
    }

    // ===================== STATISTICS & COUNTS =====================

    /**
     * Get total vendor count
     */
    @Transactional(readOnly = true)
    public long count() {
        return vendorRepository.count();
    }

    /**
     * Count vendors by status
     */
    @Transactional(readOnly = true)
    public long countByStatus(VendorStatus status) {
        return vendorRepository.countByStatus(status);
    }

    /**
     * Get active vendors count
     */
    @Transactional(readOnly = true)
    public long getActiveVendorsCount() {
        return vendorRepository.countByStatus(VendorStatus.ACTIVE);
    }

    /**
     * Get inactive vendors count
     */
    @Transactional(readOnly = true)
    public long getInactiveVendorsCount() {
        return vendorRepository.countByStatus(VendorStatus.INACTIVE);
    }

    // ===================== VALIDATION & BUSINESS LOGIC =====================

    /**
     * Check if vendor email exists
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return vendorRepository.existsByVendorEmail(email);
    }

    /**
     * Check if vendor can be deleted
     */
    @Transactional(readOnly = true)
    public boolean canDelete(String vendorId) {
        Vendor vendor = get(vendorId);
        return vendor.getAssets().isEmpty();
    }

    /**
     * Activate vendor
     */
    public Vendor activate(String vendorId) {
        logger.info("Activating vendor: {}", vendorId);
        return updateStatus(vendorId, "ACTIVE");
    }

    /**
     * Deactivate vendor
     */
    public Vendor deactivate(String vendorId) {
        logger.info("Deactivating vendor: {}", vendorId);
        return updateStatus(vendorId, "INACTIVE");
    }

    /**
     * Suspend vendor
     */
    public Vendor suspend(String vendorId) {
        logger.info("Suspending vendor: {}", vendorId);
        return updateStatus(vendorId, "SUSPENDED");
    }

    /**
     * Blacklist vendor
     */
    public Vendor blacklist(String vendorId) {
        logger.info("Blacklisting vendor: {}", vendorId);
        return updateStatus(vendorId, "BLACKLISTED");
    }

    // ===================== ADVANCED QUERIES =====================

    /**
     * Get top rated vendors
     */
    @Transactional(readOnly = true)
    public List<Vendor> getTopRatedVendors(int limit) {
        logger.debug("Fetching top {} rated vendors", limit);
        return vendorRepository.findAll().stream()
                .filter(v -> v.getRating() != null)
                .sorted((v1, v2) -> Double.compare(
                        v2.getRating() != null ? v2.getRating() : 0.0,
                        v1.getRating() != null ? v1.getRating() : 0.0
                ))
                .limit(limit)
                .toList();
    }

    /**
     * Get vendors with most assets
     */
    @Transactional(readOnly = true)
    public List<Vendor> getVendorsWithMostAssets(int limit) {
        logger.debug("Fetching vendors with most assets (limit: {})", limit);
        return vendorRepository.findAll().stream()
                .sorted((v1, v2) -> Integer.compare(
                        v2.getAssets().size(),
                        v1.getAssets().size()
                ))
                .limit(limit)
                .toList();
    }

    /**
     * Get vendors by specialization
     */
    @Transactional(readOnly = true)
    public List<Vendor> getBySpecialization(String specialization) {
        logger.debug("Fetching vendors with specialization: {}", specialization);
        return vendorRepository.findAll().stream()
                .filter(v -> v.getSpecialization() != null &&
                        v.getSpecialization().toLowerCase().contains(specialization.toLowerCase()))
                .toList();
    }

    /**
     * Get vendors without assets
     */
    @Transactional(readOnly = true)
    public List<Vendor> getVendorsWithoutAssets() {
        logger.debug("Fetching vendors without assets");
        return vendorRepository.findAll().stream()
                .filter(v -> v.getAssets().isEmpty())
                .toList();
    }

    /**
     * Get vendor performance summary
     */
    @Transactional(readOnly = true)
    public VendorPerformanceSummary getPerformanceSummary(String vendorId) {
        Vendor vendor = get(vendorId);

        VendorPerformanceSummary summary = new VendorPerformanceSummary();
        summary.setVendorId(vendor.getVendorId());
        summary.setVendorName(vendor.getVendorName());
        summary.setRating(vendor.getRating());
        summary.setAssetCount(vendor.getAssets().size());
        summary.setStatus(vendor.getStatus());
        summary.setSpecialization(vendor.getSpecialization());

        return summary;
    }

    // Inner class for performance summary
    public static class VendorPerformanceSummary {
        private String vendorId;
        private String vendorName;
        private Double rating;
        private Integer assetCount;
        private VendorStatus status;
        private String specialization;

        // Getters and Setters
        public String getVendorId() { return vendorId; }
        public void setVendorId(String vendorId) { this.vendorId = vendorId; }

        public String getVendorName() { return vendorName; }
        public void setVendorName(String vendorName) { this.vendorName = vendorName; }

        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }

        public Integer getAssetCount() { return assetCount; }
        public void setAssetCount(Integer assetCount) { this.assetCount = assetCount; }

        public VendorStatus getStatus() { return status; }
        public void setStatus(VendorStatus status) { this.status = status; }

        public String getSpecialization() { return specialization; }
        public void setSpecialization(String specialization) { this.specialization = specialization; }
    }
}