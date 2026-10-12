package com.bmsedge.asset.service;

import com.bmsedge.asset.dto.MaintenanceCreateRequest;
import com.bmsedge.asset.model.Maintenance;
import com.bmsedge.asset.model.MaintenanceStatus;
import com.bmsedge.asset.repository.AssetRepository;
import com.bmsedge.asset.repository.MaintenanceRepository;
import com.bmsedge.asset.repository.VendorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MaintenanceServiceDuplicateScheduleTest {

    @Test
    void rejectsSchedulingAnAssetThatAlreadyHasActiveMaintenance() {
        MaintenanceRepository maintenanceRepository =
                mock(MaintenanceRepository.class);
        AssetRepository assetRepository = mock(AssetRepository.class);
        VendorRepository vendorRepository = mock(VendorRepository.class);
        ApplicationEventPublisher publisher =
                mock(ApplicationEventPublisher.class);

        Maintenance activeMaintenance = new Maintenance();
        activeMaintenance.setAssetId("asset-1");
        activeMaintenance.setStatus(MaintenanceStatus.SCHEDULED);
        when(maintenanceRepository.findByAssetIdAndStatusNotIn(
                eq("asset-1"),
                eq(Set.of(MaintenanceStatus.COMPLETED, MaintenanceStatus.CANCELLED))))
                .thenReturn(List.of(activeMaintenance));

        MaintenanceService service = new MaintenanceService(
                maintenanceRepository,
                assetRepository,
                vendorRepository,
                new AssetMaintenanceSyncService(
                        assetRepository,
                        maintenanceRepository),
                publisher,
                "https://assets.example.com");

        MaintenanceCreateRequest request = new MaintenanceCreateRequest();
        request.setAssetId("asset-1");
        request.setMaintenanceType("PREVENTIVE");
        request.setPriority("MEDIUM");

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(request));

        assertEquals("Device is already under maintenance", error.getMessage());
        verify(maintenanceRepository, never()).save(any(Maintenance.class));
    }
}
