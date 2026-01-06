package com.bmsedge.asset.exception;

/**
 * Exception thrown when an asset is not found in the database
 */
public class AssetNotFoundException extends RuntimeException {

    private final String assetId;

    public AssetNotFoundException(String assetId) {
        super(String.format("Asset not found with ID: %s", assetId));
        this.assetId = assetId;
    }

    public AssetNotFoundException(String assetId, String message) {
        super(message);
        this.assetId = assetId;
    }

    public AssetNotFoundException(String assetId, String message, Throwable cause) {
        super(message, cause);
        this.assetId = assetId;
    }

    public String getAssetId() {
        return assetId;
    }
}