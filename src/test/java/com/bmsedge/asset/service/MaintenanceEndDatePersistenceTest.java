package com.bmsedge.asset.service;

import com.bmsedge.asset.dto.MaintenanceCreateRequest;
import com.bmsedge.asset.dto.MaintenanceUpdateRequest;
import com.bmsedge.asset.model.Maintenance;
import com.bmsedge.asset.model.MaintenanceStatus;
import com.bmsedge.asset.repository.AssetRepository;
import com.bmsedge.asset.repository.MaintenanceRepository;
import com.bmsedge.asset.repository.VendorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MaintenanceEndDatePersistenceTest {
    private static final LocalDate START = LocalDate.of(2026, 11, 3);
    private static final LocalDate END = LocalDate.of(2026, 11, 7);

    @Test
    void createPersistsEndDateOnMaintenanceEntity() {
        MaintenanceRepository maintenanceRepository = mock(MaintenanceRepository.class);
        AssetRepository assetRepository = mock(AssetRepository.class);
        VendorRepository vendorRepository = mock(VendorRepository.class);
        when(maintenanceRepository.save(any(Maintenance.class))).thenAnswer(invocation -> {
            Maintenance maintenance = invocation.getArgument(0);
            maintenance.setMaintenanceId("maintenance-1");
            return maintenance;
        });

        MaintenanceService service = service(
                maintenanceRepository, assetRepository, vendorRepository);
        MaintenanceCreateRequest request = new MaintenanceCreateRequest();
        request.setAssetId("asset-1");
        request.setMaintenanceType("PREVENTIVE");
        request.setPriority("MEDIUM");
        request.setScheduledDate(START);
        request.setEndDate(END);

        Maintenance saved = service.create(request);

        assertEquals(START, saved.getScheduledDate());
        assertEquals(END, saved.getEndDate());
    }

    @Test
    void updatePersistsNewEndDateAndKeepsItWhenUpdateOmitsDate() {
        MaintenanceRepository maintenanceRepository = mock(MaintenanceRepository.class);
        AssetRepository assetRepository = mock(AssetRepository.class);
        VendorRepository vendorRepository = mock(VendorRepository.class);
        Maintenance existing = new Maintenance();
        existing.setMaintenanceId("maintenance-1");
        existing.setAssetId("asset-1");
        existing.setMaintenanceType("PREVENTIVE");
        existing.setPriority("MEDIUM");
        existing.setStatus(MaintenanceStatus.SCHEDULED);
        existing.setScheduledDate(START);
        existing.setEndDate(END);
        when(maintenanceRepository.findById("maintenance-1")).thenReturn(Optional.of(existing));
        when(maintenanceRepository.save(any(Maintenance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MaintenanceService service = service(
                maintenanceRepository, assetRepository, vendorRepository);
        MaintenanceUpdateRequest update = new MaintenanceUpdateRequest();
        update.setEndDate(LocalDate.of(2026, 11, 9));

        Maintenance updated = service.update("maintenance-1", update);
        assertEquals(LocalDate.of(2026, 11, 9), updated.getEndDate());

        MaintenanceUpdateRequest datesOmitted = new MaintenanceUpdateRequest();
        datesOmitted.setPriority("HIGH");
        updated = service.update("maintenance-1", datesOmitted);
        assertEquals(LocalDate.of(2026, 11, 9), updated.getEndDate());
    }

    private MaintenanceService service(
            MaintenanceRepository maintenanceRepository,
            AssetRepository assetRepository,
            VendorRepository vendorRepository) {
        return new MaintenanceService(
                maintenanceRepository,
                assetRepository,
                vendorRepository,
                new AssetMaintenanceSyncService(assetRepository, maintenanceRepository),
                mock(ApplicationEventPublisher.class),
                "https://assets.example.com");
    }
}
