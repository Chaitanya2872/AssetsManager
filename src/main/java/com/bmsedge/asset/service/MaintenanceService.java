package com.bmsedge.asset.service;

import com.bmsedge.asset.dto.MaintenanceCreateRequest;
import com.bmsedge.asset.dto.BulkMaintenanceCreateRequest;
import com.bmsedge.asset.dto.MaintenanceUpdateRequest;
import com.bmsedge.asset.exception.MaintenanceNotFoundException;
import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.Maintenance;
import com.bmsedge.asset.model.MaintenanceStatus;
import com.bmsedge.asset.model.Vendor;
import com.bmsedge.asset.notification.EmailNotificationEvent;
import com.bmsedge.asset.repository.AssetRepository;
import com.bmsedge.asset.repository.MaintenanceRepository;
import com.bmsedge.asset.repository.VendorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class MaintenanceService {

    private static final Logger logger =
            LoggerFactory.getLogger(MaintenanceService.class);

    private final MaintenanceRepository repository;
        private final AssetRepository assetRepository;
        private final VendorRepository vendorRepository;
        private final AssetMaintenanceSyncService maintenanceSync;
        private final ApplicationEventPublisher eventPublisher;
        private final String frontendBaseUrl;


    public MaintenanceService(
                        MaintenanceRepository repository,
                        AssetRepository assetRepository,
                        VendorRepository vendorRepository,
                        AssetMaintenanceSyncService maintenanceSync,
                        ApplicationEventPublisher eventPublisher,
                        @Value("${app.frontend-base-url:http://localhost:3000}") String frontendBaseUrl
    ) {
        this.repository = repository;
                this.assetRepository = assetRepository;
                this.vendorRepository = vendorRepository;
                this.maintenanceSync = maintenanceSync;
                this.eventPublisher = eventPublisher;
                this.frontendBaseUrl = frontendBaseUrl;
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

        if (request.getEndDate() != null && request.getScheduledDate() != null
                && request.getEndDate().isBefore(request.getScheduledDate())) {
            throw new IllegalArgumentException("End date cannot be before scheduled date");
        }


        logger.info(
                "Creating maintenance for asset: {}",
                request.getAssetId()
        );


        boolean alreadyUnderMaintenance = maintenanceSync
                .findActive(request.getAssetId().trim())
                .stream()
                .findAny()
                .isPresent();

        if (alreadyUnderMaintenance) {
            throw new IllegalArgumentException(
                    "Device is already under maintenance"
            );
        }

        Maintenance maintenance = new Maintenance();


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

        maintenance.setEndDate(
                request.getEndDate()
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

        maintenanceSync.onMaintenanceChanged(saved.getAssetId());

                if (saved.getStatus() == MaintenanceStatus.SCHEDULED) {
                        publishMaintenanceNotification(saved, false);
                }


        logger.info(
                "Maintenance created successfully: {}",
                saved.getMaintenanceId()
        );


        return saved;
    }

    public List<Maintenance> createForAssets(BulkMaintenanceCreateRequest bulk) {
        if (bulk == null || bulk.getAssetIds() == null || bulk.getAssetIds().isEmpty()) {
            throw new IllegalArgumentException("At least one asset is required");
        }
        if (bulk.getMaintenanceType() == null || bulk.getMaintenanceType().isBlank()
                || bulk.getPriority() == null || bulk.getPriority().isBlank()
                || bulk.getTechnicianName() == null || bulk.getTechnicianName().isBlank()
                || bulk.getScheduledDate() == null || bulk.getScheduledDate().isBefore(LocalDate.now())
                || bulk.getEndDate() == null) {
            throw new IllegalArgumentException("Maintenance type, current or future schedule date, end date, priority, and technician are required");
        }
        if (bulk.getEndDate().isBefore(bulk.getScheduledDate())) {
            throw new IllegalArgumentException("End date cannot be before scheduled date");
        }
        List<String> ids = bulk.getAssetIds().stream().filter(Objects::nonNull)
                .map(String::trim).filter(id -> !id.isBlank()).distinct().toList();
        if (ids.size() != bulk.getAssetIds().size()) {
            throw new IllegalArgumentException("Asset IDs are invalid");
        }
        // Validate all targets and active schedules before creating anything.
        ids.forEach(id -> {
            if (!assetRepository.existsById(id)) {
                throw new IllegalArgumentException("Asset not found: " + id);
            }
            if (!maintenanceSync.findActive(id).isEmpty()) {
                throw new IllegalArgumentException("Asset is already under maintenance: " + id);
            }
        });
        return ids.stream().map(id -> {
            MaintenanceCreateRequest request = new MaintenanceCreateRequest();
            request.setAssetId(id);
            request.setMaintenanceType(bulk.getMaintenanceType());
            request.setPriority(bulk.getPriority());
            request.setScheduledDate(bulk.getScheduledDate());
            request.setEndDate(bulk.getEndDate());
            request.setTechnicianName(bulk.getTechnicianName());
            request.setDescription(bulk.getDescription());
            request.setNotes(bulk.getNotes());
            return create(request);
        }).toList();
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

        MaintenanceStatus previousStatus = maintenance.getStatus();
        LocalDate previousScheduledDate = maintenance.getScheduledDate();


        LocalDate updatedScheduledDate = request.getScheduledDate() != null
                ? request.getScheduledDate()
                : maintenance.getScheduledDate();
        LocalDate updatedEndDate = request.getEndDate() != null
                ? request.getEndDate()
                : maintenance.getEndDate();
        if (updatedScheduledDate != null && updatedEndDate != null
                && updatedEndDate.isBefore(updatedScheduledDate)) {
            throw new IllegalArgumentException("End date cannot be before scheduled date");
        }

        if (request.getScheduledDate() != null) {
            maintenance.setScheduledDate(request.getScheduledDate());
        }

        if (request.getEndDate() != null) {
            maintenance.setEndDate(
                    request.getEndDate()
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


        Maintenance updated = repository.save(maintenance);
        maintenanceSync.onMaintenanceChanged(updated.getAssetId());
        if (updated.getStatus() == MaintenanceStatus.COMPLETED
                && previousStatus != MaintenanceStatus.COMPLETED) {
            publishMaintenanceNotification(updated, true);
        } else if (updated.getStatus() == MaintenanceStatus.SCHEDULED
                && (previousStatus != MaintenanceStatus.SCHEDULED
                || !Objects.equals(previousScheduledDate, updated.getScheduledDate()))) {
            publishMaintenanceNotification(updated, false);
        }
        return updated;
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

        boolean shouldNotifyCompletion = maintenance.getStatus() != MaintenanceStatus.COMPLETED;

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


        Maintenance completed = repository.save(maintenance);
        maintenanceSync.onMaintenanceChanged(completed.getAssetId());
        if (shouldNotifyCompletion) {
            publishMaintenanceNotification(completed, true);
        }
        return completed;
    }

    private void publishMaintenanceNotification(Maintenance maintenance, boolean completed) {
        if (maintenance.getVendorId() == null || maintenance.getVendorId().isBlank()) {
            return;
        }

        Vendor vendor = vendorRepository.findById(maintenance.getVendorId()).orElse(null);
        if (vendor == null || vendor.getVendorEmail() == null || vendor.getVendorEmail().isBlank()) {
            logger.info("Skipped maintenance email: no vendor email for maintenance {}",
                    maintenance.getMaintenanceId());
            return;
        }

        Asset asset = assetRepository.findById(maintenance.getAssetId()).orElse(null);
        EmailNotificationEvent event = completed
                ? EmailNotificationEvent.maintenanceCompleted(
                        vendor.getVendorEmail(), vendor.getVendorName(), maintenance, asset, frontendBaseUrl)
                : EmailNotificationEvent.maintenanceScheduled(
                        vendor.getVendorEmail(), vendor.getVendorName(), maintenance, asset, frontendBaseUrl);
        eventPublisher.publishEvent(event);
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
        repository.flush();
        maintenanceSync.onMaintenanceChanged(maintenance.getAssetId());

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
