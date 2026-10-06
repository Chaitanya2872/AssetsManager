package com.bmsedge.asset.controllers;

import com.bmsedge.asset.dto.AssetUpdateRequest;
import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.AssetDocument;
import com.bmsedge.asset.model.AssetStatus;
import com.bmsedge.asset.service.AssetService;
import com.bmsedge.asset.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;
        private final DocumentService documentService;

        public AssetController(AssetService assetService, DocumentService documentService) {
        this.assetService = assetService;
                this.documentService = documentService;
    }

    // ============================================================
    // BASIC CRUD OPERATIONS
    // ============================================================

    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(assetService.get(id));
    }

    @PostMapping
    public ResponseEntity<Asset> createAsset(
            @Valid @RequestBody Asset asset
    ) {

        Asset created = assetService.create(
                asset.getAssetName(),
                asset.getAssetCategory(),
                asset.getLocation()
        );

        if (asset.getAssetType() != null) {
            created.setAssetType(asset.getAssetType());
        }

        if (asset.getManufacturer() != null) {
            created.setManufacturer(asset.getManufacturer());
        }

        if (asset.getBranch() != null) {
            created.setBranch(asset.getBranch());
        }

        if (asset.getSerialNumber() != null) {
            created.setSerialNumber(asset.getSerialNumber());
        }

        if (asset.getModelNumber() != null) {
            created.setModelNumber(asset.getModelNumber());
        }

        if (asset.getDescription() != null) {
            created.setDescription(asset.getDescription());
        }

        if (asset.getAssetImageUrl() != null) {
            created.setAssetImageUrl(asset.getAssetImageUrl());
        }

        if (asset.getDateOfInstallation() != null) {
            created.setDateOfInstallation(
                    asset.getDateOfInstallation()
            );
        }

        if (asset.getQuantity() != null) {
            created.setQuantity(asset.getQuantity());
        }

        if (asset.getAssignedTo() != null) {
            created.setAssignedTo(asset.getAssignedTo());
        }

        if (asset.getValue() != null) {
            created.setValue(asset.getValue());
        }

        if (asset.getVendorId() != null) {
            created.setVendorId(asset.getVendorId());
        }

        if (asset.getVendorName() != null) {
            created.setVendorName(asset.getVendorName());
        }

        if (asset.getVendorEmail() != null) {
            created.setVendorEmail(asset.getVendorEmail());
        }

        if (asset.getVendorPhone() != null) {
            created.setVendorPhone(asset.getVendorPhone());
        }

        if (asset.getDlpEndDate() != null) {
            created.setDlpEndDate(asset.getDlpEndDate());
        }

        if (asset.getWarrantyStartDate() != null) {
            created.setWarrantyStartDate(
                    asset.getWarrantyStartDate()
            );
        }

        if (asset.getWarrantyEndDate() != null) {
            created.setWarrantyEndDate(
                    asset.getWarrantyEndDate()
            );
        }

        if (asset.getVendorContractStart() != null) {
            created.setVendorContractStart(
                    asset.getVendorContractStart()
            );
        }

        if (asset.getVendorContractEnd() != null) {
            created.setVendorContractEnd(
                    asset.getVendorContractEnd()
            );
        }

        Asset saved = assetService.update(
                created.getAssetId(),
                created
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(
            @PathVariable String id,
            @Valid @RequestBody Asset asset
    ) {

        Asset updated = assetService.update(id, asset);

        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/image")
    public ResponseEntity<Asset> updateAssetImage(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        String imageUrl = request.get("assetImageUrl");
        if (imageUrl == null || imageUrl.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(assetService.updateImageUrl(id, imageUrl));
    }

    @RequestMapping(
            value = "/{id}/image",
            method = {RequestMethod.POST, RequestMethod.PUT},
            consumes = "multipart/form-data")
    public ResponseEntity<Asset> uploadAssetImage(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        assetService.get(id);
        AssetDocument image = documentService.uploadAssetImage(file, id);
        String imageUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath("/api/documents/{documentId}/preview")
                .replaceQuery(null)
                .buildAndExpand(image.getDocumentId())
                .toUriString();

        return ResponseEntity.ok(assetService.updateImageUrl(id, imageUrl));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Asset> partialUpdateAsset(
            @PathVariable String id,
            @RequestBody AssetUpdateRequest request
    ) {

        assetService.get(id);

        Asset updates = new Asset();

        if (request.getAssetName() != null) {
            updates.setAssetName(request.getAssetName());
        }

        if (request.getCategory() != null) {
            updates.setAssetCategory(request.getCategory());
        }

        if (request.getAssetType() != null) {
            updates.setAssetType(request.getAssetType());
        }

        if (request.getStatus() != null) {
            updates.setStatus(request.getStatus());
        }

        if (request.getLocation() != null) {
            updates.setLocation(request.getLocation());
        }

        if (request.getManufacturer() != null) {
            updates.setManufacturer(request.getManufacturer());
        }

        if (request.getBranch() != null) {
            updates.setBranch(request.getBranch());
        }

        if (request.getSerialNumber() != null) {
            updates.setSerialNumber(request.getSerialNumber());
        }

        if (request.getModelNumber() != null) {
            updates.setModelNumber(request.getModelNumber());
        }

        if (request.getDescription() != null) {
            updates.setDescription(request.getDescription());
        }

        if (request.getDateOfInstallation() != null) {
            updates.setDateOfInstallation(
                    request.getDateOfInstallation()
            );
        }

        if (request.getQuantity() != null) {
            updates.setQuantity(request.getQuantity());
        }

        if (request.getAssignedTo() != null) {
            updates.setAssignedTo(request.getAssignedTo());
        }

        if (request.getValue() != null) {
            updates.setValue(request.getValue());
        }

        if (request.getVendorId() != null) {
            updates.setVendorId(request.getVendorId());
        }

        if (request.getVendorName() != null) {
            updates.setVendorName(request.getVendorName());
        }

        if (request.getVendorEmail() != null) {
            updates.setVendorEmail(request.getVendorEmail());
        }

        if (request.getVendorPhone() != null) {
            updates.setVendorPhone(request.getVendorPhone());
        }

        if (request.getDlpEndDate() != null) {
            updates.setDlpEndDate(request.getDlpEndDate());
        }

        if (request.getWarrantyStartDate() != null) {
            updates.setWarrantyStartDate(
                    request.getWarrantyStartDate()
            );
        }

        if (request.getWarrantyEndDate() != null) {
            updates.setWarrantyEndDate(
                    request.getWarrantyEndDate()
            );
        }

        if (request.getVendorContractStart() != null) {
            updates.setVendorContractStart(
                    request.getVendorContractStart()
            );
        }

        if (request.getVendorContractEnd() != null) {
            updates.setVendorContractEnd(
                    request.getVendorContractEnd()
            );
        }

        Asset updated = assetService.update(id, updates);

        return ResponseEntity.ok(updated);
    }

    // ============================================================
    // DELETE
    // ============================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAsset(
            @PathVariable String id
    ) {

        assetService.delete(id);

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put(
                "message",
                "Asset deleted successfully"
        );
        response.put("assetId", id);

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // STATUS
    // ============================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<Asset> updateAssetStatus(
            @PathVariable String id,
            @RequestParam AssetStatus status
    ) {

        return ResponseEntity.ok(
                assetService.updateStatus(id, status)
        );
    }

    // ============================================================
    // ASSIGN TO EMPLOYEE
    // ============================================================

    @RequestMapping(
            value = "/{id}/assign",
            method = {
                    RequestMethod.POST,
                    RequestMethod.PUT,
                    RequestMethod.PATCH
            }
    )
    public ResponseEntity<Asset> assignAsset(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> request,
            @RequestParam(required = false) String assignedTo
    ) {

        String employee = assignedTo;

        if (request != null) {

            for (String key : new String[]{
                    "assignedTo",
                    "employeeName",
                    "name",
                    "userName",
                    "employeeEmail",
                    "email"
            }) {

                Object value = request.get(key);

                if (employee == null
                        && value instanceof String
                        && !((String) value).isBlank()) {

                    employee = (String) value;
                }
            }
        }

        return ResponseEntity.ok(
                assetService.assignToEmployee(id, employee)
        );
    }

    // ============================================================
    // SEARCH
    // ============================================================

    @GetMapping("/search")
    public ResponseEntity<List<Asset>> searchAssets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) AssetStatus status
    ) {

        return ResponseEntity.ok(
                assetService.searchAssets(
                        name,
                        category,
                        status
                )
        );
    }

    // ============================================================
    // CATEGORY
    // ============================================================

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Asset>> getAssetsByCategory(
            @PathVariable String category
    ) {

        return ResponseEntity.ok(
                assetService.getByCategory(category)
        );
    }

    // ============================================================
    // LOCATION
    // ============================================================

    @GetMapping("/location/{location}")
    public ResponseEntity<List<Asset>> getAssetsByLocation(
            @PathVariable String location
    ) {

        return ResponseEntity.ok(
                assetService.getAssetsByLocation(location)
        );
    }

    // ============================================================
    // STATUS FILTER
    // ============================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Asset>> getAssetsByStatus(
            @PathVariable AssetStatus status
    ) {

        return ResponseEntity.ok(
                assetService.getByStatus(status)
        );
    }

    // ============================================================
    // BRANCH
    // ============================================================

    @GetMapping("/branch/{branch}")
    public ResponseEntity<List<Asset>> getAssetsByBranch(
            @PathVariable String branch
    ) {

        return ResponseEntity.ok(
                assetService.getByBranch(branch)
        );
    }

    // ============================================================
    // MANUFACTURER
    // ============================================================

    @GetMapping("/manufacturer/{manufacturer}")
    public ResponseEntity<List<Asset>> getAssetsByManufacturer(
            @PathVariable String manufacturer
    ) {

        return ResponseEntity.ok(
                assetService.getByManufacturer(manufacturer)
        );
    }

    // ============================================================
    // VENDOR
    // ============================================================

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<Asset>> getAssetsByVendor(
            @PathVariable String vendorId
    ) {

        return ResponseEntity.ok(
                assetService.getAssetsByVendor(vendorId)
        );
    }

    @GetMapping("/vendors/lookup")
    public ResponseEntity<List<Map<String, Object>>> getVendorLookup() {

        return ResponseEntity.ok(
                assetService.getVendorLookup()
        );
    }

    @GetMapping("/vendors/stats")
    public ResponseEntity<List<Map<String, Object>>> getVendorStats() {

        return ResponseEntity.ok(
                assetService.getVendorStats()
        );
    }

    // ============================================================
    // BULK VENDOR ASSIGNMENT
    // ============================================================

    @PostMapping("/bulk/assign-vendor")
    public ResponseEntity<Map<String, Object>> assignVendorToAssets(
            @RequestBody Map<String, Object> request
    ) {

        Object assetIdsObject = request.get("assetIds");

        if (!(assetIdsObject instanceof List<?>)) {

            Map<String, Object> response = new HashMap<>();

            response.put("success", false);
            response.put(
                    "message",
                    "assetIds must be a list"
            );

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        @SuppressWarnings("unchecked")
        List<String> assetIds =
                (List<String>) assetIdsObject;

        String vendorId =
                (String) request.get("vendorId");

        String vendorName =
                (String) request.get("vendorName");

        String vendorEmail =
                (String) request.get("vendorEmail");

        String vendorPhone =
                (String) request.get("vendorPhone");

        int updated =
                assetService.bulkAssignVendor(
                        assetIds,
                        vendorId,
                        vendorName,
                        vendorEmail,
                        vendorPhone
                );

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put(
                "message",
                "Vendor assigned to assets successfully"
        );
        response.put("updatedCount", updated);
        response.put(
                "totalRequested",
                assetIds.size()
        );
        response.put("assetIds", assetIds);

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // DLP
    // ============================================================

    @GetMapping("/alerts/dlp/dashboard")
    public ResponseEntity<Map<String, Object>>
    getDlpAlertDashboard() {

        return ResponseEntity.ok(
                assetService.getDlpAlertDashboard()
        );
    }

    @GetMapping("/alerts/dlp/expiring")
    public ResponseEntity<List<Asset>> getDlpExpiring(
            @RequestParam(defaultValue = "30") int days
    ) {

        return ResponseEntity.ok(
                assetService.getDlpExpiringWithin(days)
        );
    }

    @GetMapping("/alerts/dlp/critical")
    public ResponseEntity<List<Asset>> getCriticalDlpAlerts() {

        return ResponseEntity.ok(
                assetService.getDlpExpiringIn7Days()
        );
    }

    @GetMapping("/alerts/dlp/warning")
    public ResponseEntity<List<Asset>> getWarningDlpAlerts() {

        return ResponseEntity.ok(
                assetService.getDlpExpiringIn30Days()
        );
    }

    @GetMapping("/alerts/dlp/expired")
    public ResponseEntity<List<Asset>> getExpiredDlp() {

        return ResponseEntity.ok(
                assetService.getDlpExpired()
        );
    }

    @GetMapping("/alerts/dlp/counts")
    public ResponseEntity<Map<String, Long>> getDlpAlertCounts() {

        return ResponseEntity.ok(
                assetService.getDlpAlertCounts()
        );
    }

    @PatchMapping("/{id}/dlp")
    public ResponseEntity<Asset> updateDlpEndDate(
            @PathVariable String id,
            @RequestParam LocalDate dlpEndDate
    ) {

        return ResponseEntity.ok(
                assetService.updateDlpEndDate(
                        id,
                        dlpEndDate
                )
        );
    }

    // ============================================================
    // WARRANTY
    // ============================================================

    @GetMapping("/alerts/warranty/expiring")
    public ResponseEntity<List<Asset>> getWarrantyExpiring(
            @RequestParam(defaultValue = "30") int days
    ) {

        return ResponseEntity.ok(
                assetService.getWarrantyExpiringWithin(days)
        );
    }

    @GetMapping("/alerts/warranty/expired")
    public ResponseEntity<List<Asset>> getExpiredWarranty() {

        return ResponseEntity.ok(
                assetService.getWarrantyExpired()
        );
    }

    @PatchMapping("/{id}/warranty")
    public ResponseEntity<Asset> updateWarranty(
            @PathVariable String id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {

        return ResponseEntity.ok(
                assetService.updateWarranty(
                        id,
                        startDate,
                        endDate
                )
        );
    }

    // ============================================================
    // VENDOR CONTRACT
    // ============================================================

    @GetMapping("/alerts/contracts/expiring")
    public ResponseEntity<List<Asset>> getContractsExpiring(
            @RequestParam(defaultValue = "30") int days
    ) {

        return ResponseEntity.ok(
                assetService.getVendorContractExpiringWithin(days)
        );
    }

    @GetMapping("/alerts/contracts/expired")
    public ResponseEntity<List<Asset>> getExpiredContracts() {

        return ResponseEntity.ok(
                assetService.getVendorContractExpired()
        );
    }

    @PatchMapping("/{id}/contract")
    public ResponseEntity<Asset> updateVendorContract(
            @PathVariable String id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {

        return ResponseEntity.ok(
                assetService.updateVendorContract(
                        id,
                        startDate,
                        endDate
                )
        );
    }

    // ============================================================
    // ALL ALERTS
    // ============================================================

    @GetMapping("/alerts/all")
    public ResponseEntity<Map<String, Object>> getAllAlerts() {

        Map<String, Object> alerts = new HashMap<>();

        alerts.put(
                "dlp",
                Map.of(
                        "critical",
                        assetService.getDlpExpiringIn7Days(),
                        "warning",
                        assetService.getDlpExpiringIn30Days(),
                        "expired",
                        assetService.getDlpExpired()
                )
        );

        alerts.put(
                "warranty",
                Map.of(
                        "expiringSoon",
                        assetService.getWarrantyExpiringWithin(30),
                        "expired",
                        assetService.getWarrantyExpired()
                )
        );

        alerts.put(
                "contracts",
                Map.of(
                        "expiringSoon",
                        assetService.getVendorContractExpiringWithin(30),
                        "expired",
                        assetService.getVendorContractExpired()
                )
        );

        Map<String, Long> summary = new HashMap<>();

        summary.put(
                "totalDlpAlerts",
                (long) assetService
                        .getDlpExpiringIn30Days()
                        .size()
        );

        summary.put(
                "criticalDlpAlerts",
                (long) assetService
                        .getDlpExpiringIn7Days()
                        .size()
        );

        summary.put(
                "expiredDlp",
                (long) assetService
                        .getDlpExpired()
                        .size()
        );

        summary.put(
                "warrantyExpiring",
                (long) assetService
                        .getWarrantyExpiringWithin(30)
                        .size()
        );

        summary.put(
                "warrantyExpired",
                (long) assetService
                        .getWarrantyExpired()
                        .size()
        );

        summary.put(
                "contractsExpiring",
                (long) assetService
                        .getVendorContractExpiringWithin(30)
                        .size()
        );

        summary.put(
                "contractsExpired",
                (long) assetService
                        .getVendorContractExpired()
                        .size()
        );

        alerts.put("summary", summary);

        return ResponseEntity.ok(alerts);
    }

    // ============================================================
    // LOOKUPS
    // ============================================================

    @GetMapping("/lookups")
    public ResponseEntity<Map<String, Object>> getLookups() {

        Map<String, Object> lookups = new HashMap<>();

        lookups.put(
                "categories",
                assetService.getDistinctCategories()
        );

        lookups.put(
                "locations",
                assetService.getDistinctLocations()
        );

        lookups.put(
                "branches",
                assetService.getDistinctBranches()
        );

        lookups.put(
                "manufacturers",
                assetService.getDistinctManufacturers()
        );

        lookups.put(
                "vendors",
                assetService.getVendorLookup()
        );

        lookups.put(
                "statuses",
                AssetStatus.values()
        );

        return ResponseEntity.ok(lookups);
    }

    @GetMapping("/lookups/categories")
    public ResponseEntity<List<String>> getCategories() {

        return ResponseEntity.ok(
                assetService.getDistinctCategories()
        );
    }

    @GetMapping("/lookups/locations")
    public ResponseEntity<List<String>> getLocations() {

        return ResponseEntity.ok(
                assetService.getDistinctLocations()
        );
    }

    @GetMapping("/lookups/branches")
    public ResponseEntity<List<String>> getBranches() {

        return ResponseEntity.ok(
                assetService.getDistinctBranches()
        );
    }

    @GetMapping("/lookups/manufacturers")
    public ResponseEntity<List<String>> getManufacturers() {

        return ResponseEntity.ok(
                assetService.getDistinctManufacturers()
        );
    }

    // ============================================================
    // STATISTICS
    // ============================================================

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAssetStatistics() {

        List<Asset> allAssets = assetService.getAll();

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalAssets", allAssets.size());

        stats.put(
                "availableAssets",
                allAssets.stream()
                        .filter(a ->
                                a.getStatus() == AssetStatus.AVAILABLE)
                        .count()
        );

        stats.put(
                "inUseAssets",
                allAssets.stream()
                        .filter(a ->
                                a.getStatus() == AssetStatus.IN_USE)
                        .count()
        );

        stats.put(
                "maintenanceAssets",
                allAssets.stream()
                        .filter(a ->
                                a.getStatus() ==
                                        AssetStatus.UNDER_MAINTENANCE)
                        .count()
        );

        stats.put(
                "retiredAssets",
                allAssets.stream()
                        .filter(a ->
                                a.getStatus() == AssetStatus.RETIRED)
                        .count()
        );

        stats.put(
                "assetsWithVendor",
                allAssets.stream()
                        .filter(a -> a.getVendorId() != null)
                        .count()
        );

        stats.put(
                "assetsWithoutVendor",
                allAssets.stream()
                        .filter(a -> a.getVendorId() == null)
                        .count()
        );

        stats.put(
                "vendorCount",
                assetService.getVendorLookup().size()
        );

        stats.put(
                "dlpAlerts",
                assetService.getDlpAlertCounts()
        );

        stats.put(
                "dlpExpiring30Days",
                assetService
                        .getDlpExpiringIn30Days()
                        .size()
        );

        stats.put(
                "dlpExpiring7Days",
                assetService
                        .getDlpExpiringIn7Days()
                        .size()
        );

        stats.put(
                "dlpExpired",
                assetService
                        .getDlpExpired()
                        .size()
        );

        stats.put(
                "warrantyExpiring",
                assetService
                        .getWarrantyExpiringWithin(30)
                        .size()
        );

        stats.put(
                "warrantyExpired",
                assetService
                        .getWarrantyExpired()
                        .size()
        );

        stats.put(
                "contractsExpiring",
                assetService
                        .getVendorContractExpiringWithin(30)
                        .size()
        );

        stats.put(
                "contractsExpired",
                assetService
                        .getVendorContractExpired()
                        .size()
        );

        Map<String, Long> categoryBreakdown =
                allAssets.stream()
                        .filter(a ->
                                a.getAssetCategory() != null)
                        .collect(
                                Collectors.groupingBy(
                                        Asset::getAssetCategory,
                                        Collectors.counting()
                                )
                        );

        stats.put(
                "categoryBreakdown",
                categoryBreakdown
        );

        Map<String, Long> locationBreakdown =
                allAssets.stream()
                        .filter(a ->
                                a.getLocation() != null)
                        .collect(
                                Collectors.groupingBy(
                                        Asset::getLocation,
                                        Collectors.counting()
                                )
                        );

        stats.put(
                "locationBreakdown",
                locationBreakdown
        );

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/by-status")
    public ResponseEntity<Map<String, Long>> getStatsByStatus() {

        Map<String, Long> statusCounts = new HashMap<>();

        for (AssetStatus status : AssetStatus.values()) {

            statusCounts.put(
                    status.name(),
                    assetService.getAssetCountByStatus(status)
            );
        }

        return ResponseEntity.ok(statusCounts);
    }

    @GetMapping("/stats/by-category")
    public ResponseEntity<Map<String, Long>> getStatsByCategory() {

        Map<String, Long> categoryCount = new HashMap<>();

        for (String category :
                assetService.getDistinctCategories()) {

            categoryCount.put(
                    category,
                    assetService.getAssetCountByCategory(category)
            );
        }

        return ResponseEntity.ok(categoryCount);
    }

    @GetMapping("/stats/by-location")
    public ResponseEntity<Map<String, Long>> getStatsByLocation() {

        Map<String, Long> locationCount = new HashMap<>();

        for (String location :
                assetService.getDistinctLocations()) {

            locationCount.put(
                    location,
                    assetService.getAssetCountByLocation(location)
            );
        }

        return ResponseEntity.ok(locationCount);
    }

    // ============================================================
    // HEALTH CHECK
    // ============================================================

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {

        Map<String, Object> health = new HashMap<>();

        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put(
                "totalAssets",
                assetService.getTotalAssetCount()
        );

        return ResponseEntity.ok(health);
    }
}
