package com.bmsedge.asset.service;

import com.bmsedge.asset.dto.MaintenanceCreateRequest;
import com.bmsedge.asset.dto.MaintenanceUpdateRequest;
import com.bmsedge.asset.exception.MaintenanceNotFoundException;
import com.bmsedge.asset.model.Maintenance;
import com.bmsedge.asset.model.MaintenanceStatus;
import com.bmsedge.asset.repository.MaintenanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MaintenanceService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceService.class);
    private final MaintenanceRepository repository;

    public MaintenanceService(MaintenanceRepository repository) {
        this.repository = repository;
    }

    public Maintenance create(MaintenanceCreateRequest request) {
        logger.info("Creating maintenance for asset: {}", request.getAssetId());

        Maintenance maintenance = new Maintenance();
        maintenance.setAssetId(request.getAssetId());
        maintenance.setVendorId(request.getVendorId());
        maintenance.setMaintenanceType(request.getMaintenanceType());
        maintenance.setScheduledDate(request.getScheduledDate());
        maintenance.setPriority(request.getPriority());
        maintenance.setDescription(request.getDescription());
        maintenance.setTechnicianName(request.getTechnicianName());
        maintenance.setNotes(request.getNotes());

        Maintenance savedMaintenance = repository.save(maintenance);
        logger.info("Maintenance created successfully with ID: {}", savedMaintenance.getMaintenanceId());

        return savedMaintenance;
    }

    @Transactional(readOnly = true)
    public Maintenance get(String id) {
        logger.debug("Fetching maintenance with ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Maintenance not found with ID: {}", id);
                    return new MaintenanceNotFoundException(id);
                });
    }

    @Transactional(readOnly = true)
    public List<Maintenance> getAll() {
        logger.debug("Fetching all maintenance records");
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Maintenance> getByAsset(String assetId) {
        logger.debug("Fetching maintenance for asset: {}", assetId);
        return repository.findByAssetIdOrderByScheduledDateDesc(assetId);
    }

    @Transactional(readOnly = true)
    public List<Maintenance> getByVendor(String vendorId) {
        logger.debug("Fetching maintenance for vendor: {}", vendorId);
        return repository.findByVendorId(vendorId);
    }

    @Transactional(readOnly = true)
    public List<Maintenance> getByStatus(MaintenanceStatus status) {
        logger.debug("Fetching maintenance with status: {}", status);
        return repository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Maintenance> getOverdue() {
        logger.debug("Fetching overdue maintenance");
        return repository.findOverdue(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Maintenance> getUpcoming(int days) {
        logger.debug("Fetching upcoming maintenance for next {} days", days);
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(days);
        return repository.findUpcoming(start, end);
    }

    public Maintenance update(String id, MaintenanceUpdateRequest request) {
        logger.info("Updating maintenance with ID: {}", id);

        Maintenance maintenance = get(id);

        if (request.getScheduledDate() != null) {
            maintenance.setScheduledDate(request.getScheduledDate());
        }
        if (request.getCompletedDate() != null) {
            maintenance.setCompletedDate(request.getCompletedDate());
        }
        if (request.getStatus() != null) {
            maintenance.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            maintenance.setPriority(request.getPriority());
        }
        if (request.getDescription() != null) {
            maintenance.setDescription(request.getDescription());
        }
        if (request.getWorkPerformed() != null) {
            maintenance.setWorkPerformed(request.getWorkPerformed());
        }
        if (request.getPartsReplaced() != null) {
            maintenance.setPartsReplaced(request.getPartsReplaced());
        }
        if (request.getCost() != null) {
            maintenance.setCost(request.getCost());
        }
        if (request.getTechnicianName() != null) {
            maintenance.setTechnicianName(request.getTechnicianName());
        }
        if (request.getNotes() != null) {
            maintenance.setNotes(request.getNotes());
        }
        if (request.getNextMaintenanceDate() != null) {
            maintenance.setNextMaintenanceDate(request.getNextMaintenanceDate());
        }
        if (request.getDowntimeHours() != null) {
            maintenance.setDowntimeHours(request.getDowntimeHours());
        }

        Maintenance savedMaintenance = repository.save(maintenance);
        logger.info("Maintenance updated successfully: {}", id);

        return savedMaintenance;
    }

    public Maintenance complete(String id, String workPerformed, Double cost) {
        logger.info("Completing maintenance with ID: {}", id);

        Maintenance maintenance = get(id);
        maintenance.setStatus(MaintenanceStatus.COMPLETED);
        maintenance.setCompletedDate(LocalDate.now());
        maintenance.setWorkPerformed(workPerformed);
        maintenance.setCost(cost);

        Maintenance savedMaintenance = repository.save(maintenance);
        logger.info("Maintenance completed successfully: {}", id);

        return savedMaintenance;
    }

    public void delete(String id) {
        logger.info("Deleting maintenance with ID: {}", id);

        if (!repository.existsById(id)) {
            logger.error("Maintenance not found with ID: {}", id);
            throw new MaintenanceNotFoundException(id);
        }

        repository.deleteById(id);
        logger.info("Maintenance deleted successfully: {}", id);
    }

    @Transactional(readOnly = true)
    public long countByStatus(MaintenanceStatus status) {
        return repository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countByAsset(String assetId) {
        return repository.countByAssetId(assetId);
    }
}