package com.bmsedge.asset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating a new Asset
 */
public class AssetCreateRequest {

    @NotBlank(message = "Asset name is required")
    @Size(min = 3, max = 100, message = "Asset name must be between 3 and 100 characters")
    private String assetName;

    @NotBlank(message = "Category is required")
    @Size(min = 3, max = 50, message = "Category must be between 3 and 50 characters")
    private String category;

    private String location;

    // Constructors
    public AssetCreateRequest() {
    }

    public AssetCreateRequest(String assetName, String category) {
        this.assetName = assetName;
        this.category = category;
    }

    public AssetCreateRequest(String assetName, String category, String location) {
        this.assetName = assetName;
        this.category = category;
        this.location = location;
    }

    // Getters and Setters
    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "AssetCreateRequest{" +
                "assetName='" + assetName + '\'' +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}