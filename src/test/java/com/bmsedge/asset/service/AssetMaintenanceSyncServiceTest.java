package com.bmsedge.asset.service;

import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.AssetStatus;
import com.bmsedge.asset.model.Maintenance;
import com.bmsedge.asset.model.MaintenanceStatus;
import com.bmsedge.asset.repository.AssetRepository;
import com.bmsedge.asset.repository.MaintenanceRepository;
import com.bmsedge.asset.repository.WorkOrderRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;

class AssetMaintenanceSyncServiceTest {

    @Test
    void enteringUnderMaintenanceCreatesLinkedRecordWithAssetDetails() {
        AssetRepository assets = mock(AssetRepository.class);
        MaintenanceRepository maintenance = mock(MaintenanceRepository.class);
        WorkOrderRepository workOrders = mock(WorkOrderRepository.class);
        when(maintenance.findByAssetIdAndStatusNotIn(eq("asset-1"), any()))
                .thenReturn(List.of());

        Asset asset = new Asset();
        asset.setAssetId("asset-1");
        asset.setAssetName("Dell EliteBook");
        asset.setLocation("HQ");
        asset.setAssignedTo("Alex");
        asset.setStatus(AssetStatus.UNDER_MAINTENANCE);

        new AssetMaintenanceSyncService(assets, maintenance, workOrders)
                .onAssetStatusChanged(asset, AssetStatus.IN_USE);

        var captor = org.mockito.ArgumentCaptor.forClass(Maintenance.class);
        verify(maintenance).save(captor.capture());
        Maintenance created = captor.getValue();
        assertEquals("asset-1", created.getAssetId());
        assertEquals("Alex", created.getTechnicianName());
        assertEquals(MaintenanceStatus.SCHEDULED, created.getStatus());
        assertTrue(created.getDescription().contains("Dell EliteBook (asset-1) at HQ"));
    }

    @Test
    void existingActiveMaintenanceIsNotDuplicated() {
        AssetRepository assets = mock(AssetRepository.class);
        MaintenanceRepository maintenance = mock(MaintenanceRepository.class);
        WorkOrderRepository workOrders = mock(WorkOrderRepository.class);
        Maintenance existing = new Maintenance();
        existing.setAssetId("asset-1");
        when(maintenance.findByAssetIdAndStatusNotIn(eq("asset-1"), any()))
                .thenReturn(List.of(existing));

        Asset asset = new Asset();
        asset.setAssetId("asset-1");
        asset.setStatus(AssetStatus.UNDER_MAINTENANCE);

        new AssetMaintenanceSyncService(assets, maintenance, workOrders)
                .onAssetStatusChanged(asset, AssetStatus.AVAILABLE);

        verify(maintenance, never()).save(any(Maintenance.class));
    }

    @Test
    void leavingUnderMaintenanceClosesActiveRecords() {
        AssetRepository assets = mock(AssetRepository.class);
        MaintenanceRepository maintenance = mock(MaintenanceRepository.class);
        WorkOrderRepository workOrders = mock(WorkOrderRepository.class);
        Maintenance active = new Maintenance();
        active.setAssetId("asset-1");
        when(maintenance.findByAssetIdAndStatusNotIn(eq("asset-1"), any()))
                .thenReturn(List.of(active));

        Asset asset = new Asset();
        asset.setAssetId("asset-1");
        asset.setStatus(AssetStatus.IN_USE);

        new AssetMaintenanceSyncService(assets, maintenance, workOrders)
                .onAssetStatusChanged(asset, AssetStatus.UNDER_MAINTENANCE);

        assertEquals(MaintenanceStatus.COMPLETED, active.getStatus());
        verify(maintenance, times(1)).saveAll(List.of(active));
    }

    @Test
    void deletingAssetRemovesWorkOrdersAndMaintenanceRecords() {
        AssetRepository assets = mock(AssetRepository.class);
        MaintenanceRepository maintenance = mock(MaintenanceRepository.class);
        WorkOrderRepository workOrders = mock(WorkOrderRepository.class);
        Maintenance record = new Maintenance();
        record.setMaintenanceId("maintenance-1");
        record.setAssetId("asset-1");
        when(maintenance.findByAssetIdOrderByScheduledDateDesc("asset-1"))
                .thenReturn(List.of(record));

        new AssetMaintenanceSyncService(assets, maintenance, workOrders)
                .removeForAsset("asset-1");

        var order = inOrder(workOrders, maintenance);
        order.verify(workOrders).deleteByMaintenanceIdIn(List.of("maintenance-1"));
        order.verify(workOrders).deleteByAssetId("asset-1");
        order.verify(maintenance).deleteAll(List.of(record));
    }
}
