package com.bmsedge.asset.service;

import com.bmsedge.asset.exception.DocumentNotFoundException;
import com.bmsedge.asset.model.AssetDocument;
import com.bmsedge.asset.repository.AssetDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    private final AssetDocumentRepository repository;
    private final Path fileStorageLocation;

    public DocumentService(
            AssetDocumentRepository repository,
            @Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.repository = repository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
            logger.info("File storage location created: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            logger.error("Could not create upload directory", ex);
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    public AssetDocument uploadDocument(
            MultipartFile file,
            String assetId,
            String vendorId,
            String category,
            String description,
            String tags) {

        logger.info("Uploading document for asset: {}", assetId);

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        int extensionStart = originalFilename.lastIndexOf('.');
        String fileExtension = extensionStart < 0
                ? ""
                : originalFilename.substring(extensionStart);
        if (!fileExtension.matches("\\.[A-Za-z0-9]{1,10}")) {
            fileExtension = "";
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Validate file type
            String contentType = file.getContentType();
            if (!isValidFileType(contentType)) {
                throw new IllegalArgumentException("Invalid file type. Only PDF, DOCX, PNG, JPG and JPEG files are allowed.");
            }

            // Validate file size (10MB max)
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            AssetDocument document = new AssetDocument();
            document.setAssetId(assetId);
            document.setVendorId(vendorId);
            document.setDocumentName(originalFilename);
            document.setFilePath(targetLocation.toString());
            document.setFileSize(file.getSize());
            document.setMimeType(contentType);
            document.setCategory(category);
            document.setDescription(description);
            document.setTags(tags);
            document.setDocumentType(determineDocumentType(contentType));

            AssetDocument savedDocument = repository.save(document);
            logger.info("Document uploaded successfully with ID: {}", savedDocument.getDocumentId());

            return savedDocument;
        } catch (IOException ex) {
            logger.error("Could not store file {}", fileName, ex);
            throw new RuntimeException("Could not store file " + fileName, ex);
        }
    }

    private boolean isValidFileType(String contentType) {
        if (contentType == null) {
            return false;
        }

        String normalizedType = contentType.toLowerCase(Locale.ROOT);
        return normalizedType.equals("application/pdf")
                || normalizedType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                || isSupportedImageType(normalizedType);
    }

    public AssetDocument uploadAssetImage(MultipartFile file, String assetId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an image file");
        }

        String contentType = file.getContentType() == null
                ? ""
                : file.getContentType().toLowerCase(Locale.ROOT);

        if (!isSupportedImageType(contentType)) {
            throw new IllegalArgumentException("Image must be a PNG, JPG, JPEG, GIF, or WebP file");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Image size exceeds maximum limit of 10MB");
        }

        return uploadDocument(file, assetId, null, "ASSET_IMAGE", "Asset image", "asset-image");
    }

    private boolean isSupportedImageType(String contentType) {
        return "image/png".equals(contentType)
                || "image/jpeg".equals(contentType)
                || "image/jpg".equals(contentType)
                || "image/gif".equals(contentType)
                || "image/webp".equals(contentType);
    }

    private String determineDocumentType(String mimeType) {
        if (mimeType == null) return "OTHER";
        if (mimeType.contains("pdf")) return "PDF";
        if (mimeType.contains("wordprocessingml")) return "DOCX";
        if (mimeType.contains("image")) return "IMAGE";
        return "OTHER";
    }

    @Transactional(readOnly = true)
    public AssetDocument get(String id) {
        logger.debug("Fetching document with ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Document not found with ID: {}", id);
                    return new DocumentNotFoundException(id);
                });
    }

    @Transactional(readOnly = true)
    public List<AssetDocument> getAll() {
        logger.debug("Fetching all documents");
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AssetDocument> getByAsset(String assetId) {
        logger.debug("Fetching documents for asset: {}", assetId);
        return repository.findByAssetIdOrderByCreatedAtDesc(assetId);
    }

    @Transactional(readOnly = true)
    public List<AssetDocument> getByVendor(String vendorId) {
        logger.debug("Fetching documents for vendor: {}", vendorId);
        return repository.findByVendorId(vendorId);
    }

    @Transactional(readOnly = true)
    public List<AssetDocument> getByCategory(String category) {
        logger.debug("Fetching documents by category: {}", category);
        return repository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<AssetDocument> searchDocuments(String keyword) {
        logger.debug("Searching documents with keyword: {}", keyword);
        return repository.searchDocuments(keyword);
    }

    public Resource downloadDocument(String id) {
        logger.info("Downloading document with ID: {}", id);

        AssetDocument document = get(id);

        try {
            Path filePath = Paths.get(document.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                logger.info("Document downloaded successfully: {}", document.getDocumentName());
                return resource;
            } else {
                logger.error("File not found or not readable: {}", document.getDocumentName());
                throw new DocumentNotFoundException("file", document.getDocumentName());
            }
        } catch (MalformedURLException ex) {
            logger.error("File not found: {}", document.getDocumentName(), ex);
            throw new RuntimeException("File not found: " + document.getDocumentName(), ex);
        }
    }

    public void delete(String id) {
        logger.info("Deleting document with ID: {}", id);

        AssetDocument document = get(id);

        try {
            Path filePath = Paths.get(document.getFilePath());
            Files.deleteIfExists(filePath);
            logger.info("Physical file deleted: {}", document.getDocumentName());
        } catch (IOException ex) {
            logger.warn("Could not delete physical file: {}", document.getDocumentName(), ex);
            // Continue with database deletion even if file deletion fails
        }

        repository.deleteById(id);
        logger.info("Document deleted successfully from database: {}", id);
    }

    @Transactional(readOnly = true)
    public long countByAsset(String assetId) {
        return repository.countByAssetId(assetId);
    }

    @Transactional(readOnly = true)
    public long countByVendor(String vendorId) {
        return repository.countByVendorId(vendorId);
    }

    @Transactional(readOnly = true)
    public long countByCategory(String category) {
        return repository.countByCategory(category);
    }
}