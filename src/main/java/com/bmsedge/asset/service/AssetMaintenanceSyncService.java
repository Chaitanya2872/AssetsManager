package com.bmsedge.asset.service;

import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.AssetStatus;
import com.bmsedge.asset.model.Maintenance;
import com.bmsedge.asset.model.MaintenanceStatus;
import com.bmsedge.asset.repository.AssetRepository;
import com.bmsedge.asset.repository.MaintenanceRepository;
import com.bmsedge.asset.repository.WorkOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Keeps Asset Master status and Preventive Maintenance records in step,
 * linked by the asset's unique ID.
 *
 * - Asset set to UNDER_MAINTENANCE -> an active maintenance record exists
 *   (created if missing, never duplicated).
 * - Active maintenance record exists -> asset is UNDER_MAINTENANCE.
 * - No active maintenance left -> asset goes back to IN_USE (if assigned)
 *   or AVAILABLE.
 *
 * "Active" means any status other than COMPLETED or CANCELLED.
 * This class writes through the repositories directly so it never
 * re-enters AssetService / MaintenanceService.
 */
@Service
@Transactional
public class AssetMaintenanceSyncService {

    private static final Logger logger =
            LoggerFactory.getLogger(AssetMaintenanceSyncService.class);

    private static final Set<MaintenanceStatus> CLOSED =
            Set.of(MaintenanceStatus.COMPLETED, MaintenanceStatus.CANCELLED);

    private final AssetRepository assetRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final WorkOrderRepository workOrderRepository;

    @Autowired
    public AssetMaintenanceSyncService(
            AssetRepository assetRepository,
            MaintenanceRepository maintenanceRepository,
            WorkOrderRepository workOrderRepository
    ) {
        this.assetRepository = assetRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.workOrderRepository = workOrderRepository;
    }

    /** Retained for existing unit-level callers which exercise status synchronization only. */
    public AssetMaintenanceSyncService(
            AssetRepository assetRepository,
            MaintenanceRepository maintenanceRepository
    ) {
        this(assetRepository, maintenanceRepository, null);
    }

    public List<Maintenance> findActive(String assetId) {
        return maintenanceRepository.findByAssetIdAndStatusNotIn(assetId, CLOSED);
    }

    public void removeForAsset(String assetId) {
        List<Maintenance> records = maintenanceRepository.findByAssetIdOrderByScheduledDateDesc(assetId);
        if (workOrderRepository == null) {
            maintenanceRepository.deleteAll(records);
            return;
        }
        if (records.isEmpty()) {
            workOrderRepository.deleteByAssetId(assetId);
            return;
        }

        List<String> maintenanceIds = records.stream()
                .map(Maintenance::getMaintenanceId)
                .filter(id -> id != null && !id.isBlank())
                .toList();
        if (!maintenanceIds.isEmpty()) {
            workOrderRepository.deleteByMaintenanceIdIn(maintenanceIds);
        }
        workOrderRepository.deleteByAssetId(assetId);
        maintenanceRepository.deleteAll(records);
    }

    /** Call after an asset's status was changed in Asset Master. */
    public void onAssetStatusChanged(Asset asset, AssetStatus previous) {
        if (asset == null || asset.getStatus() == previous) {
            return;
        }

        String assetId = asset.getAssetId();
        List<Maintenance> active = findActive(assetId);

        if (asset.getStatus() == AssetStatus.UNDER_MAINTENANCE) {
            if (active.isEmpty()) {
                Maintenance m = new Maintenance(
                        assetId, "PREVENTIVE_MAINTENANCE", LocalDate.now());
                m.setPriority("MEDIUM");
                m.setStatus(MaintenanceStatus.SCHEDULED);
                m.setCost(0.0);
                if (asset.getVendorId() != null && !asset.getVendorId().isBlank()) {
                    m.setVendorId(asset.getVendorId());
                }
                m.setDescription("Auto-created: " + asset.getAssetName()
                        + " (" + assetId + ")"
                        + (asset.getLocation() != null && !asset.getLocation().isBlank()
                                ? " at " + asset.getLocation() : "")
                        + " set to Under Maintenance");
                maintenanceRepository.save(m);
                logger.info("Auto-created maintenance for asset {}", assetId);
            }
            return;
        }

        if (previous == AssetStatus.UNDER_MAINTENANCE) {
            for (Maintenance m : active) {
                m.setStatus(MaintenanceStatus.COMPLETED);
                m.setCompletedDate(LocalDate.now());
                m.setNotes("Closed automatically: asset status changed to "
                        + asset.getStatus().getDisplayName());
            }
            maintenanceRepository.saveAll(active);
        }
    }

    /** Call after a maintenance record was created, changed or deleted. */
    public void onMaintenanceChanged(String assetId) {
        if (assetId == null || assetId.isBlank()) {
            return;
        }

        Asset asset = assetRepository.findById(assetId).orElse(null);
        if (asset == null || asset.getStatus() == AssetStatus.RETIRED) {
            return;
        }

        boolean hasActive = !findActive(assetId).isEmpty();

        if (hasActive && asset.getStatus() != AssetStatus.UNDER_MAINTENANCE) {
            asset.setStatus(AssetStatus.UNDER_MAINTENANCE);
            assetRepository.save(asset);
        } else if (!hasActive && asset.getStatus() == AssetStatus.UNDER_MAINTENANCE) {
            boolean assigned = asset.getAssignedTo() != null
                    && !asset.getAssignedTo().isBlank();
            asset.setStatus(assigned ? AssetStatus.IN_USE : AssetStatus.AVAILABLE);
            assetRepository.save(asset);
        }
    }
}
