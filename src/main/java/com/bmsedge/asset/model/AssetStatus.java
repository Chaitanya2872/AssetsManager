package com.bmsedge.asset.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum AssetStatus {

    AVAILABLE(
            "Available",
            "Asset is ready for use"
    ),

    IN_USE(
            "In Use",
            "Asset is currently in use"
    ),

    UNDER_MAINTENANCE(
            "Under Maintenance",
            "Asset is under maintenance or repair"
    ),

    RETIRED(
            "Retired",
            "Asset is retired"
    );

    private final String displayName;
    private final String description;

    AssetStatus(
            String displayName,
            String description
    ) {
        this.displayName = displayName;
        this.description = description;
    }

    // ============================================================
    // GETTERS
    // ============================================================

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    // ============================================================
    // JSON INPUT
    // ============================================================

    /**
     * Allows the backend to accept:
     *
     * AVAILABLE
     * available
     * Available
     *
     * IN_USE
     * In Use
     * in_use
     *
     * UNDER_MAINTENANCE
     * Under Maintenance
     * under_maintenance
     *
     * RETIRED
     * Retired
     *
     * ACTIVE
     * Active
     *
     * MAINTENANCE
     * Maintenance
     */
    @JsonCreator
    public static AssetStatus fromValue(String value) {

        if (value == null || value.trim().isEmpty()) {
            return AVAILABLE;
        }

        String normalized = value
                .trim()
                .replace("-", "_")
                .replace(" ", "_")
                .toUpperCase(Locale.ROOT);

        switch (normalized) {

            case "AVAILABLE":
                return AVAILABLE;

            case "ACTIVE":
                return AVAILABLE;

            case "IN_USE":
            case "INUSE":
            case "ASSIGNED":
                return IN_USE;

            case "UNDER_MAINTENANCE":
            case "UNDERMAINTENANCE":
            case "MAINTENANCE":
                return UNDER_MAINTENANCE;

            case "RETIRED":
            case "DECOMMISSIONED":
                return RETIRED;

            default:
                throw new IllegalArgumentException(
                        "Invalid asset status: " + value +
                        ". Allowed values: " +
                        "AVAILABLE, IN_USE, UNDER_MAINTENANCE, RETIRED"
                );
        }
    }

    // ============================================================
    // JSON OUTPUT
    // ============================================================

    /**
     * Frontend receives:
     *
     * Available
     * In Use
     * Under Maintenance
     * Retired
     */
    @JsonValue
    public String getJsonValue() {
        return displayName;
    }

    // ============================================================
    // HELPER
    // ============================================================

    public boolean isAvailable() {
        return this == AVAILABLE;
    }

    public boolean isInUse() {
        return this == IN_USE;
    }

    public boolean isUnderMaintenance() {
        return this == UNDER_MAINTENANCE;
    }

    public boolean isRetired() {
        return this == RETIRED;
    }
}