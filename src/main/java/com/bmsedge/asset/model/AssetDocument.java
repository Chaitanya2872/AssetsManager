package com.bmsedge.asset.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "asset_documents")
public class AssetDocument {

    @Id
    @Column(name = "document_id", nullable = false, unique = true, updatable = false)
    private String documentId;

    @Column(name = "asset_id", nullable = false)
    private String assetId;

    @Column(name = "vendor_id")
    private String vendorId;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "document_type", nullable = false)
    private String documentType; // PDF, DOCX, IMAGE, etc.

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize; // in bytes

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "category")
    private String category; // WARRANTY, MANUAL, INVOICE, MAINTENANCE_REPORT, COMPLIANCE, OTHER

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    @Column(name = "tags", length = 500)
    private String tags; // Comma-separated tags

    @Column(name = "version")
    private String version;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ======================
    // Constructors
    // ======================
    public AssetDocument() {
    }

    public AssetDocument(String assetId, String documentName, String filePath, String documentType) {
        this.assetId = assetId;
        this.documentName = documentName;
        this.filePath = filePath;
        this.documentType = documentType;
    }

    // ======================
    // Lifecycle hooks
    // ======================
    @PrePersist
    protected void onCreate() {
        this.documentId = "DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ======================
    // Getters & Setters
    // ======================
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "AssetDocument{" +
                "documentId='" + documentId + '\'' +
                ", documentName='" + documentName + '\'' +
                ", assetId='" + assetId + '\'' +
                ", documentType='" + documentType + '\'' +
                '}';
    }
}