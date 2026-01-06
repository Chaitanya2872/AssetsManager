package com.bmsedge.asset.model;

/**
 * Enum representing the various states an asset can be in
 */
public enum AssetStatus {

    /**
     * Asset is available and ready to be used
     */
    AVAILABLE("Available", "Asset is ready for use"),

    /**
     * Asset is currently being used
     */
    IN_USE("In Use", "Asset is currently in use"),

    /**
     * Asset is under maintenance or repair
     */
    UNDER_MAINTENANCE("Under Maintenance", "Asset is under maintenance or repair"),

    /**
     * Asset has been retired and is no longer in service
     */
    RETIRED("Retired", "Asset is retired");

    private final String displayName;
    private final String description;

    AssetStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
