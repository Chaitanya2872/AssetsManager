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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MaintenanceService {

    private static final Logger logger =
            LoggerFactory.getLogger(MaintenanceService.class);

    private final MaintenanceRepository repository;


    public MaintenanceService(
            MaintenanceRepository repository
    ) {
        this.repository = repository;
    }


    // ============================================================
    // CREATE
    // ============================================================

    public Maintenance create(
            MaintenanceCreateRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Maintenance request cannot be null"
            );
        }

        if (request.getAssetId() == null ||
                request.getAssetId().isBlank()) {

            throw new IllegalArgumentException(
                    "Asset ID is required"
            );
        }

        if (request.getMaintenanceType() == null ||
                request.getMaintenanceType().isBlank()) {

            throw new IllegalArgumentException(
                    "Maintenance type is required"
            );
        }

        if (request.getPriority() == null ||
                request.getPriority().isBlank()) {

            throw new IllegalArgumentException(
                    "Priority is required"
            );
        }


        logger.info(
                "Creating maintenance for asset: {}",
                request.getAssetId()
        );


        Maintenance maintenance =
                new Maintenance();


        maintenance.setAssetId(
                request.getAssetId().trim()
        );

        maintenance.setVendorId(
                request.getVendorId()
        );

        maintenance.setMaintenanceType(
                request.getMaintenanceType().trim().toUpperCase()
        );

        maintenance.setScheduledDate(
                request.getScheduledDate()
        );

        maintenance.setPriority(
                request.getPriority().trim().toUpperCase()
        );

        maintenance.setDescription(
                request.getDescription()
        );

        maintenance.setWorkPerformed(
                request.getWorkPerformed()
        );

        maintenance.setPartsReplaced(
                request.getPartsReplaced()
        );

        maintenance.setCost(
                request.getCost() == null
                        ? 0.0
                        : request.getCost()
        );

        maintenance.setTechnicianName(
                request.getTechnicianName()
        );

        maintenance.setNotes(
                request.getNotes()
        );

        maintenance.setCompletedDate(
                request.getCompletedDate()
        );

        maintenance.setNextMaintenanceDate(
                request.getNextMaintenanceDate()
        );

        maintenance.setDowntimeHours(
                request.getDowntimeHours()
        );


        if (request.getStatus() != null) {

            maintenance.setStatus(
                    request.getStatus()
            );

        } else {

            maintenance.setStatus(
                    MaintenanceStatus.SCHEDULED
            );
        }


        Maintenance saved =
                repository.save(maintenance);


        logger.info(
                "Maintenance created successfully: {}",
                saved.getMaintenanceId()
        );


        return saved;
    }


    // ============================================================
    // GET BY ID
    // ============================================================

    @Transactional(readOnly = true)
    public Maintenance get(String id) {

        if (id == null || id.isBlank()) {

            throw new MaintenanceNotFoundException(
                    String.valueOf(id)
            );
        }

        return repository.findById(id)
                .orElseThrow(() ->
                        new MaintenanceNotFoundException(id)
                );
    }


    // ============================================================
    // GET ALL
    // ============================================================

    @Transactional(readOnly = true)
    public List<Maintenance> getAll() {

        return repository.findAll();
    }


    // ============================================================
    // GET BY ASSET
    // ============================================================

    @Transactional(readOnly = true)
    public List<Maintenance> getByAsset(
            String assetId
    ) {

        if (assetId == null || assetId.isBlank()) {
            throw new IllegalArgumentException(
                    "Asset ID is required"
            );
        }

        return repository
                .findByAssetIdOrderByScheduledDateDesc(
                        assetId
                );
    }


    // ============================================================
    // GET BY VENDOR
    // ============================================================

    @Transactional(readOnly = true)
    public List<Maintenance> getByVendor(
            String vendorId
    ) {

        return repository.findByVendorId(vendorId);
    }


    // ============================================================
    // GET BY STATUS
    // ============================================================

    @Transactional(readOnly = true)
    public List<Maintenance> getByStatus(
            MaintenanceStatus status
    ) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Maintenance status is required"
            );
        }

        return repository.findByStatus(status);
    }


    // ============================================================
    // OVERDUE
    // ============================================================

    @Transactional(readOnly = true)
    public List<Maintenance> getOverdue() {

        return repository.findOverdue(
                LocalDate.now()
        );
    }


    // ============================================================
    // UPCOMING
    // ============================================================

    @Transactional(readOnly = true)
    public List<Maintenance> getUpcoming(
            int days
    ) {

        if (days < 0) {
            days = 0;
        }

        LocalDate start =
                LocalDate.now();

        LocalDate end =
                start.plusDays(days);

        return repository.findUpcoming(
                start,
                end
        );
    }


    // ============================================================
    // UPDATE
    // ============================================================

    public Maintenance update(
            String id,
            MaintenanceUpdateRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Maintenance update cannot be null"
            );
        }


        Maintenance maintenance =
                get(id);


        if (request.getScheduledDate() != null) {
            maintenance.setScheduledDate(
                    request.getScheduledDate()
            );
        }


        if (request.getCompletedDate() != null) {
            maintenance.setCompletedDate(
                    request.getCompletedDate()
            );
        }


        if (request.getStatus() != null) {
            maintenance.setStatus(
                    request.getStatus()
            );
        }


        if (request.getPriority() != null &&
                !request.getPriority().isBlank()) {

            maintenance.setPriority(
                    request.getPriority()
                            .trim()
                            .toUpperCase()
            );
        }


        if (request.getDescription() != null) {
            maintenance.setDescription(
                    request.getDescription()
            );
        }


        if (request.getWorkPerformed() != null) {
            maintenance.setWorkPerformed(
                    request.getWorkPerformed()
            );
        }


        if (request.getPartsReplaced() != null) {
            maintenance.setPartsReplaced(
                    request.getPartsReplaced()
            );
        }


        if (request.getCost() != null) {
            maintenance.setCost(
                    request.getCost()
            );
        }


        if (request.getTechnicianName() != null) {
            maintenance.setTechnicianName(
                    request.getTechnicianName()
            );
        }


        if (request.getNotes() != null) {
            maintenance.setNotes(
                    request.getNotes()
            );
        }


        if (request.getNextMaintenanceDate() != null) {
            maintenance.setNextMaintenanceDate(
                    request.getNextMaintenanceDate()
            );
        }


        if (request.getDowntimeHours() != null) {
            maintenance.setDowntimeHours(
                    request.getDowntimeHours()
            );
        }


        return repository.save(
                maintenance
        );
    }


    // ============================================================
    // COMPLETE
    // ============================================================

    public Maintenance complete(
            String id,
            String workPerformed,
            Double cost
    ) {

        Maintenance maintenance =
                get(id);


        maintenance.setStatus(
                MaintenanceStatus.COMPLETED
        );

        maintenance.setCompletedDate(
                LocalDate.now()
        );

        maintenance.setWorkPerformed(
                workPerformed
        );

        maintenance.setCost(
                cost == null ? 0.0 : cost
        );


        return repository.save(
                maintenance
        );
    }


    // ============================================================
    // DELETE
    // ============================================================

    public void delete(String id) {

        Maintenance maintenance =
                get(id);

        repository.delete(
                maintenance
        );

        logger.info(
                "Maintenance deleted successfully: {}",
                id
        );
    }


    // ============================================================
    // COUNT BY STATUS
    // ============================================================

    @Transactional(readOnly = true)
    public long countByStatus(
            MaintenanceStatus status
    ) {

        return repository.countByStatus(
                status
        );
    }


    // ============================================================
    // COUNT BY ASSET
    // ============================================================

    @Transactional(readOnly = true)
    public long countByAsset(
            String assetId
    ) {

        return repository.countByAssetId(
                assetId
        );
    }


    // ============================================================
    // STATISTICS
    // ============================================================

    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {

        Map<String, Object> statistics =
                new HashMap<>();


        long total =
                repository.count();


        long scheduled =
                countByStatus(
                        MaintenanceStatus.SCHEDULED
                );


        long pending =
                countByStatus(
                        MaintenanceStatus.PENDING
                );


        long inProgress =
                countByStatus(
                        MaintenanceStatus.IN_PROGRESS
                );


        long completed =
                countByStatus(
                        MaintenanceStatus.COMPLETED
                );


        long cancelled =
                countByStatus(
                        MaintenanceStatus.CANCELLED
                );


        long overdue =
                countByStatus(
                        MaintenanceStatus.OVERDUE
                );


        long onHold =
                countByStatus(
                        MaintenanceStatus.ON_HOLD
                );


        LocalDate today =
                LocalDate.now();

        LocalDate end =
                today.plusDays(30);


        long upcoming =
                repository.countUpcoming(
                        today,
                        end
                );


        Double totalCost =
                repository.sumCost();


        if (totalCost == null) {
            totalCost = 0.0;
        }


        List<Object[]> typeCounts =
                repository.countByMaintenanceType();


        Map<String, Long> categoryBreakdown =
                new HashMap<>();


        for (Object[] row : typeCounts) {

            if (row[0] == null) {
                continue;
            }

            String type =
                    String.valueOf(row[0]);

            Long count =
                    row[1] == null
                            ? 0L
                            : ((Number) row[1]).longValue();

            categoryBreakdown.put(
                    type,
                    count
            );
        }


        statistics.put(
                "total",
                total
        );

        statistics.put(
                "totalMaintenance",
                total
        );

        statistics.put(
                "scheduled",
                scheduled
        );

        statistics.put(
                "pending",
                pending
        );

        statistics.put(
                "inProgress",
                inProgress
        );

        statistics.put(
                "completed",
                completed
        );

        statistics.put(
                "cancelled",
                cancelled
        );

        statistics.put(
                "overdue",
                overdue
        );

        statistics.put(
                "onHold",
                onHold
        );

        statistics.put(
                "upcoming",
                upcoming
        );

        statistics.put(
                "totalCost",
                totalCost
        );

        statistics.put(
                "categoryBreakdown",
                categoryBreakdown
        );


        return statistics;
    }
}