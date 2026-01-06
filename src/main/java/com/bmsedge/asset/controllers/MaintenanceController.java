package com.bmsedge.asset.controllers;

import com.bmsedge.asset.dto.MaintenanceCreateRequest;
import com.bmsedge.asset.dto.MaintenanceUpdateRequest;
import com.bmsedge.asset.model.Maintenance;
import com.bmsedge.asset.model.MaintenanceStatus;
import com.bmsedge.asset.service.MaintenanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "*")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping
    public ResponseEntity<Maintenance> create(@Valid @RequestBody MaintenanceCreateRequest request) {
        Maintenance maintenance = maintenanceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenance);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Maintenance> get(@PathVariable String id) {
        Maintenance maintenance = maintenanceService.get(id);
        return ResponseEntity.ok(maintenance);
    }

    @GetMapping
    public ResponseEntity<List<Maintenance>> getAll() {
        List<Maintenance> maintenance = maintenanceService.getAll();
        return ResponseEntity.ok(maintenance);
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<Maintenance>> getByAsset(@PathVariable String assetId) {
        List<Maintenance> maintenance = maintenanceService.getByAsset(assetId);
        return ResponseEntity.ok(maintenance);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<Maintenance>> getByVendor(@PathVariable String vendorId) {
        List<Maintenance> maintenance = maintenanceService.getByVendor(vendorId);
        return ResponseEntity.ok(maintenance);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Maintenance>> getByStatus(@PathVariable MaintenanceStatus status) {
        List<Maintenance> maintenance = maintenanceService.getByStatus(status);
        return ResponseEntity.ok(maintenance);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Maintenance>> getOverdue() {
        List<Maintenance> maintenance = maintenanceService.getOverdue();
        return ResponseEntity.ok(maintenance);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Maintenance>> getUpcoming(@RequestParam(defaultValue = "30") int days) {
        List<Maintenance> maintenance = maintenanceService.getUpcoming(days);
        return ResponseEntity.ok(maintenance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Maintenance> update(
            @PathVariable String id,
            @Valid @RequestBody MaintenanceUpdateRequest request) {
        Maintenance maintenance = maintenanceService.update(id, request);
        return ResponseEntity.ok(maintenance);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Maintenance> complete(
            @PathVariable String id,
            @RequestParam String workPerformed,
            @RequestParam Double cost) {
        Maintenance maintenance = maintenanceService.complete(id, workPerformed, cost);
        return ResponseEntity.ok(maintenance);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        maintenanceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable MaintenanceStatus status) {
        long count = maintenanceService.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/asset/{assetId}")
    public ResponseEntity<Long> countByAsset(@PathVariable String assetId) {
        long count = maintenanceService.countByAsset(assetId);
        return ResponseEntity.ok(count);
    }
}