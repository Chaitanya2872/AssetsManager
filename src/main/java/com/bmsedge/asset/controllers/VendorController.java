package com.bmsedge.asset.controllers;

import com.bmsedge.asset.dto.VendorCreateRequest;
import com.bmsedge.asset.dto.VendorUpdateRequest;
import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.Vendor;
import com.bmsedge.asset.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(origins = "*")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    // ===================== VENDOR CRUD =====================

    /**
     * Create new vendor
     * POST /api/vendors
     */
    @PostMapping
    public ResponseEntity<Vendor> create(@Valid @RequestBody VendorCreateRequest request) {
        Vendor vendor = vendorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vendor);
    }

    /**
     * Get vendor by ID
     * GET /api/vendors/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vendor> get(@PathVariable String id) {
        Vendor vendor = vendorService.get(id);
        return ResponseEntity.ok(vendor);
    }

    /**
     * Get vendor by email
     * GET /api/vendors/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Vendor> getByEmail(@PathVariable String email) {
        Vendor vendor = vendorService.getByEmail(email);
        return ResponseEntity.ok(vendor);
    }

    /**
     * Get all vendors
     * GET /api/vendors
     */
    @GetMapping
    public ResponseEntity<List<Vendor>> getAll() {
        List<Vendor> vendors = vendorService.getAll();
        return ResponseEntity.ok(vendors);
    }

    /**
     * Get vendors by status
     * GET /api/vendors/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Vendor>> getByStatus(@PathVariable String status) {
        List<Vendor> vendors = vendorService.getByStatus(status);
        return ResponseEntity.ok(vendors);
    }

    /**
     * Search vendors
     * GET /api/vendors/search?q={query}
     */
    @GetMapping("/search")
    public ResponseEntity<List<Vendor>> search(@RequestParam("q") String query) {
        List<Vendor> vendors = vendorService.search(query);
        return ResponseEntity.ok(vendors);
    }

    /**
     * Get vendors by asset ID
     * GET /api/vendors/asset/{assetId}
     */
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<Vendor>> getByAssetId(@PathVariable String assetId) {
        List<Vendor> vendors = vendorService.getByAssetId(assetId);
        return ResponseEntity.ok(vendors);
    }

    /**
     * Update vendor
     * PUT /api/vendors/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vendor> update(
            @PathVariable String id,
            @Valid @RequestBody VendorUpdateRequest request) {
        Vendor vendor = vendorService.update(id, request);
        return ResponseEntity.ok(vendor);
    }

    /**
     * Update vendor status
     * PATCH /api/vendors/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Vendor> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        Vendor vendor = vendorService.updateStatus(id, status);
        return ResponseEntity.ok(vendor);
    }

    /**
     * Update vendor rating
     * PATCH /api/vendors/{id}/rating
     */
    @PatchMapping("/{id}/rating")
    public ResponseEntity<Vendor> updateRating(
            @PathVariable String id,
            @RequestParam Double rating) {
        Vendor vendor = vendorService.updateRating(id, rating);
        return ResponseEntity.ok(vendor);
    }

    /**
     * Delete vendor
     * DELETE /api/vendors/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        vendorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== ASSET RELATIONSHIP =====================

    /**
     * Add asset to vendor
     * POST /api/vendors/{vendorId}/assets/{assetId}
     */
    @PostMapping("/{vendorId}/assets/{assetId}")
    public ResponseEntity<Vendor> addAsset(
            @PathVariable String vendorId,
            @PathVariable String assetId) {
        Vendor vendor = vendorService.addAsset(vendorId, assetId);
        return ResponseEntity.ok(vendor);
    }

    /**
     * Remove asset from vendor
     * DELETE /api/vendors/{vendorId}/assets/{assetId}
     */
    @DeleteMapping("/{vendorId}/assets/{assetId}")
    public ResponseEntity<Vendor> removeAsset(
            @PathVariable String vendorId,
            @PathVariable String assetId) {
        Vendor vendor = vendorService.removeAsset(vendorId, assetId);
        return ResponseEntity.ok(vendor);
    }

    /**
     * Bulk add assets to vendor
     * POST /api/vendors/{vendorId}/assets/bulk
     */
    @PostMapping("/{vendorId}/assets/bulk")
    public ResponseEntity<Map<String, Object>> bulkAddAssets(
            @PathVariable String vendorId,
            @RequestBody List<String> assetIds) {
        Vendor vendor = vendorService.bulkAddAssets(vendorId, assetIds);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("vendorId", vendor.getVendorId());
        response.put("totalAssets", vendor.getAssets().size());
        response.put("addedCount", assetIds.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Bulk remove assets from vendor
     * DELETE /api/vendors/{vendorId}/assets/bulk
     */
    @DeleteMapping("/{vendorId}/assets/bulk")
    public ResponseEntity<Map<String, Object>> bulkRemoveAssets(
            @PathVariable String vendorId,
            @RequestBody List<String> assetIds) {
        Vendor vendor = vendorService.bulkRemoveAssets(vendorId, assetIds);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("vendorId", vendor.getVendorId());
        response.put("totalAssets", vendor.getAssets().size());
        response.put("removedCount", assetIds.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Get all assets for a vendor
     * GET /api/vendors/{vendorId}/assets
     */
    @GetMapping("/{vendorId}/assets")
    public ResponseEntity<Set<Asset>> getVendorAssets(@PathVariable String vendorId) {
        Set<Asset> assets = vendorService.getVendorAssets(vendorId);
        return ResponseEntity.ok(assets);
    }

    /**
     * Get asset count for a vendor
     * GET /api/vendors/{vendorId}/assets/count
     */
    @GetMapping("/{vendorId}/assets/count")
    public ResponseEntity<Map<String, Integer>> getAssetCount(@PathVariable String vendorId) {
        int count = vendorService.getAssetCount(vendorId);
        Map<String, Integer> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // ===================== STATISTICS & COUNTS =====================

    /**
     * Get vendor statistics
     * GET /api/vendors/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalVendors", vendorService.count());
        stats.put("activeVendors", vendorService.getActiveVendorsCount());
        stats.put("inactiveVendors", vendorService.getInactiveVendorsCount());

        return ResponseEntity.ok(stats);
    }

    /**
     * Count vendors by status
     * GET /api/vendors/count/status/{status}
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Map<String, Long>> countByStatus(@PathVariable String status) {
        long count = vendorService.countByStatus(
                com.bmsedge.asset.model.VendorStatus.valueOf(status.toUpperCase())
        );
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // ===================== VALIDATION =====================

    /**
     * Check if email exists
     * GET /api/vendors/check-email?email={email}
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = vendorService.emailExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * Check if vendor can be deleted
     * GET /api/vendors/{vendorId}/can-delete
     */
    @GetMapping("/{vendorId}/can-delete")
    public ResponseEntity<Map<String, Boolean>> canDelete(@PathVariable String vendorId) {
        boolean canDelete = vendorService.canDelete(vendorId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("canDelete", canDelete);
        return ResponseEntity.ok(response);
    }

    // ===================== STATUS ACTIONS =====================

    /**
     * Activate vendor
     * POST /api/vendors/{vendorId}/activate
     */
    @PostMapping("/{vendorId}/activate")
    public ResponseEntity<Vendor> activate(@PathVariable String vendorId) {
        Vendor vendor = vendorService.activate(vendorId);
        return ResponseEntity.ok(vendor);
    }

    /**
     * Deactivate vendor
     * POST /api/vendors/{vendorId}/deactivate
     */
    @PostMapping("/{vendorId}/deactivate")
    public ResponseEntity<Vendor> deactivate(@PathVariable String vendorId) {
        Vendor vendor = vendorService.deactivate(vendorId);
        return ResponseEntity.ok(vendor);
    }

    /**
     * Suspend vendor
     * POST /api/vendors/{vendorId}/suspend
     */
    @PostMapping("/{vendorId}/suspend")
    public ResponseEntity<Vendor> suspend(@PathVariable String vendorId) {
        Vendor vendor = vendorService.suspend(vendorId);
        return ResponseEntity.ok(vendor);
    }

    /**
     * Blacklist vendor
     * POST /api/vendors/{vendorId}/blacklist
     */
    @PostMapping("/{vendorId}/blacklist")
    public ResponseEntity<Vendor> blacklist(@PathVariable String vendorId) {
        Vendor vendor = vendorService.blacklist(vendorId);
        return ResponseEntity.ok(vendor);
    }

    // ===================== ADVANCED QUERIES =====================

    /**
     * Get top rated vendors
     * GET /api/vendors/top-rated?limit={limit}
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<Vendor>> getTopRated(
            @RequestParam(defaultValue = "10") int limit) {
        List<Vendor> vendors = vendorService.getTopRatedVendors(limit);
        return ResponseEntity.ok(vendors);
    }

    /**
     * Get vendors with most assets
     * GET /api/vendors/most-assets?limit={limit}
     */
    @GetMapping("/most-assets")
    public ResponseEntity<List<Vendor>> getMostAssets(
            @RequestParam(defaultValue = "10") int limit) {
        List<Vendor> vendors = vendorService.getVendorsWithMostAssets(limit);
        return ResponseEntity.ok(vendors);
    }

    /**
     * Get vendors by specialization
     * GET /api/vendors/specialization/{specialization}
     */
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<Vendor>> getBySpecialization(
            @PathVariable String specialization) {
        List<Vendor> vendors = vendorService.getBySpecialization(specialization);
        return ResponseEntity.ok(vendors);
    }

    /**
     * Get vendors without assets
     * GET /api/vendors/without-assets
     */
    @GetMapping("/without-assets")
    public ResponseEntity<List<Vendor>> getWithoutAssets() {
        List<Vendor> vendors = vendorService.getVendorsWithoutAssets();
        return ResponseEntity.ok(vendors);
    }

    /**
     * Get vendor performance summary
     * GET /api/vendors/{vendorId}/performance
     */
    @GetMapping("/{vendorId}/performance")
    public ResponseEntity<VendorService.VendorPerformanceSummary> getPerformanceSummary(
            @PathVariable String vendorId) {
        VendorService.VendorPerformanceSummary summary =
                vendorService.getPerformanceSummary(vendorId);
        return ResponseEntity.ok(summary);
    }
}