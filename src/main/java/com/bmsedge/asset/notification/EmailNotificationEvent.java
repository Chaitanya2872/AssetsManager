package com.bmsedge.asset.notification;

import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.Maintenance;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public record EmailNotificationEvent(
        String recipientEmail,
        String templateName,
        Map<String, String> templateValues) {

    public static EmailNotificationEvent assetAssigned(
            String recipientEmail,
            String recipientName,
            Asset asset,
            String frontendBaseUrl) {
        String assetName = value(asset.getAssetName());
        EmailNotificationEvent event = create(
                recipientEmail,
                "asset-email.txt",
                recipientName,
                "Asset assigned: " + assetName,
                "An asset has been assigned to you",
                "The following asset has been assigned to you.",
                assetName,
                asset.getAssetId(),
                asset.getStatus() == null ? "" : asset.getStatus().getDisplayName(),
                frontendBaseUrl,
                asset.getAssetId(),
                "View asset");
        Map<String, String> values = new HashMap<>(event.templateValues());
        values.put("employeeName", value(recipientName).isBlank() ? recipientEmail : recipientName);
        values.put("serialNumber", value(asset.getSerialNumber()));
        LocalDateTime assignedAt = asset.getUpdatedAt() == null
                ? LocalDateTime.now()
                : asset.getUpdatedAt();
        values.put("assignedDate", assignedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return new EmailNotificationEvent(recipientEmail, event.templateName(), Map.copyOf(values));
    }

    public static EmailNotificationEvent maintenanceScheduled(
            String recipientEmail,
            String recipientName,
            Maintenance maintenance,
            Asset asset,
            String frontendBaseUrl) {
        String assetName = asset == null ? maintenance.getAssetId() : value(asset.getAssetName());
        String scheduledDate = maintenance.getScheduledDate() == null
                ? "not specified"
                : maintenance.getScheduledDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return create(
                recipientEmail,
                "maintenance-scheduled.txt",
                recipientName,
                "Maintenance scheduled: " + assetName,
                "Maintenance has been scheduled",
                maintenance.getMaintenanceType() + " maintenance is scheduled for " + scheduledDate + ".",
                assetName,
                maintenance.getAssetId(),
                maintenance.getStatus() == null ? "" : maintenance.getStatus().name(),
                frontendBaseUrl,
                maintenance.getAssetId(),
                "View asset");
    }

    public static EmailNotificationEvent maintenanceCompleted(
            String recipientEmail,
            String recipientName,
            Maintenance maintenance,
            Asset asset,
            String frontendBaseUrl) {
        String assetName = asset == null ? maintenance.getAssetId() : value(asset.getAssetName());
        return create(
                recipientEmail,
                "maintenance-completed.txt",
                recipientName,
                "Maintenance completed: " + assetName,
                "Maintenance has been completed",
                maintenance.getMaintenanceType() + " maintenance for this asset has been completed.",
                assetName,
                maintenance.getAssetId(),
                maintenance.getStatus() == null ? "" : maintenance.getStatus().name(),
                frontendBaseUrl,
                maintenance.getAssetId(),
                "View asset");
    }

    private static EmailNotificationEvent create(
            String recipientEmail,
            String templateName,
            String recipientName,
            String subject,
            String heading,
            String message,
            String assetName,
            String assetId,
            String status,
            String frontendBaseUrl,
            String actionId,
            String actionLabel) {
        String baseUrl = frontendBaseUrl == null ? "" : frontendBaseUrl.replaceAll("/+$", "");
        return new EmailNotificationEvent(recipientEmail, templateName, Map.ofEntries(
                Map.entry("subject", subject),
                Map.entry("companyName", "Asset Management"),
                Map.entry("heading", heading),
                Map.entry("recipientName", value(recipientName).isBlank() ? recipientEmail : recipientName),
                Map.entry("employeeName", value(recipientName).isBlank() ? recipientEmail : recipientName),
                Map.entry("message", message),
                Map.entry("assetName", value(assetName)),
                Map.entry("assetId", value(assetId)),
                Map.entry("serialNumber", ""),
                Map.entry("assignedDate", ""),
                Map.entry("status", value(status)),
                Map.entry("actionUrl", baseUrl + "/assets/" + value(actionId)),
                Map.entry("actionLabel", actionLabel)));
    }

    private static String value(String value) {
        return value == null ? "" : value;
    }
}