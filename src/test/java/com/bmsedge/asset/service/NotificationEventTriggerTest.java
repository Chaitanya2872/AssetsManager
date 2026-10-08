package com.bmsedge.asset.service;

import com.bmsedge.asset.dto.MaintenanceCreateRequest;
import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.AssetStatus;
import com.bmsedge.asset.model.Maintenance;
import com.bmsedge.asset.model.Vendor;
import com.bmsedge.asset.notification.EmailNotificationEvent;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;

class NotificationEventTriggerTest {

    @Test
    void assetAssignmentPublishesNotificationToAssigneeEmail() {
        AssetRepository assetRepository = mock(AssetRepository.class);
        VendorRepository vendorRepository = mock(VendorRepository.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        Asset asset = new Asset();
        asset.setAssetId("asset-1");
        asset.setAssetName("Laptop");
        asset.setStatus(AssetStatus.AVAILABLE);
        when(assetRepository.findById("asset-1")).thenReturn(Optional.of(asset));
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));
        AssetService service = new AssetService(
                assetRepository, vendorRepository, publisher, "https://assets.example.com");

        service.assignToEmployee("asset-1", "Alex", "alex@example.com");

        ArgumentCaptor<EmailNotificationEvent> event =
                ArgumentCaptor.forClass(EmailNotificationEvent.class);
        verify(publisher).publishEvent(event.capture());
        assertEquals("alex@example.com", event.getValue().recipientEmail());
        assertEquals("Alex", event.getValue().templateValues().get("recipientName"));
    }

    @Test
    void maintenanceScheduleAndCompletionPublishVendorNotifications() {
        MaintenanceRepository maintenanceRepository = mock(MaintenanceRepository.class);
        AssetRepository assetRepository = mock(AssetRepository.class);
        VendorRepository vendorRepository = mock(VendorRepository.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        Vendor vendor = new Vendor();
        vendor.setVendorId("vendor-1");
        vendor.setVendorName("Service Partner");
        vendor.setVendorEmail("service@example.com");
        Asset asset = new Asset();
        asset.setAssetId("asset-1");
        asset.setAssetName("Generator");
        asset.setStatus(AssetStatus.AVAILABLE);
        when(vendorRepository.findById("vendor-1")).thenReturn(Optional.of(vendor));
        when(assetRepository.findById("asset-1")).thenReturn(Optional.of(asset));
        when(maintenanceRepository.save(any(Maintenance.class))).thenAnswer(invocation -> {
            Maintenance maintenance = invocation.getArgument(0);
            if (maintenance.getMaintenanceId() == null) {
                maintenance.setMaintenanceId("maintenance-1");
            }
            return maintenance;
        });

        MaintenanceService service = new MaintenanceService(
                maintenanceRepository,
                assetRepository,
                vendorRepository,
                publisher,
                "https://assets.example.com");
        MaintenanceCreateRequest request = new MaintenanceCreateRequest();
        request.setAssetId("asset-1");
        request.setVendorId("vendor-1");
        request.setMaintenanceType("PREVENTIVE");
        request.setPriority("MEDIUM");
        request.setScheduledDate(LocalDate.of(2026, 11, 1));

        Maintenance created = service.create(request);
        when(maintenanceRepository.findById("maintenance-1")).thenReturn(Optional.of(created));
        service.complete("maintenance-1", "Inspection completed", 25.0);
        service.complete("maintenance-1", "Inspection completed", 25.0);

        ArgumentCaptor<EmailNotificationEvent> events =
                ArgumentCaptor.forClass(EmailNotificationEvent.class);
        verify(publisher, times(2)).publishEvent(events.capture());
        assertEquals("Maintenance scheduled: Generator",
                events.getAllValues().get(0).templateValues().get("subject"));
        assertEquals("Maintenance completed: Generator",
                events.getAllValues().get(1).templateValues().get("subject"));
        assertEquals("service@example.com", events.getAllValues().get(0).recipientEmail());
    }
}