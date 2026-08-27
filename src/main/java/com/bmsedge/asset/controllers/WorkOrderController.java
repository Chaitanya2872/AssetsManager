package com.bmsedge.asset.controllers;

import com.bmsedge.asset.dto.WorkOrderCreateRequest;
import com.bmsedge.asset.dto.WorkOrderUpdateRequest;
import com.bmsedge.asset.model.WorkOrder;
import com.bmsedge.asset.model.WorkOrderStatus;
import com.bmsedge.asset.service.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @PostMapping
    public ResponseEntity<WorkOrder> create(@Valid @RequestBody WorkOrderCreateRequest request) {
        WorkOrder workOrder = workOrderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(workOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrder> get(@PathVariable String id) {
        WorkOrder workOrder = workOrderService.get(id);
        return ResponseEntity.ok(workOrder);
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<WorkOrder> getByNumber(@PathVariable String number) {
        WorkOrder workOrder = workOrderService.getByNumber(number);
        return ResponseEntity.ok(workOrder);
    }

    @GetMapping
    public ResponseEntity<List<WorkOrder>> getAll() {
        List<WorkOrder> workOrders = workOrderService.getAll();
        return ResponseEntity.ok(workOrders);
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<WorkOrder>> getByAsset(@PathVariable String assetId) {
        List<WorkOrder> workOrders = workOrderService.getByAsset(assetId);
        return ResponseEntity.ok(workOrders);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<WorkOrder>> getByVendor(@PathVariable String vendorId) {
        List<WorkOrder> workOrders = workOrderService.getByVendor(vendorId);
        return ResponseEntity.ok(workOrders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<WorkOrder>> getByStatus(@PathVariable WorkOrderStatus status) {
        List<WorkOrder> workOrders = workOrderService.getByStatus(status);
        return ResponseEntity.ok(workOrders);
    }

    @GetMapping("/assigned/{assignedTo}")
    public ResponseEntity<List<WorkOrder>> getByAssignee(@PathVariable String assignedTo) {
        List<WorkOrder> workOrders = workOrderService.getByAssignee(assignedTo);
        return ResponseEntity.ok(workOrders);
    }

    @GetMapping("/active")
    public ResponseEntity<List<WorkOrder>> getActive() {
        List<WorkOrder> workOrders = workOrderService.getActive();
        return ResponseEntity.ok(workOrders);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<WorkOrder>> getOverdue() {
        List<WorkOrder> workOrders = workOrderService.getOverdue();
        return ResponseEntity.ok(workOrders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkOrder> update(
            @PathVariable String id,
            @Valid @RequestBody WorkOrderUpdateRequest request) {
        WorkOrder workOrder = workOrderService.update(id, request);
        return ResponseEntity.ok(workOrder);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkOrder> updateStatus(
            @PathVariable String id,
            @RequestParam WorkOrderStatus status) {
        WorkOrder workOrder = workOrderService.updateStatus(id, status);
        return ResponseEntity.ok(workOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        workOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable WorkOrderStatus status) {
        long count = workOrderService.countByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/asset/{assetId}")
    public ResponseEntity<Long> countByAsset(@PathVariable String assetId) {
        long count = workOrderService.countByAsset(assetId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/vendor/{vendorId}")
    public ResponseEntity<Long> countByVendor(@PathVariable String vendorId) {
        long count = workOrderService.countByVendor(vendorId);
        return ResponseEntity.ok(count);
    }
}