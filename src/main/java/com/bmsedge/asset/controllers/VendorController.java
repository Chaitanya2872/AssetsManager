package com.bmsedge.asset.controllers;

import com.bmsedge.asset.dto.VendorCreateRequest;
import com.bmsedge.asset.dto.VendorUpdateRequest;
import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.Vendor;
import com.bmsedge.asset.model.VendorStatus;
import com.bmsedge.asset.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    // ============================================================
    // VENDOR CRUD
    // ============================================================

    /**
     * Create vendor
     * POST /api/vendors
     */
    @PostMapping
    public ResponseEntity<Vendor> create(
            @Valid @RequestBody VendorCreateRequest request
    ) {

        Vendor vendor =
                vendorService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(vendor);
    }

    /**
     * Get vendor by ID
     * GET /api/vendors/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vendor> get(
            @PathVariable String id
    ) {

        return ResponseEntity.ok(
                vendorService.get(id)
        );
    }

    /**
     * Get vendor by email
     * GET /api/vendors/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Vendor> getByEmail(
            @PathVariable String email
    ) {

        return ResponseEntity.ok(
                vendorService.getByEmail(email)
        );
    }

    /**
     * Get all vendors
     * GET /api/vendors
     */
    @GetMapping
    public ResponseEntity<List<Vendor>> getAll() {

        return ResponseEntity.ok(
                vendorService.getAll()
        );
    }

    /**
     * Get vendors by status
     * GET /api/vendors/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Vendor>> getByStatus(
            @PathVariable String status
    ) {

        return ResponseEntity.ok(
                vendorService.getByStatus(status)
        );
    }

    /**
     * Search vendors
     * GET /api/vendors/search?q=abc
     */
    @GetMapping("/search")
    public ResponseEntity<List<Vendor>> search(
            @RequestParam("q") String query
    ) {

        return ResponseEntity.ok(
                vendorService.search(query)
        );
    }

    /**
     * Get vendors by asset ID
     * GET /api/vendors/asset/{assetId}
     */
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<Vendor>> getByAssetId(
            @PathVariable String assetId
    ) {

        return ResponseEntity.ok(
                vendorService.getByAssetId(assetId)
        );
    }

    /**
     * Update vendor
     * PUT /api/vendors/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vendor> update(
            @PathVariable String id,
            @Valid @RequestBody VendorUpdateRequest request
    ) {

        Vendor vendor =
                vendorService.update(
                        id,
                        request
                );

        return ResponseEntity.ok(vendor);
    }

    /**
     * Update vendor status
     * PATCH /api/vendors/{id}/status?status=ACTIVE
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Vendor> updateStatus(
            @PathVariable String id,
            @RequestParam String status
    ) {

        Vendor vendor =
                vendorService.updateStatus(
                        id,
                        status
                );

        return ResponseEntity.ok(vendor);
    }

    /**
     * Update vendor rating
     * PATCH /api/vendors/{id}/rating?rating=4.5
     */
    @PatchMapping("/{id}/rating")
    public ResponseEntity<Vendor> updateRating(
            @PathVariable String id,
            @RequestParam Double rating
    ) {

        Vendor vendor =
                vendorService.updateRating(
                        id,
                        rating
                );

        return ResponseEntity.ok(vendor);
    }

    /**
     * Delete vendor
     * DELETE /api/vendors/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id
    ) {

        vendorService.delete(id);

        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // ASSET RELATIONSHIP
    // ============================================================

    /**
     * Add asset to vendor
     * POST /api/vendors/{vendorId}/assets/{assetId}
     */
    @PostMapping("/{vendorId}/assets/{assetId}")
    public ResponseEntity<Vendor> addAsset(
            @PathVariable String vendorId,
            @PathVariable String assetId
    ) {

        Vendor vendor =
                vendorService.addAsset(
                        vendorId,
                        assetId
                );

        return ResponseEntity.ok(vendor);
    }

    /**
     * Remove asset from vendor
     * DELETE /api/vendors/{vendorId}/assets/{assetId}
     */
    @DeleteMapping("/{vendorId}/assets/{assetId}")
    public ResponseEntity<Vendor> removeAsset(
            @PathVariable String vendorId,
            @PathVariable String assetId
    ) {

        Vendor vendor =
                vendorService.removeAsset(
                        vendorId,
                        assetId
                );

        return ResponseEntity.ok(vendor);
    }

    /**
     * Bulk add assets
     * POST /api/vendors/{vendorId}/assets/bulk
     */
    @PostMapping("/{vendorId}/assets/bulk")
    public ResponseEntity<Map<String, Object>> bulkAddAssets(
            @PathVariable String vendorId,
            @RequestBody List<String> assetIds
    ) {

        if (assetIds == null) {
            assetIds = List.of();
        }

        Vendor vendor =
                vendorService.bulkAddAssets(
                        vendorId,
                        assetIds
                );

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "success",
                true
        );

        response.put(
                "vendorId",
                vendor.getVendorId()
        );

        response.put(
                "totalAssets",
                vendor.getAssets() == null
                        ? 0
                        : vendor.getAssets().size()
        );

        response.put(
                "addedCount",
                assetIds.size()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Bulk remove assets
     * DELETE /api/vendors/{vendorId}/assets/bulk
     */
    @DeleteMapping("/{vendorId}/assets/bulk")
    public ResponseEntity<Map<String, Object>> bulkRemoveAssets(
            @PathVariable String vendorId,
            @RequestBody List<String> assetIds
    ) {

        if (assetIds == null) {
            assetIds = List.of();
        }

        Vendor vendor =
                vendorService.bulkRemoveAssets(
                        vendorId,
                        assetIds
                );

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "success",
                true
        );

        response.put(
                "vendorId",
                vendor.getVendorId()
        );

        response.put(
                "totalAssets",
                vendor.getAssets() == null
                        ? 0
                        : vendor.getAssets().size()
        );

        response.put(
                "removedCount",
                assetIds.size()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get all assets for vendor
     * GET /api/vendors/{vendorId}/assets
     */
    @GetMapping("/{vendorId}/assets")
    public ResponseEntity<Set<Asset>> getVendorAssets(
            @PathVariable String vendorId
    ) {

        return ResponseEntity.ok(
                vendorService.getVendorAssets(vendorId)
        );
    }

    /**
     * Get asset count
     * GET /api/vendors/{vendorId}/assets/count
     */
    @GetMapping("/{vendorId}/assets/count")
    public ResponseEntity<Map<String, Integer>> getAssetCount(
            @PathVariable String vendorId
    ) {

        int count =
                vendorService.getAssetCount(
                        vendorId
                );

        Map<String, Integer> response =
                new HashMap<>();

        response.put(
                "count",
                count
        );

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // STATISTICS
    // ============================================================

    /**
     * Vendor statistics
     * GET /api/vendors/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>>
    getStatistics() {

        Map<String, Object> stats =
                new HashMap<>();

        stats.put(
                "totalVendors",
                vendorService.count()
        );

        stats.put(
                "activeVendors",
                vendorService.getActiveVendorsCount()
        );

        stats.put(
                "inactiveVendors",
                vendorService.getInactiveVendorsCount()
        );

        return ResponseEntity.ok(stats);
    }

    /**
     * Count by status
     * GET /api/vendors/count/status/{status}
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Map<String, Long>>
    countByStatus(
            @PathVariable String status
    ) {

        VendorStatus vendorStatus;

        try {

            vendorStatus =
                    VendorStatus.valueOf(
                            status.toUpperCase()
                    );

        } catch (IllegalArgumentException e) {

            Map<String, Long> response =
                    new HashMap<>();

            response.put(
                    "count",
                    0L
            );

            return ResponseEntity.badRequest()
                    .body(response);
        }

        long count =
                vendorService.countByStatus(
                        vendorStatus
                );

        Map<String, Long> response =
                new HashMap<>();

        response.put(
                "count",
                count
        );

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // VALIDATION
    // ============================================================

    /**
     * Check vendor email
     * GET /api/vendors/check-email?email=test@example.com
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>>
    checkEmail(
            @RequestParam String email
    ) {

        boolean exists =
                vendorService.emailExists(email);

        Map<String, Boolean> response =
                new HashMap<>();

        response.put(
                "exists",
                exists
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Check if vendor can be deleted
     * GET /api/vendors/{vendorId}/can-delete
     */
    @GetMapping("/{vendorId}/can-delete")
    public ResponseEntity<Map<String, Boolean>>
    canDelete(
            @PathVariable String vendorId
    ) {

        boolean canDelete =
                vendorService.canDelete(
                        vendorId
                );

        Map<String, Boolean> response =
                new HashMap<>();

        response.put(
                "canDelete",
                canDelete
        );

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // STATUS ACTIONS
    // ============================================================

    /**
     * Activate vendor
     * POST /api/vendors/{vendorId}/activate
     */
    @PostMapping("/{vendorId}/activate")
    public ResponseEntity<Vendor> activate(
            @PathVariable String vendorId
    ) {

        return ResponseEntity.ok(
                vendorService.activate(vendorId)
        );
    }

    /**
     * Deactivate vendor
     * POST /api/vendors/{vendorId}/deactivate
     */
    @PostMapping("/{vendorId}/deactivate")
    public ResponseEntity<Vendor> deactivate(
            @PathVariable String vendorId
    ) {

        return ResponseEntity.ok(
                vendorService.deactivate(vendorId)
        );
    }

    /**
     * Suspend vendor
     * POST /api/vendors/{vendorId}/suspend
     */
    @PostMapping("/{vendorId}/suspend")
    public ResponseEntity<Vendor> suspend(
            @PathVariable String vendorId
    ) {

        return ResponseEntity.ok(
                vendorService.suspend(vendorId)
        );
    }

    /**
     * Blacklist vendor
     * POST /api/vendors/{vendorId}/blacklist
     */
    @PostMapping("/{vendorId}/blacklist")
    public ResponseEntity<Vendor> blacklist(
            @PathVariable String vendorId
    ) {

        return ResponseEntity.ok(
                vendorService.blacklist(vendorId)
        );
    }

    // ============================================================
    // ADVANCED QUERIES
    // ============================================================

    /**
     * Top rated vendors
     * GET /api/vendors/top-rated?limit=10
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<Vendor>> getTopRated(
            @RequestParam(defaultValue = "10") int limit
    ) {

        if (limit < 1) {
            limit = 10;
        }

        return ResponseEntity.ok(
                vendorService.getTopRatedVendors(
                        limit
                )
        );
    }

    /**
     * Vendors with most assets
     * GET /api/vendors/most-assets?limit=10
     */
    @GetMapping("/most-assets")
    public ResponseEntity<List<Vendor>> getMostAssets(
            @RequestParam(defaultValue = "10") int limit
    ) {

        if (limit < 1) {
            limit = 10;
        }

        return ResponseEntity.ok(
                vendorService.getVendorsWithMostAssets(
                        limit
                )
        );
    }

    /**
     * Vendors by specialization
     * GET /api/vendors/specialization/{specialization}
     */
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<Vendor>>
    getBySpecialization(
            @PathVariable String specialization
    ) {

        return ResponseEntity.ok(
                vendorService.getBySpecialization(
                        specialization
                )
        );
    }

    /**
     * Vendors without assets
     * GET /api/vendors/without-assets
     */
    @GetMapping("/without-assets")
    public ResponseEntity<List<Vendor>>
    getWithoutAssets() {

        return ResponseEntity.ok(
                vendorService.getVendorsWithoutAssets()
        );
    }

    /**
     * Vendor performance
     * GET /api/vendors/{vendorId}/performance
     */
    @GetMapping("/{vendorId}/performance")
    public ResponseEntity<VendorService.VendorPerformanceSummary>
    getPerformanceSummary(
            @PathVariable String vendorId
    ) {

        VendorService.VendorPerformanceSummary summary =
                vendorService.getPerformanceSummary(
                        vendorId
                );

        return ResponseEntity.ok(summary);
    }
}