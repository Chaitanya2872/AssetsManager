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

    private static final Logger logger =
            LoggerFactory.getLogger(AssetService.class);

    private final AssetRepository assetRepository;
    private final VendorRepository vendorRepository;

    public AssetService(
            AssetRepository assetRepository,
            VendorRepository vendorRepository
    ) {
        this.assetRepository = assetRepository;
        this.vendorRepository = vendorRepository;
    }


    // ============================================================
    // ASSET CRUD
    // ============================================================

    public Asset create(
            String name,
            String category,
            String location
    ) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Asset name is required"
            );
        }

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "Asset category is required"
            );
        }

        logger.info(
                "Creating asset: name={}, category={}, location={}",
                name,
                category,
                location
        );

        Asset asset = new Asset();

        asset.setAssetName(name.trim());
        asset.setAssetCategory(category.trim());
        asset.setLocation(
                location == null ? null : location.trim()
        );
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setQuantity(1);

        Asset saved = assetRepository.save(asset);

        logger.info(
                "Asset created successfully: {}",
                saved.getAssetId()
        );

        return saved;
    }


    // ============================================================
    // GET ASSET
    // ============================================================

    @Transactional(readOnly = true)
    public Asset get(String id) {

        if (id == null || id.isBlank()) {
            throw new AssetNotFoundException(id);
        }

        return assetRepository.findById(id)
                .orElseThrow(() -> {

                    logger.error(
                            "Asset not found: {}",
                            id
                    );

                    return new AssetNotFoundException(id);
                });
    }


    // ============================================================
    // GET ALL
    // ============================================================

    @Transactional(readOnly = true)
    public List<Asset> getAll() {

        return assetRepository.findAll();
    }


    // ============================================================
    // UPDATE ASSET
    // ============================================================

    public Asset update(
            String id,
            Asset request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Asset request cannot be null"
            );
        }

        Asset asset = get(id);

        // --------------------------------------------------------
        // BASIC INFORMATION
        // --------------------------------------------------------

        if (request.getAssetName() != null) {
            asset.setAssetName(
                    request.getAssetName().trim()
            );
        }

        if (request.getAssetCategory() != null) {
            asset.setAssetCategory(
                    request.getAssetCategory().trim()
            );
        }

        if (request.getAssetType() != null) {
            asset.setAssetType(
                    request.getAssetType().trim()
            );
        }

        if (request.getStatus() != null) {
            asset.setStatus(request.getStatus());
        }

        if (request.getLocation() != null) {
            asset.setLocation(
                    request.getLocation().trim()
            );
        }

        if (request.getManufacturer() != null) {
            asset.setManufacturer(
                    request.getManufacturer().trim()
            );
        }

        if (request.getBranch() != null) {
            asset.setBranch(
                    request.getBranch().trim()
            );
        }

        // --------------------------------------------------------
        // SERIAL NUMBER
        // --------------------------------------------------------

        if (request.getSerialNumber() != null) {

            String serialNumber =
                    request.getSerialNumber().trim();

            if (!serialNumber.isBlank()
                    && assetRepository.existsBySerialNumberAndAssetIdNot(
                    serialNumber,
                    id
            )) {

                throw new IllegalArgumentException(
                        "Serial number already exists: "
                                + serialNumber
                );
            }

            asset.setSerialNumber(
                    serialNumber.isBlank()
                            ? null
                            : serialNumber
            );
        }

        // --------------------------------------------------------
        // MODEL
        // --------------------------------------------------------

        if (request.getModelNumber() != null) {
            asset.setModelNumber(
                    request.getModelNumber().trim()
            );
        }

        // --------------------------------------------------------
        // DESCRIPTION
        // --------------------------------------------------------

        if (request.getDescription() != null) {
            asset.setDescription(
                    request.getDescription()
            );
        }

        // --------------------------------------------------------
        // INSTALLATION / QUANTITY
        // --------------------------------------------------------

        if (request.getDateOfInstallation() != null) {
            asset.setDateOfInstallation(
                    request.getDateOfInstallation()
            );
        }

        if (request.getQuantity() != null) {

            if (request.getQuantity() < 1) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than zero"
                );
            }

            asset.setQuantity(
                    request.getQuantity()
            );
        }

        // --------------------------------------------------------
        // ASSIGNMENT
        // --------------------------------------------------------

        if (request.getAssignedTo() != null) {
            asset.setAssignedTo(
                    request.getAssignedTo().trim()
            );
        }

        // --------------------------------------------------------
        // VALUE
        // --------------------------------------------------------

        if (request.getValue() != null) {

            if (request.getValue().signum() < 0) {
                throw new IllegalArgumentException(
                        "Asset value cannot be negative"
                );
            }

            asset.setValue(
                    request.getValue()
            );
        }

        // --------------------------------------------------------
        // VENDOR INFORMATION
        // --------------------------------------------------------

        if (request.getVendorId() != null) {
            asset.setVendorId(
                    request.getVendorId().trim()
            );
        }

        if (request.getVendorName() != null) {
            asset.setVendorName(
                    request.getVendorName().trim()
            );
        }

        if (request.getVendorEmail() != null) {
            asset.setVendorEmail(
                    request.getVendorEmail().trim()
            );
        }

        if (request.getVendorPhone() != null) {
            asset.setVendorPhone(
                    request.getVendorPhone().trim()
            );
        }

        // --------------------------------------------------------
        // DLP
        // --------------------------------------------------------

        if (request.getDlpEndDate() != null) {
            asset.setDlpEndDate(
                    request.getDlpEndDate()
            );
        }

        // --------------------------------------------------------
        // WARRANTY
        // --------------------------------------------------------

        if (request.getWarrantyStartDate() != null) {
            asset.setWarrantyStartDate(
                    request.getWarrantyStartDate()
            );
        }

        if (request.getWarrantyEndDate() != null) {
            asset.setWarrantyEndDate(
                    request.getWarrantyEndDate()
            );
        }

        // --------------------------------------------------------
        // CONTRACT
        // --------------------------------------------------------

        if (request.getVendorContractStart() != null) {
            asset.setVendorContractStart(
                    request.getVendorContractStart()
            );
        }

        if (request.getVendorContractEnd() != null) {
            asset.setVendorContractEnd(
                    request.getVendorContractEnd()
            );
        }

        validateDates(asset);

        return assetRepository.save(asset);
    }

    public Asset updateImageUrl(String id, String imageUrl) {
        Asset asset = get(id);
        asset.setAssetImageUrl(imageUrl);
        return assetRepository.save(asset);
    }


    // ============================================================
    // STATUS
    // ============================================================

    public Asset updateStatus(
            String id,
            AssetStatus status
    ) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Asset status is required"
            );
        }

        Asset asset = get(id);

        asset.setStatus(status);

        return assetRepository.save(asset);
    }


    // ============================================================
    // ASSIGN TO EMPLOYEE
    // ============================================================

    public Asset assignToEmployee(
            String id,
            String assignedTo
    ) {

        String employee = normalize(assignedTo);

        if (employee == null) {
            throw new IllegalArgumentException(
                    "Employee to assign the asset to is required"
            );
        }

        Asset asset = get(id);

        asset.setAssignedTo(employee);
        asset.setStatus(AssetStatus.IN_USE);

        return assetRepository.save(asset);
    }


    // ============================================================
    // DELETE
    // ============================================================

    public void delete(String id) {

        Asset asset = get(id);

        /*
         * Remove vendor relationships first.
         * This prevents many-to-many foreign-key problems.
         */
        if (asset.getVendors() != null
                && !asset.getVendors().isEmpty()) {

            for (Vendor vendor :
                    new HashSet<>(asset.getVendors())) {

                vendor.removeAsset(asset);
                vendorRepository.save(vendor);
            }
        }

        assetRepository.delete(asset);

        logger.info(
                "Asset deleted successfully: {}",
                id
        );
    }


    // ============================================================
    // SEARCH & FILTER
    // ============================================================

    @Transactional(readOnly = true)
    public List<Asset> searchAssets(
            String name,
            String category,
            AssetStatus status
    ) {

        return assetRepository.findByFilters(
                normalize(name),
                normalize(category),
                status,
                null
        );
    }


    @Transactional(readOnly = true)
    public List<Asset> searchAssets(
            String name,
            String category,
            AssetStatus status,
            String location
    ) {

        return assetRepository.findByFilters(
                normalize(name),
                normalize(category),
                status,
                normalize(location)
        );
    }


    @Transactional(readOnly = true)
    public List<Asset> searchByText(
            String query
    ) {

        if (query == null || query.isBlank()) {
            return getAll();
        }

        return assetRepository.searchAssets(
                query.trim()
        );
    }


    // ============================================================
    // FILTER HELPERS
    // ============================================================

    @Transactional(readOnly = true)
    public List<Asset> getByCategory(
            String category
    ) {

        return assetRepository.findByAssetCategory(
                category
        );
    }


    @Transactional(readOnly = true)
    public List<Asset> getAssetsByLocation(
            String location
    ) {

        return assetRepository.findByLocation(
                location
        );
    }


    @Transactional(readOnly = true)
    public List<Asset> getAssetsByVendor(
            String vendorId
    ) {

        return assetRepository.findByVendorId(
                vendorId
        );
    }


    @Transactional(readOnly = true)
    public List<Asset> getByStatus(
            AssetStatus status
    ) {

        return assetRepository.findByStatus(
                status
        );
    }


    @Transactional(readOnly = true)
    public List<Asset> getByBranch(
            String branch
    ) {

        return assetRepository.findByBranch(
                branch
        );
    }


    @Transactional(readOnly = true)
    public List<Asset> getByManufacturer(
            String manufacturer
    ) {

        return assetRepository.findByManufacturer(
                manufacturer
        );
    }


    // ============================================================
    // VENDOR RELATIONSHIP
    // ============================================================

    public Asset assignVendor(
            String assetId,
            String vendorId
    ) {

        Asset asset = get(assetId);

        Vendor vendor =
                vendorRepository.findById(vendorId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Vendor not found: "
                                                + vendorId
                                )
                        );

        vendor.addAsset(asset);

        // Keep the denormalized vendor fields in sync.
        asset.setVendorId(
                vendor.getVendorId()
        );

        asset.setVendorName(
                vendor.getVendorName()
        );

        asset.setVendorEmail(
                vendor.getVendorEmail()
        );

        asset.setVendorPhone(
                vendor.getVendorPhone()
        );

        vendorRepository.save(vendor);
        assetRepository.save(asset);

        return asset;
    }


    public Asset removeVendor(
            String assetId,
            String vendorId
    ) {

        Asset asset = get(assetId);

        Vendor vendor =
                vendorRepository.findById(vendorId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Vendor not found: "
                                                + vendorId
                                )
                        );

        vendor.removeAsset(asset);

        /*
         * Clear the denormalized vendor fields only when
         * this vendor is actually being removed.
         */
        if (Objects.equals(
                asset.getVendorId(),
                vendorId
        )) {

            asset.setVendorId(null);
            asset.setVendorName(null);
            asset.setVendorEmail(null);
            asset.setVendorPhone(null);
        }

        vendorRepository.save(vendor);

        return assetRepository.save(asset);
    }


    // ============================================================
    // BULK VENDOR ASSIGNMENT
    // ============================================================

    public int bulkAssignVendor(
            List<String> assetIds,
            String vendorId,
            String vendorName,
            String vendorEmail,
            String vendorPhone
    ) {

        if (assetIds == null || assetIds.isEmpty()) {
            return 0;
        }

        int count = 0;

        for (String assetId : assetIds) {

            if (assetId == null || assetId.isBlank()) {
                continue;
            }

            try {

                Asset asset = get(assetId);

                asset.setVendorId(
                        normalize(vendorId)
                );

                asset.setVendorName(
                        normalize(vendorName)
                );

                asset.setVendorEmail(
                        normalize(vendorEmail)
                );

                asset.setVendorPhone(
                        normalize(vendorPhone)
                );

                assetRepository.save(asset);

                count++;

            } catch (Exception e) {

                logger.error(
                        "Failed to assign vendor to asset {}: {}",
                        assetId,
                        e.getMessage()
                );
            }
        }

        return count;
    }


    // ============================================================
    // VENDOR LOOKUP
    // ============================================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVendorLookup() {

        return assetRepository
                .getVendorStatistics()
                .stream()
                .map(row -> {

                    Map<String, Object> map =
                            new LinkedHashMap<>();

                    map.put("vendorId", row[0]);
                    map.put("vendorName", row[1]);
                    map.put("vendorEmail", row[2]);
                    map.put("vendorPhone", row[3]);
                    map.put("assetCount", row[4]);

                    return map;
                })
                .collect(Collectors.toList());
    }


    // ============================================================
    // VENDOR STATISTICS
    // ============================================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVendorStats() {

        return assetRepository
                .getVendorStatistics()
                .stream()
                .map(row -> {

                    Map<String, Object> map =
                            new LinkedHashMap<>();

                    map.put("vendorId", row[0]);
                    map.put("vendorName", row[1]);
                    map.put("vendorEmail", row[2]);
                    map.put("vendorPhone", row[3]);
                    map.put("assetCount", row[4]);

                    return map;
                })
                .collect(Collectors.toList());
    }


    // ============================================================
    // DLP ALERTS
    // ============================================================

    @Transactional(readOnly = true)
    public Map<String, Object> getDlpAlertDashboard() {

        Map<String, Object> dashboard =
                new LinkedHashMap<>();

        dashboard.put(
                "expired",
                getDlpExpired()
        );

        dashboard.put(
                "critical",
                getDlpExpiringIn7Days()
        );

        dashboard.put(
                "warning",
                getDlpExpiringIn30Days()
        );

        dashboard.put(
                "counts",
                getDlpAlertCounts()
        );

        return dashboard;
    }


    @Transactional(readOnly = true)
    public List<Asset> getDlpExpired() {

        return assetRepository.findDlpExpired();
    }


    @Transactional(readOnly = true)
    public List<Asset> getDlpExpiringWithin(
            int days
    ) {

        if (days < 0) {
            throw new IllegalArgumentException(
                    "Days cannot be negative"
            );
        }

        LocalDate endDate =
                LocalDate.now().plusDays(days);

        return assetRepository.findDlpExpiringBetween(
                endDate
        );
    }


    @Transactional(readOnly = true)
    public List<Asset> getDlpExpiringIn7Days() {

        return getDlpExpiringWithin(7);
    }


    @Transactional(readOnly = true)
    public List<Asset> getDlpExpiringIn30Days() {

        return getDlpExpiringWithin(30);
    }


    @Transactional(readOnly = true)
    public Map<String, Long> getDlpAlertCounts() {

        Map<String, Long> counts =
                new LinkedHashMap<>();

        counts.put(
                "expired",
                (long) getDlpExpired().size()
        );

        counts.put(
                "critical",
                (long) getDlpExpiringIn7Days().size()
        );

        counts.put(
                "warning",
                (long) getDlpExpiringIn30Days().size()
        );

        return counts;
    }


    public Asset updateDlpEndDate(
            String assetId,
            LocalDate dlpEndDate
    ) {

        Asset asset = get(assetId);

        asset.setDlpEndDate(dlpEndDate);

        return assetRepository.save(asset);
    }


    @Transactional(readOnly = true)
    public boolean hasExpiredDlp(
            String assetId
    ) {

        return get(assetId).isDlpExpired();
    }


    @Transactional(readOnly = true)
    public Asset.DlpAlertLevel getDlpAlertLevel(
            String assetId
    ) {

        return get(assetId).getDlpAlertLevel();
    }


    // ============================================================
    // WARRANTY
    // ============================================================

    @Transactional(readOnly = true)
    public List<Asset> getWarrantyExpired() {

        return assetRepository.findWarrantyExpired();
    }


    @Transactional(readOnly = true)
    public List<Asset> getWarrantyExpiringWithin(
            int days
    ) {

        if (days < 0) {
            throw new IllegalArgumentException(
                    "Days cannot be negative"
            );
        }

        LocalDate endDate =
                LocalDate.now().plusDays(days);

        return assetRepository.findWarrantyExpiringBefore(
                endDate
        );
    }


    public Asset updateWarranty(
            String assetId,
            LocalDate start,
            LocalDate end
    ) {

        validateDateRange(
                start,
                end,
                "Warranty"
        );

        Asset asset = get(assetId);

        asset.setWarrantyStartDate(start);
        asset.setWarrantyEndDate(end);

        return assetRepository.save(asset);
    }


    // ============================================================
    // VENDOR CONTRACT
    // ============================================================

    @Transactional(readOnly = true)
    public List<Asset> getVendorContractExpired() {

        return assetRepository.findContractExpired();
    }


    @Transactional(readOnly = true)
    public List<Asset> getVendorContractExpiringWithin(
            int days
    ) {

        if (days < 0) {
            throw new IllegalArgumentException(
                    "Days cannot be negative"
            );
        }

        LocalDate endDate =
                LocalDate.now().plusDays(days);

        return assetRepository.findContractExpiringBefore(
                endDate
        );
    }


    public Asset updateVendorContract(
            String assetId,
            LocalDate start,
            LocalDate end
    ) {

        validateDateRange(
                start,
                end,
                "Vendor contract"
        );

        Asset asset = get(assetId);

        asset.setVendorContractStart(start);
        asset.setVendorContractEnd(end);

        return assetRepository.save(asset);
    }


    // ============================================================
    // LOOKUPS
    // ============================================================

    @Transactional(readOnly = true)
    public List<String> getDistinctCategories() {

        return assetRepository.findDistinctCategories();
    }


    @Transactional(readOnly = true)
    public List<String> getDistinctLocations() {

        return assetRepository.findDistinctLocations();
    }


    @Transactional(readOnly = true)
    public List<String> getDistinctBranches() {

        return assetRepository.findDistinctBranches();
    }


    @Transactional(readOnly = true)
    public List<String> getDistinctManufacturers() {

        return assetRepository.findDistinctManufacturers();
    }


    // ============================================================
    // VENDOR IDS
    // ============================================================

    @Transactional(readOnly = true)
    public List<String> getDistinctVendorIds() {

        return assetRepository.findDistinctVendorIds();
    }


    // ============================================================
    // STATISTICS
    // ============================================================

    @Transactional(readOnly = true)
    public long getTotalAssetCount() {

        return assetRepository.count();
    }


    @Transactional(readOnly = true)
    public long getAssetCountByStatus(
            AssetStatus status
    ) {

        return assetRepository.countByStatus(status);
    }


    @Transactional(readOnly = true)
    public long getAssetCountByCategory(
            String category
    ) {

        return assetRepository.countByAssetCategory(
                category
        );
    }


    @Transactional(readOnly = true)
    public long getAssetCountByLocation(
            String location
    ) {

        return assetRepository.countByLocation(
                location
        );
    }


    @Transactional(readOnly = true)
    public long getAssetCountByVendor(
            String vendorId
    ) {

        return assetRepository.countByVendorId(
                vendorId
        );
    }


    @Transactional(readOnly = true)
    public long getAssignedAssetCount() {

        return assetRepository.countAssignedAssets();
    }


    @Transactional(readOnly = true)
    public long getUnassignedAssetCount() {

        return assetRepository.countUnassignedAssets();
    }


    // ============================================================
    // VALIDATION
    // ============================================================

    @Transactional(readOnly = true)
    public boolean serialNumberExists(
            String serialNumber
    ) {

        if (serialNumber == null
                || serialNumber.isBlank()) {

            return false;
        }

        return assetRepository.existsBySerialNumber(
                serialNumber.trim()
        );
    }


    @Transactional(readOnly = true)
    public boolean serialNumberExistsForAnotherAsset(
            String serialNumber,
            String assetId
    ) {

        if (serialNumber == null
                || serialNumber.isBlank()) {

            return false;
        }

        return assetRepository
                .existsBySerialNumberAndAssetIdNot(
                        serialNumber.trim(),
                        assetId
                );
    }


    @Transactional(readOnly = true)
    public boolean exists(
            String assetId
    ) {

        return assetId != null
                && !assetId.isBlank()
                && assetRepository.existsById(assetId);
    }


    public boolean isValidAsset(
            Asset asset
    ) {

        return asset != null
                && asset.getAssetName() != null
                && !asset.getAssetName().isBlank()
                && asset.getAssetCategory() != null
                && !asset.getAssetCategory().isBlank();
    }


    // ============================================================
    // INTERNAL VALIDATION HELPERS
    // ============================================================

    private String normalize(String value) {

        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isBlank()
                ? null
                : normalized;
    }


    private void validateDates(Asset asset) {

        if (asset.getWarrantyStartDate() != null
                && asset.getWarrantyEndDate() != null
                && asset.getWarrantyEndDate()
                .isBefore(asset.getWarrantyStartDate())) {

            throw new IllegalArgumentException(
                    "Warranty end date cannot be before warranty start date"
            );
        }

        if (asset.getVendorContractStart() != null
                && asset.getVendorContractEnd() != null
                && asset.getVendorContractEnd()
                .isBefore(asset.getVendorContractStart())) {

            throw new IllegalArgumentException(
                    "Vendor contract end date cannot be before start date"
            );
        }
    }


    private void validateDateRange(
            LocalDate start,
            LocalDate end,
            String fieldName
    ) {

        if (start != null
                && end != null
                && end.isBefore(start)) {

            throw new IllegalArgumentException(
                    fieldName
                            + " end date cannot be before start date"
            );
        }
    }
}
