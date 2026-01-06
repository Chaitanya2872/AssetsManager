package com.bmsedge.asset.controllers;

import com.bmsedge.asset.dto.AssetUpdateRequest;
import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.AssetStatus;
import com.bmsedge.asset.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    // ============================================================
    // BASIC CRUD OPERATIONS
    // ============================================================

    /**
     * Get all assets
     * GET /api/assets
     */
    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        List<Asset> assets = assetService.getAll();
        return ResponseEntity.ok(assets);
    }

    /**
     * Get asset by ID
     * GET /api/assets/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable String id) {
        Asset asset = assetService.get(id);
        return ResponseEntity.ok(asset);
    }

    /**
     * Create new asset
     * POST /api/assets
     */
    @PostMapping
    public ResponseEntity<Asset> createAsset(@Valid @RequestBody Asset asset) {
        // Create base asset with required fields
        Asset created = assetService.create(
                asset.getAssetName(),
                asset.getAssetCategory(),
                asset.getLocation()
        );

        // Set additional fields if provided
        if (asset.getModelNumber() != null) created.setModelNumber(asset.getModelNumber());
        if (asset.getSerialNumber() != null) created.setSerialNumber(asset.getSerialNumber());
        if (asset.getAssetType() != null) created.setAssetType(asset.getAssetType());
        if (asset.getManufacturer() != null) created.setManufacturer(asset.getManufacturer());
        if (asset.getDateOfInstallation() != null) created.setDateOfInstallation(asset.getDateOfInstallation());
        if (asset.getDescription() != null) created.setDescription(asset.getDescription());
        if (asset.getBranch() != null) created.setBranch(asset.getBranch());
        if (asset.getDlpEndDate() != null) created.setDlpEndDate(asset.getDlpEndDate());
        if (asset.getQuantity() != null) created.setQuantity(asset.getQuantity());
        if (asset.getWarrantyStartDate() != null) created.setWarrantyStartDate(asset.getWarrantyStartDate());
        if (asset.getWarrantyEndDate() != null) created.setWarrantyEndDate(asset.getWarrantyEndDate());
        if (asset.getVendorContractStart() != null) created.setVendorContractStart(asset.getVendorContractStart());
        if (asset.getVendorContractEnd() != null) created.setVendorContractEnd(asset.getVendorContractEnd());

        // Set vendor information if provided
        if (asset.getVendorId() != null) created.setVendorId(asset.getVendorId());
        if (asset.getVendorName() != null) created.setVendorName(asset.getVendorName());
        if (asset.getVendorEmail() != null) created.setVendorEmail(asset.getVendorEmail());
        if (asset.getVendorPhone() != null) created.setVendorPhone(asset.getVendorPhone());

        // Save with all updates
        Asset saved = assetService.update(created.getAssetId(), created);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Update asset
     * PUT /api/assets/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(
            @PathVariable String id,
            @Valid @RequestBody Asset asset) {
        Asset updated = assetService.update(id, asset);
        return ResponseEntity.ok(updated);
    }

    /**
     * Partially update asset
     * PATCH /api/assets/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Asset> partialUpdateAsset(
            @PathVariable String id,
            @RequestBody AssetUpdateRequest request) {
        Asset asset = assetService.get(id);

        // Create temporary asset with updates
        Asset updates = new Asset();
        if (request.getAssetName() != null) updates.setAssetName(request.getAssetName());
        if (request.getCategory() != null) updates.setAssetCategory(request.getCategory());
        if (request.getAssetType() != null) updates.setAssetType(request.getAssetType());
        if (request.getStatus() != null) updates.setStatus(request.getStatus());
        if (request.getLocation() != null) updates.setLocation(request.getLocation());
        if (request.getManufacturer() != null) updates.setManufacturer(request.getManufacturer());
        if (request.getBranch() != null) updates.setBranch(request.getBranch());
        if (request.getSerialNumber() != null) updates.setSerialNumber(request.getSerialNumber());
        if (request.getModelNumber() != null) updates.setModelNumber(request.getModelNumber());
        if (request.getDescription() != null) updates.setDescription(request.getDescription());
        if (request.getDateOfInstallation() != null) updates.setDateOfInstallation(request.getDateOfInstallation());
        if (request.getQuantity() != null) updates.setQuantity(request.getQuantity());
        if (request.getDlpEndDate() != null) updates.setDlpEndDate(request.getDlpEndDate());
        if (request.getWarrantyStartDate() != null) updates.setWarrantyStartDate(request.getWarrantyStartDate());
        if (request.getWarrantyEndDate() != null) updates.setWarrantyEndDate(request.getWarrantyEndDate());
        if (request.getVendorContractStart() != null) updates.setVendorContractStart(request.getVendorContractStart());
        if (request.getVendorContractEnd() != null) updates.setVendorContractEnd(request.getVendorContractEnd());

        Asset updated = assetService.update(id, updates);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete asset
     * DELETE /api/assets/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAsset(@PathVariable String id) {
        assetService.delete(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Asset deleted successfully");
        response.put("assetId", id);

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // STATUS MANAGEMENT
    // ============================================================

    /**
     * Update asset status
     * PATCH /api/assets/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Asset> updateAssetStatus(
            @PathVariable String id,
            @RequestParam AssetStatus status) {
        Asset updated = assetService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    // ============================================================
    // SEARCH & FILTER
    // ============================================================

    /**
     * Search assets with filters
     * GET /api/assets/search?name=...&category=...&status=...
     */
    @GetMapping("/search")
    public ResponseEntity<List<Asset>> searchAssets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) AssetStatus status) {
        List<Asset> assets = assetService.searchAssets(name, category, status);
        return ResponseEntity.ok(assets);
    }

    /**
     * Get assets by category
     * GET /api/assets/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Asset>> getAssetsByCategory(@PathVariable String category) {
        List<Asset> assets = assetService.getByCategory(category);
        return ResponseEntity.ok(assets);
    }

    /**
     * Get assets by location
     * GET /api/assets/location/{location}
     */
    @GetMapping("/location/{location}")
    public ResponseEntity<List<Asset>> getAssetsByLocation(@PathVariable String location) {
        List<Asset> assets = assetService.getAssetsByLocation(location);
        return ResponseEntity.ok(assets);
    }

    /**
     * Get assets by status
     * GET /api/assets/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Asset>> getAssetsByStatus(@PathVariable AssetStatus status) {
        List<Asset> assets = assetService.getAll().stream()
                .filter(a -> a.getStatus() == status)
                .toList();
        return ResponseEntity.ok(assets);
    }

    /**
     * Get assets by branch
     * GET /api/assets/branch/{branch}
     */
    @GetMapping("/branch/{branch}")
    public ResponseEntity<List<Asset>> getAssetsByBranch(@PathVariable String branch) {
        List<Asset> assets = assetService.getAll().stream()
                .filter(a -> branch.equals(a.getBranch()))
                .toList();
        return ResponseEntity.ok(assets);
    }

    /**
     * Get assets by manufacturer
     * GET /api/assets/manufacturer/{manufacturer}
     */
    @GetMapping("/manufacturer/{manufacturer}")
    public ResponseEntity<List<Asset>> getAssetsByManufacturer(@PathVariable String manufacturer) {
        List<Asset> assets = assetService.getAll().stream()
                .filter(a -> manufacturer.equals(a.getManufacturer()))
                .toList();
        return ResponseEntity.ok(assets);
    }

    // ============================================================
    // VENDOR MANAGEMENT
    // ============================================================

    /**
     * Get assets by vendor ID
     * GET /api/assets/vendor/{vendorId}
     */
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<Asset>> getAssetsByVendor(@PathVariable String vendorId) {
        List<Asset> assets = assetService.getAssetsByVendor(vendorId);
        return ResponseEntity.ok(assets);
    }

    /**
     * Get vendor lookup list
     * GET /api/assets/vendors/lookup
     */
    @GetMapping("/vendors/lookup")
    public ResponseEntity<List<Map<String, Object>>> getVendorLookup() {
        List<Map<String, Object>> vendors = assetService.getVendorLookup();
        return ResponseEntity.ok(vendors);
    }

    /**
     * Get vendor statistics
     * GET /api/assets/vendors/stats
     */
    @GetMapping("/vendors/stats")
    public ResponseEntity<List<Map<String, Object>>> getVendorStats() {
        List<Map<String, Object>> stats = assetService.getVendorStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Bulk assign vendor to multiple assets
     * POST /api/assets/bulk/assign-vendor
     */
    @PostMapping("/bulk/assign-vendor")
    public ResponseEntity<Map<String, Object>> assignVendorToAssets(
            @RequestBody Map<String, Object> request) {

        @SuppressWarnings("unchecked")
        List<String> assetIds = (List<String>) request.get("assetIds");
        String vendorId = (String) request.get("vendorId");
        String vendorName = (String) request.get("vendorName");
        String vendorEmail = (String) request.get("vendorEmail");
        String vendorPhone = (String) request.get("vendorPhone");

        int updated = assetService.bulkAssignVendor(assetIds, vendorId, vendorName, vendorEmail, vendorPhone);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Vendor assigned to assets successfully");
        response.put("updatedCount", updated);
        response.put("totalRequested", assetIds.size());
        response.put("assetIds", assetIds);

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // DLP ALERTS
    // ============================================================

    /**
     * Get DLP alert dashboard
     * GET /api/assets/alerts/dlp/dashboard
     */
    @GetMapping("/alerts/dlp/dashboard")
    public ResponseEntity<Map<String, Object>> getDlpAlertDashboard() {
        Map<String, Object> dashboard = assetService.getDlpAlertDashboard();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get assets with DLP expiring in specified days
     * GET /api/assets/alerts/dlp/expiring?days=30
     */
    @GetMapping("/alerts/dlp/expiring")
    public ResponseEntity<List<Asset>> getDlpExpiring(
            @RequestParam(defaultValue = "30") int days) {
        List<Asset> assets = assetService.getDlpExpiringWithin(days);
        return ResponseEntity.ok(assets);
    }

    /**
     * Get assets with DLP expiring in next 7 days (critical)
     * GET /api/assets/alerts/dlp/critical
     */
    @GetMapping("/alerts/dlp/critical")
    public ResponseEntity<List<Asset>> getCriticalDlpAlerts() {
        List<Asset> assets = assetService.getDlpExpiringIn7Days();
        return ResponseEntity.ok(assets);
    }

    /**
     * Get assets with DLP expiring in next 30 days
     * GET /api/assets/alerts/dlp/warning
     */
    @GetMapping("/alerts/dlp/warning")
    public ResponseEntity<List<Asset>> getWarningDlpAlerts() {
        List<Asset> assets = assetService.getDlpExpiringIn30Days();
        return ResponseEntity.ok(assets);
    }

    /**
     * Get assets with expired DLP
     * GET /api/assets/alerts/dlp/expired
     */
    @GetMapping("/alerts/dlp/expired")
    public ResponseEntity<List<Asset>> getExpiredDlp() {
        List<Asset> assets = assetService.getDlpExpired();
        return ResponseEntity.ok(assets);
    }

    /**
     * Get DLP alert counts by level
     * GET /api/assets/alerts/dlp/counts
     */
    @GetMapping("/alerts/dlp/counts")
    public ResponseEntity<Map<String, Long>> getDlpAlertCounts() {
        Map<String, Long> counts = assetService.getDlpAlertCounts();
        return ResponseEntity.ok(counts);
    }

    /**
     * Update DLP end date for an asset
     * PATCH /api/assets/{id}/dlp
     */
    @PatchMapping("/{id}/dlp")
    public ResponseEntity<Asset> updateDlpEndDate(
            @PathVariable String id,
            @RequestParam LocalDate dlpEndDate) {
        Asset updated = assetService.updateDlpEndDate(id, dlpEndDate);
        return ResponseEntity.ok(updated);
    }

    // ============================================================
    // WARRANTY ALERTS
    // ============================================================

    /**
     * Get assets with warranty expiring soon
     * GET /api/assets/alerts/warranty/expiring?days=30
     */
    @GetMapping("/alerts/warranty/expiring")
    public ResponseEntity<List<Asset>> getWarrantyExpiring(
            @RequestParam(defaultValue = "30") int days) {
        List<Asset> assets = assetService.getWarrantyExpiringWithin(days);
        return ResponseEntity.ok(assets);
    }

    /**
     * Get assets with expired warranty
     * GET /api/assets/alerts/warranty/expired
     */
    @GetMapping("/alerts/warranty/expired")
    public ResponseEntity<List<Asset>> getExpiredWarranty() {
        List<Asset> assets = assetService.getWarrantyExpired();
        return ResponseEntity.ok(assets);
    }

    /**
     * Update warranty dates for an asset
     * PATCH /api/assets/{id}/warranty
     */
    @PatchMapping("/{id}/warranty")
    public ResponseEntity<Asset> updateWarranty(
            @PathVariable String id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Asset updated = assetService.updateWarranty(id, startDate, endDate);
        return ResponseEntity.ok(updated);
    }

    // ============================================================
    // VENDOR CONTRACT ALERTS
    // ============================================================

    /**
     * Get assets with vendor contract expiring soon
     * GET /api/assets/alerts/contracts/expiring?days=30
     */
    @GetMapping("/alerts/contracts/expiring")
    public ResponseEntity<List<Asset>> getContractsExpiring(
            @RequestParam(defaultValue = "30") int days) {
        List<Asset> assets = assetService.getVendorContractExpiringWithin(days);
        return ResponseEntity.ok(assets);
    }

    /**
     * Get assets with expired vendor contracts
     * GET /api/assets/alerts/contracts/expired
     */
    @GetMapping("/alerts/contracts/expired")
    public ResponseEntity<List<Asset>> getExpiredContracts() {
        List<Asset> assets = assetService.getVendorContractExpired();
        return ResponseEntity.ok(assets);
    }

    /**
     * Update vendor contract dates for an asset
     * PATCH /api/assets/{id}/contract
     */
    @PatchMapping("/{id}/contract")
    public ResponseEntity<Asset> updateVendorContract(
            @PathVariable String id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Asset updated = assetService.updateVendorContract(id, startDate, endDate);
        return ResponseEntity.ok(updated);
    }

    // ============================================================
    // COMBINED ALERTS
    // ============================================================

    /**
     * Get all critical alerts (DLP, warranty, contracts)
     * GET /api/assets/alerts/all
     */
    @GetMapping("/alerts/all")
    public ResponseEntity<Map<String, Object>> getAllAlerts() {
        Map<String, Object> alerts = new HashMap<>();

        // DLP Alerts
        alerts.put("dlp", Map.of(
                "critical", assetService.getDlpExpiringIn7Days(),
                "warning", assetService.getDlpExpiringIn30Days(),
                "expired", assetService.getDlpExpired()
        ));

        // Warranty Alerts
        alerts.put("warranty", Map.of(
                "expiringSoon", assetService.getWarrantyExpiringWithin(30),
                "expired", assetService.getWarrantyExpired()
        ));

        // Contract Alerts
        alerts.put("contracts", Map.of(
                "expiringSoon", assetService.getVendorContractExpiringWithin(30),
                "expired", assetService.getVendorContractExpired()
        ));

        // Summary counts
        Map<String, Long> summary = new HashMap<>();
        summary.put("totalDlpAlerts", (long) assetService.getDlpExpiringIn30Days().size());
        summary.put("criticalDlpAlerts", (long) assetService.getDlpExpiringIn7Days().size());
        summary.put("expiredDlp", (long) assetService.getDlpExpired().size());
        summary.put("warrantyExpiring", (long) assetService.getWarrantyExpiringWithin(30).size());
        summary.put("warrantyExpired", (long) assetService.getWarrantyExpired().size());
        summary.put("contractsExpiring", (long) assetService.getVendorContractExpiringWithin(30).size());
        summary.put("contractsExpired", (long) assetService.getVendorContractExpired().size());

        alerts.put("summary", summary);

        return ResponseEntity.ok(alerts);
    }

    // ============================================================
    // LOOKUPS & DROPDOWNS
    // ============================================================

    /**
     * Get all dropdown/lookup data in one call
     * GET /api/assets/lookups
     */
    @GetMapping("/lookups")
    public ResponseEntity<Map<String, Object>> getLookups() {
        Map<String, Object> lookups = new HashMap<>();
        lookups.put("categories", assetService.getDistinctCategories());
        lookups.put("locations", assetService.getDistinctLocations());
        lookups.put("branches", assetService.getDistinctBranches());
        lookups.put("manufacturers", assetService.getDistinctManufacturers());
        lookups.put("vendors", assetService.getVendorLookup());
        lookups.put("statuses", AssetStatus.values());
        return ResponseEntity.ok(lookups);
    }

    /**
     * Get distinct categories
     * GET /api/assets/lookups/categories
     */
    @GetMapping("/lookups/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(assetService.getDistinctCategories());
    }

    /**
     * Get distinct locations
     * GET /api/assets/lookups/locations
     */
    @GetMapping("/lookups/locations")
    public ResponseEntity<List<String>> getLocations() {
        return ResponseEntity.ok(assetService.getDistinctLocations());
    }

    /**
     * Get distinct branches
     * GET /api/assets/lookups/branches
     */
    @GetMapping("/lookups/branches")
    public ResponseEntity<List<String>> getBranches() {
        return ResponseEntity.ok(assetService.getDistinctBranches());
    }

    /**
     * Get distinct manufacturers
     * GET /api/assets/lookups/manufacturers
     */
    @GetMapping("/lookups/manufacturers")
    public ResponseEntity<List<String>> getManufacturers() {
        return ResponseEntity.ok(assetService.getDistinctManufacturers());
    }

    // ============================================================
    // STATISTICS & ANALYTICS
    // ============================================================

    /**
     * Get comprehensive asset statistics
     * GET /api/assets/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAssetStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<Asset> allAssets = assetService.getAll();

        // Basic counts
        stats.put("totalAssets", allAssets.size());
        stats.put("availableAssets", allAssets.stream().filter(a -> a.getStatus() == AssetStatus.AVAILABLE).count());
        stats.put("inUseAssets", allAssets.stream().filter(a -> a.getStatus() == AssetStatus.IN_USE).count());
        stats.put("maintenanceAssets", allAssets.stream().filter(a -> a.getStatus() == AssetStatus.UNDER_MAINTENANCE).count());
        stats.put("retiredAssets", allAssets.stream().filter(a -> a.getStatus() == AssetStatus.RETIRED).count());

        // Vendor stats
        stats.put("assetsWithVendor", allAssets.stream().filter(a -> a.getVendorId() != null).count());
        stats.put("assetsWithoutVendor", allAssets.stream().filter(a -> a.getVendorId() == null).count());
        stats.put("vendorCount", assetService.getVendorLookup().size());

        // Alert stats
        stats.put("dlpAlerts", assetService.getDlpAlertCounts());
        stats.put("dlpExpiring30Days", assetService.getDlpExpiringIn30Days().size());
        stats.put("dlpExpiring7Days", assetService.getDlpExpiringIn7Days().size());
        stats.put("dlpExpired", assetService.getDlpExpired().size());

        // Warranty stats
        stats.put("warrantyExpiring", assetService.getWarrantyExpiringWithin(30).size());
        stats.put("warrantyExpired", assetService.getWarrantyExpired().size());

        // Contract stats
        stats.put("contractsExpiring", assetService.getVendorContractExpiringWithin(30).size());
        stats.put("contractsExpired", assetService.getVendorContractExpired().size());

        // Category breakdown
        Map<String, Long> categoryBreakdown = allAssets.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Asset::getAssetCategory,
                        java.util.stream.Collectors.counting()
                ));
        stats.put("categoryBreakdown", categoryBreakdown);

        // Location breakdown
        Map<String, Long> locationBreakdown = allAssets.stream()
                .filter(a -> a.getLocation() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        Asset::getLocation,
                        java.util.stream.Collectors.counting()
                ));
        stats.put("locationBreakdown", locationBreakdown);

        return ResponseEntity.ok(stats);
    }

    /**
     * Get asset counts by status
     * GET /api/assets/stats/by-status
     */
    @GetMapping("/stats/by-status")
    public ResponseEntity<Map<String, Long>> getStatsByStatus() {
        List<Asset> allAssets = assetService.getAll();

        Map<String, Long> statusCounts = new HashMap<>();
        for (AssetStatus status : AssetStatus.values()) {
            long count = allAssets.stream()
                    .filter(a -> a.getStatus() == status)
                    .count();
            statusCounts.put(status.name(), count);
        }

        return ResponseEntity.ok(statusCounts);
    }

    /**
     * Get asset counts by category
     * GET /api/assets/stats/by-category
     */
    @GetMapping("/stats/by-category")
    public ResponseEntity<Map<String, Long>> getStatsByCategory() {
        List<Asset> allAssets = assetService.getAll();

        Map<String, Long> categoryCount = allAssets.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Asset::getAssetCategory,
                        java.util.stream.Collectors.counting()
                ));

        return ResponseEntity.ok(categoryCount);
    }

    /**
     * Get asset counts by location
     * GET /api/assets/stats/by-location
     */
    @GetMapping("/stats/by-location")
    public ResponseEntity<Map<String, Long>> getStatsByLocation() {
        List<Asset> allAssets = assetService.getAll();

        Map<String, Long> locationCount = allAssets.stream()
                .filter(a -> a.getLocation() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        Asset::getLocation,
                        java.util.stream.Collectors.counting()
                ));

        return ResponseEntity.ok(locationCount);
    }

    // ============================================================
    // HEALTH CHECK
    // ============================================================

    /**
     * Health check endpoint
     * GET /api/assets/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", java.time.LocalDateTime.now());
        health.put("totalAssets", assetService.getAll().size());
        return ResponseEntity.ok(health);
    }
}