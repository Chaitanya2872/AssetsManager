package com.bmsedge.asset.service;

import com.bmsedge.asset.exception.AssetNotFoundException;
import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.AssetStatus;
import com.bmsedge.asset.model.Vendor;
import com.bmsedge.asset.repository.AssetRepository;
import com.bmsedge.asset.repository.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssetService {

    private static final Logger logger = LoggerFactory.getLogger(AssetService.class);

    private final AssetRepository assetRepository;
    private final VendorRepository vendorRepository;

    public AssetService(AssetRepository assetRepository,
                        VendorRepository vendorRepository) {
        this.assetRepository = assetRepository;
        this.vendorRepository = vendorRepository;
    }

    // ============================================================
    // ASSET CRUD OPERATIONS
    // ============================================================

    /**
     * Create a new asset with basic information
     */
    public Asset create(String name, String category, String location) {
        logger.info("Creating new asset: name={}, category={}, location={}", name, category, location);

        Asset asset = new Asset();
        asset.setAssetName(name);
        asset.setAssetCategory(category);
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setLocation(location);

        Asset saved = assetRepository.save(asset);
        logger.info("Asset created successfully with ID: {}", saved.getAssetId());

        return saved;
    }

    /**
     * Get asset by ID
     */
    @Transactional(readOnly = true)
    public Asset get(String id) {
        logger.debug("Fetching asset with ID: {}", id);
        return assetRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Asset not found with ID: {}", id);
                    return new AssetNotFoundException(id);
                });
    }

    /**
     * Get all assets
     */
    @Transactional(readOnly = true)
    public List<Asset> getAll() {
        logger.debug("Fetching all assets");
        return assetRepository.findAll();
    }

    /**
     * Update asset with partial or full data
     */
    public Asset update(String id, Asset request) {
        logger.info("Updating asset with ID: {}", id);

        Asset asset = get(id);

        // Update basic information
        if (request.getAssetName() != null) {
            asset.setAssetName(request.getAssetName());
        }
        if (request.getAssetCategory() != null) {
            asset.setAssetCategory(request.getAssetCategory());
        }
        if (request.getAssetType() != null) {
            asset.setAssetType(request.getAssetType());
        }
        if (request.getStatus() != null) {
            asset.setStatus(request.getStatus());
        }
        if (request.getLocation() != null) {
            asset.setLocation(request.getLocation());
        }
        if (request.getManufacturer() != null) {
            asset.setManufacturer(request.getManufacturer());
        }
        if (request.getBranch() != null) {
            asset.setBranch(request.getBranch());
        }
        if (request.getSerialNumber() != null) {
            asset.setSerialNumber(request.getSerialNumber());
        }
        if (request.getModelNumber() != null) {
            asset.setModelNumber(request.getModelNumber());
        }
        if (request.getDescription() != null) {
            asset.setDescription(request.getDescription());
        }
        if (request.getDateOfInstallation() != null) {
            asset.setDateOfInstallation(request.getDateOfInstallation());
        }
        if (request.getQuantity() != null) {
            asset.setQuantity(request.getQuantity());
        }

        // Update vendor information
        if (request.getVendorId() != null) {
            asset.setVendorId(request.getVendorId());
        }
        if (request.getVendorName() != null) {
            asset.setVendorName(request.getVendorName());
        }
        if (request.getVendorEmail() != null) {
            asset.setVendorEmail(request.getVendorEmail());
        }
        if (request.getVendorPhone() != null) {
            asset.setVendorPhone(request.getVendorPhone());
        }

        // Update date-related fields
        if (request.getDlpEndDate() != null) {
            asset.setDlpEndDate(request.getDlpEndDate());
        }
        if (request.getWarrantyStartDate() != null) {
            asset.setWarrantyStartDate(request.getWarrantyStartDate());
        }
        if (request.getWarrantyEndDate() != null) {
            asset.setWarrantyEndDate(request.getWarrantyEndDate());
        }
        if (request.getVendorContractStart() != null) {
            asset.setVendorContractStart(request.getVendorContractStart());
        }
        if (request.getVendorContractEnd() != null) {
            asset.setVendorContractEnd(request.getVendorContractEnd());
        }

        Asset saved = assetRepository.save(asset);
        logger.info("Asset updated successfully: {}", id);

        return saved;
    }

    /**
     * Update asset status
     */
    public Asset updateStatus(String id, AssetStatus status) {
        logger.info("Updating status for asset {}: {}", id, status);

        Asset asset = get(id);
        asset.setStatus(status);

        return assetRepository.save(asset);
    }

    /**
     * Delete asset
     */
    public void delete(String id) {
        logger.info("Deleting asset with ID: {}", id);

        if (!assetRepository.existsById(id)) {
            logger.error("Asset not found with ID: {}", id);
            throw new AssetNotFoundException(id);
        }

        assetRepository.deleteById(id);
        logger.info("Asset deleted successfully: {}", id);
    }

    // ============================================================
    // SEARCH & FILTER OPERATIONS
    // ============================================================

    /**
     * Search assets with multiple filters
     */
    @Transactional(readOnly = true)
    public List<Asset> searchAssets(String name, String category, AssetStatus status) {
        logger.debug("Searching assets: name={}, category={}, status={}", name, category, status);
        return assetRepository.findByFilters(name, category, status);
    }

    /**
     * Get assets by category
     */
    @Transactional(readOnly = true)
    public List<Asset> getByCategory(String category) {
        logger.debug("Fetching assets by category: {}", category);
        return assetRepository.findByAssetCategory(category);
    }

    /**
     * Get assets by location
     */
    @Transactional(readOnly = true)
    public List<Asset> getAssetsByLocation(String location) {
        logger.debug("Fetching assets by location: {}", location);
        return assetRepository.findByLocation(location);
    }

    /**
     * Get assets by vendor ID
     */
    @Transactional(readOnly = true)
    public List<Asset> getAssetsByVendor(String vendorId) {
        logger.debug("Fetching assets by vendor: {}", vendorId);
        return assetRepository.findByVendorId(vendorId);
    }

    /**
     * Get assets by status
     */
    @Transactional(readOnly = true)
    public List<Asset> getByStatus(AssetStatus status) {
        logger.debug("Fetching assets by status: {}", status);
        return assetRepository.findByStatus(status);
    }

    /**
     * Get assets by branch
     */
    @Transactional(readOnly = true)
    public List<Asset> getByBranch(String branch) {
        logger.debug("Fetching assets by branch: {}", branch);
        return assetRepository.findByBranch(branch);
    }

    /**
     * Get assets by manufacturer
     */
    @Transactional(readOnly = true)
    public List<Asset> getByManufacturer(String manufacturer) {
        logger.debug("Fetching assets by manufacturer: {}", manufacturer);
        return assetRepository.findByManufacturer(manufacturer);
    }

    // ============================================================
    // VENDOR RELATIONSHIP MANAGEMENT
    // ============================================================

    /**
     * Assign vendor to asset (Many-to-Many)
     */
    public Asset assignVendor(String assetId, String vendorId) {
        logger.info("Assigning vendor {} to asset {}", vendorId, assetId);

        Asset asset = get(assetId);
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found: " + vendorId));

        vendor.addAsset(asset); // Helper keeps both sides in sync
        vendorRepository.save(vendor);

        logger.info("Vendor assigned successfully");
        return asset;
    }

    /**
     * Remove vendor from asset
     */
    public Asset removeVendor(String assetId, String vendorId) {
        logger.info("Removing vendor {} from asset {}", vendorId, assetId);

        Asset asset = get(assetId);
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found: " + vendorId));

        vendor.removeAsset(asset);
        vendorRepository.save(vendor);

        logger.info("Vendor removed successfully");
        return asset;
    }

    /**
     * Bulk assign vendor to multiple assets
     */
    public int bulkAssignVendor(List<String> assetIds, String vendorId, String vendorName,
                                String vendorEmail, String vendorPhone) {
        logger.info("Bulk assigning vendor {} to {} assets", vendorId, assetIds.size());

        int count = 0;

        for (String assetId : assetIds) {
            try {
                Asset asset = get(assetId);
                asset.setVendorId(vendorId);
                asset.setVendorName(vendorName);
                asset.setVendorEmail(vendorEmail);
                asset.setVendorPhone(vendorPhone);
                assetRepository.save(asset);
                count++;
            } catch (Exception e) {
                logger.error("Failed to assign vendor to asset {}: {}", assetId, e.getMessage());
            }
        }

        logger.info("Bulk vendor assignment completed: {} of {} assets updated", count, assetIds.size());
        return count;
    }

    // ============================================================
    // VENDOR LOOKUPS & STATISTICS
    // ============================================================

    /**
     * Get vendor lookup list (distinct vendors from assets)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVendorLookup() {
        logger.debug("Fetching vendor lookup list");

        return assetRepository.findAll().stream()
                .filter(a -> a.getVendorId() != null)
                .collect(Collectors.groupingBy(Asset::getVendorId))
                .entrySet().stream()
                .map(entry -> {
                    Asset firstAsset = entry.getValue().get(0);
                    Map<String, Object> map = new HashMap<>();
                    map.put("vendorId", firstAsset.getVendorId());
                    map.put("vendorName", firstAsset.getVendorName());
                    map.put("vendorEmail", firstAsset.getVendorEmail());
                    map.put("vendorPhone", firstAsset.getVendorPhone());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get vendor statistics
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVendorStats() {
        logger.debug("Fetching vendor statistics");

        return assetRepository.findAll().stream()
                .filter(a -> a.getVendorId() != null)
                .collect(Collectors.groupingBy(Asset::getVendorId))
                .entrySet().stream()
                .map(entry -> {
                    Asset firstAsset = entry.getValue().get(0);
                    Map<String, Object> map = new HashMap<>();
                    map.put("vendorId", firstAsset.getVendorId());
                    map.put("vendorName", firstAsset.getVendorName());
                    map.put("vendorEmail", firstAsset.getVendorEmail());
                    map.put("assetCount", entry.getValue().size());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // ============================================================
    // DLP ALERT SYSTEM
    // ============================================================

    /**
     * Get DLP alert dashboard
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDlpAlertDashboard() {
        logger.debug("Fetching DLP alert dashboard");

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("expired", getDlpExpired());
        dashboard.put("critical", getDlpExpiringIn7Days());
        dashboard.put("warning", getDlpExpiringIn30Days());
        dashboard.put("counts", getDlpAlertCounts());

        return dashboard;
    }

    /**
     * Get assets with expired DLP
     */
    @Transactional(readOnly = true)
    public List<Asset> getDlpExpired() {
        logger.debug("Fetching expired DLP assets");
        return assetRepository.findDlpExpired();
    }

    /**
     * Get assets with DLP expiring within specified days
     */
    @Transactional(readOnly = true)
    public List<Asset> getDlpExpiringWithin(int days) {
        logger.debug("Fetching DLP expiring within {} days", days);
        LocalDate endDate = LocalDate.now().plusDays(days);
        return assetRepository.findDlpExpiringBetween(endDate);
    }

    /**
     * Get assets with DLP expiring in next 7 days (critical)
     */
    @Transactional(readOnly = true)
    public List<Asset> getDlpExpiringIn7Days() {
        return getDlpExpiringWithin(7);
    }

    /**
     * Get assets with DLP expiring in next 30 days
     */
    @Transactional(readOnly = true)
    public List<Asset> getDlpExpiringIn30Days() {
        return getDlpExpiringWithin(30);
    }

    /**
     * Get DLP alert counts by level
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getDlpAlertCounts() {
        logger.debug("Fetching DLP alert counts");

        Map<String, Long> counts = new HashMap<>();
        counts.put("expired", (long) getDlpExpired().size());
        counts.put("critical", (long) getDlpExpiringIn7Days().size());
        counts.put("warning", (long) getDlpExpiringIn30Days().size());

        return counts;
    }

    /**
     * Update DLP end date for an asset
     */
    public Asset updateDlpEndDate(String assetId, LocalDate dlpEndDate) {
        logger.info("Updating DLP end date for asset {}: {}", assetId, dlpEndDate);

        Asset asset = get(assetId);
        asset.setDlpEndDate(dlpEndDate);

        return assetRepository.save(asset);
    }

    /**
     * Check if asset has expired DLP
     */
    @Transactional(readOnly = true)
    public boolean hasExpiredDlp(String assetId) {
        return get(assetId).isDlpExpired();
    }

    /**
     * Get DLP alert level for an asset
     */
    @Transactional(readOnly = true)
    public Asset.DlpAlertLevel getDlpAlertLevel(String assetId) {
        return get(assetId).getDlpAlertLevel();
    }

    // ============================================================
    // WARRANTY ALERT SYSTEM
    // ============================================================

    /**
     * Get assets with expired warranty
     */
    @Transactional(readOnly = true)
    public List<Asset> getWarrantyExpired() {
        logger.debug("Fetching expired warranty assets");
        return assetRepository.findWarrantyExpired();
    }

    /**
     * Get assets with warranty expiring within specified days
     */
    @Transactional(readOnly = true)
    public List<Asset> getWarrantyExpiringWithin(int days) {
        logger.debug("Fetching warranty expiring within {} days", days);
        LocalDate endDate = LocalDate.now().plusDays(days);
        return assetRepository.findWarrantyExpiringBefore(endDate);
    }

    /**
     * Update warranty dates for an asset
     */
    public Asset updateWarranty(String assetId, LocalDate start, LocalDate end) {
        logger.info("Updating warranty for asset {}: {} to {}", assetId, start, end);

        Asset asset = get(assetId);
        asset.setWarrantyStartDate(start);
        asset.setWarrantyEndDate(end);

        return assetRepository.save(asset);
    }

    // ============================================================
    // VENDOR CONTRACT ALERT SYSTEM
    // ============================================================

    /**
     * Get assets with expired vendor contracts
     */
    @Transactional(readOnly = true)
    public List<Asset> getVendorContractExpired() {
        logger.debug("Fetching expired vendor contracts");
        return assetRepository.findContractExpired();
    }

    /**
     * Get assets with vendor contract expiring within specified days
     */
    @Transactional(readOnly = true)
    public List<Asset> getVendorContractExpiringWithin(int days) {
        logger.debug("Fetching vendor contracts expiring within {} days", days);
        LocalDate endDate = LocalDate.now().plusDays(days);
        return assetRepository.findContractExpiringBefore(endDate);
    }

    /**
     * Update vendor contract dates for an asset
     */
    public Asset updateVendorContract(String assetId, LocalDate start, LocalDate end) {
        logger.info("Updating vendor contract for asset {}: {} to {}", assetId, start, end);

        Asset asset = get(assetId);
        asset.setVendorContractStart(start);
        asset.setVendorContractEnd(end);

        return assetRepository.save(asset);
    }

    // ============================================================
    // LOOKUPS FOR DROPDOWNS
    // ============================================================

    /**
     * Get distinct categories
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctCategories() {
        logger.debug("Fetching distinct categories");
        return assetRepository.findDistinctCategories();
    }

    /**
     * Get distinct locations
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctLocations() {
        logger.debug("Fetching distinct locations");
        return assetRepository.findDistinctLocations();
    }

    /**
     * Get distinct branches
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctBranches() {
        logger.debug("Fetching distinct branches");
        return assetRepository.findDistinctBranches();
    }

    /**
     * Get distinct manufacturers
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctManufacturers() {
        logger.debug("Fetching distinct manufacturers");
        return assetRepository.findDistinctManufacturers();
    }

    // ============================================================
    // STATISTICS & ANALYTICS
    // ============================================================

    /**
     * Get total asset count
     */
    @Transactional(readOnly = true)
    public long getTotalAssetCount() {
        return assetRepository.count();
    }

    /**
     * Get asset count by status
     */
    @Transactional(readOnly = true)
    public long getAssetCountByStatus(AssetStatus status) {
        return assetRepository.countByStatus(status);
    }

    /**
     * Get asset count by category
     */
    @Transactional(readOnly = true)
    public long getAssetCountByCategory(String category) {
        return assetRepository.countByAssetCategory(category);
    }

    /**
     * Get asset count by location
     */
    @Transactional(readOnly = true)
    public long getAssetCountByLocation(String location) {
        return assetRepository.countByLocation(location);
    }

    /**
     * Get asset count by vendor
     */
    @Transactional(readOnly = true)
    public long getAssetCountByVendor(String vendorId) {
        return assetRepository.countByVendorId(vendorId);
    }

    // ============================================================
    // VALIDATION & BUSINESS LOGIC
    // ============================================================

    /**
     * Check if serial number exists
     */
    @Transactional(readOnly = true)
    public boolean serialNumberExists(String serialNumber) {
        return assetRepository.findBySerialNumber(serialNumber).isPresent();
    }

    /**
     * Check if asset exists
     */
    @Transactional(readOnly = true)
    public boolean exists(String assetId) {
        return assetRepository.existsById(assetId);
    }

    /**
     * Validate asset for creation/update
     */
    public boolean isValidAsset(Asset asset) {
        return asset != null &&
                asset.getAssetName() != null && !asset.getAssetName().isBlank() &&
                asset.getAssetCategory() != null && !asset.getAssetCategory().isBlank();
    }
}