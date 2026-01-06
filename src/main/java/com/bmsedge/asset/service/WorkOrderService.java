package com.bmsedge.asset.service;

import com.bmsedge.asset.dto.WorkOrderCreateRequest;
import com.bmsedge.asset.dto.WorkOrderUpdateRequest;
import com.bmsedge.asset.dto.WorkOrderCreateRequest;
import com.bmsedge.asset.dto.WorkOrderUpdateRequest;
import com.bmsedge.asset.exception.WorkOrderNotFoundException;
import com.bmsedge.asset.model.WorkOrder;
import com.bmsedge.asset.model.WorkOrderStatus;
import com.bmsedge.asset.repository.WorkOrderRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class WorkOrderService {

    private static final Logger logger = LoggerFactory.getLogger(WorkOrderService.class);
    private final WorkOrderRepository repository;

    public WorkOrderService(WorkOrderRepository repository) {
        this.repository = repository;
    }

    public WorkOrder create(@Valid WorkOrderCreateRequest request) {
        logger.info("Creating work order for asset: {}", request.getAssetId());

        WorkOrder workOrder = new WorkOrder();
        workOrder.setAssetId(request.getAssetId());
        workOrder.setVendorId(request.getVendorId());
        workOrder.setMaintenanceId(request.getMaintenanceId());
        workOrder.setTitle(request.getTitle());
        workOrder.setDescription(request.getDescription());
        workOrder.setWorkType(request.getWorkType());
        workOrder.setPriority(request.getPriority());
        workOrder.setDueDate(request.getDueDate());
        workOrder.setEstimatedHours(request.getEstimatedHours());
        workOrder.setEstimatedCost(request.getEstimatedCost());
        workOrder.setAssignedTo(request.getAssignedTo());
        workOrder.setRequestedBy(request.getRequestedBy());
        workOrder.setNotes(request.getNotes());

        WorkOrder savedWorkOrder = repository.save(workOrder);
        logger.info("Work order created successfully with ID: {} (Number: {})",
                savedWorkOrder.getWorkOrderId(), savedWorkOrder.getWorkOrderNumber());

        return savedWorkOrder;
    }

    @Transactional(readOnly = true)
    public WorkOrder get(String id) {
        logger.debug("Fetching work order with ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Work order not found with ID: {}", id);
                    return new WorkOrderNotFoundException(id);
                });
    }

    @Transactional(readOnly = true)
    public WorkOrder getByNumber(String number) {
        logger.debug("Fetching work order with number: {}", number);
        return repository.findByWorkOrderNumber(number)
                .orElseThrow(() -> new WorkOrderNotFoundException("Number: " + number));
    }

    @Transactional(readOnly = true)
    public List<WorkOrder> getAll() {
        logger.debug("Fetching all work orders");
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<WorkOrder> getByAsset(String assetId) {
        logger.debug("Fetching work orders for asset: {}", assetId);
        return repository.findByAssetIdOrderByCreatedAtDesc(assetId);
    }

    @Transactional(readOnly = true)
    public List<WorkOrder> getByVendor(String vendorId) {
        logger.debug("Fetching work orders for vendor: {}", vendorId);
        return repository.findByVendorId(vendorId);
    }

    @Transactional(readOnly = true)
    public List<WorkOrder> getByStatus(WorkOrderStatus status) {
        logger.debug("Fetching work orders with status: {}", status);
        return repository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<WorkOrder> getByAssignee(String assignedTo) {
        logger.debug("Fetching work orders assigned to: {}", assignedTo);
        return repository.findByAssignedTo(assignedTo);
    }

    @Transactional(readOnly = true)
    public List<WorkOrder> getActive() {
        logger.debug("Fetching active work orders");
        return repository.findActive();
    }

    @Transactional(readOnly = true)
    public List<WorkOrder> getOverdue() {
        logger.debug("Fetching overdue work orders");
        return repository.findOverdue(LocalDate.now());
    }

    public WorkOrder update(String id, @Valid WorkOrderUpdateRequest request) {
        logger.info("Updating work order with ID: {}", id);

        WorkOrder workOrder = get(id);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            workOrder.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            workOrder.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            workOrder.setStatus(request.getStatus());
            // Auto-set completion date if status is COMPLETED
            if (request.getStatus() == WorkOrderStatus.COMPLETED && workOrder.getCompletionDate() == null) {
                workOrder.setCompletionDate(LocalDate.now());
            }
        }
        if (request.getPriority() != null) {
            workOrder.setPriority(request.getPriority());
        }
        if (request.getWorkType() != null) {
            workOrder.setWorkType(request.getWorkType());
        }
        if (request.getAssignedTo() != null) {
            workOrder.setAssignedTo(request.getAssignedTo());
        }
        if (request.getDueDate() != null) {
            workOrder.setDueDate(request.getDueDate());
        }
        if (request.getStartDate() != null) {
            workOrder.setStartDate(request.getStartDate());
        }
        if (request.getCompletionDate() != null) {
            workOrder.setCompletionDate(request.getCompletionDate());
        }
        if (request.getEstimatedHours() != null) {
            workOrder.setEstimatedHours(request.getEstimatedHours());
        }
        if (request.getActualHours() != null) {
            workOrder.setActualHours(request.getActualHours());
        }
        if (request.getEstimatedCost() != null) {
            workOrder.setEstimatedCost(request.getEstimatedCost());
        }
        if (request.getActualCost() != null) {
            workOrder.setActualCost(request.getActualCost());
        }
        if (request.getLaborCost() != null) {
            workOrder.setLaborCost(request.getLaborCost());
        }
        if (request.getPartsCost() != null) {
            workOrder.setPartsCost(request.getPartsCost());
        }
        if (request.getWorkPerformed() != null) {
            workOrder.setWorkPerformed(request.getWorkPerformed());
        }
        if (request.getPartsUsed() != null) {
            workOrder.setPartsUsed(request.getPartsUsed());
        }
        if (request.getNotes() != null) {
            workOrder.setNotes(request.getNotes());
        }
        if (request.getCompletionNotes() != null) {
            workOrder.setCompletionNotes(request.getCompletionNotes());
        }
        if (request.getRequiresFollowup() != null) {
            workOrder.setRequiresFollowup(request.getRequiresFollowup());
        }
        if (request.getFollowupDate() != null) {
            workOrder.setFollowupDate(request.getFollowupDate());
        }

        WorkOrder savedWorkOrder = repository.save(workOrder);
        logger.info("Work order updated successfully: {}", id);

        return savedWorkOrder;
    }

    public WorkOrder updateStatus(String id, WorkOrderStatus status) {
        logger.info("Updating status for work order {}: {}", id, status);

        WorkOrder workOrder = get(id);
        workOrder.setStatus(status);

        // Auto-set dates based on status
        if (status == WorkOrderStatus.IN_PROGRESS && workOrder.getStartDate() == null) {
            workOrder.setStartDate(LocalDate.now());
        } else if (status == WorkOrderStatus.COMPLETED && workOrder.getCompletionDate() == null) {
            workOrder.setCompletionDate(LocalDate.now());
        }

        return repository.save(workOrder);
    }

    public void delete(String id) {
        logger.info("Deleting work order with ID: {}", id);

        if (!repository.existsById(id)) {
            logger.error("Work order not found with ID: {}", id);
            throw new WorkOrderNotFoundException(id);
        }

        repository.deleteById(id);
        logger.info("Work order deleted successfully: {}", id);
    }

    @Transactional(readOnly = true)
    public long countByStatus(WorkOrderStatus status) {
        return repository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countByAsset(String assetId) {
        return repository.countByAssetId(assetId);
    }

    @Transactional(readOnly = true)
    public long countByVendor(String vendorId) {
        return repository.countByVendorId(vendorId);
    }
}