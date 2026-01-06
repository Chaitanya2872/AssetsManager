package com.bmsedge.asset.controllers;

import com.bmsedge.asset.model.AssetDocument;
import com.bmsedge.asset.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<AssetDocument> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("assetId") String assetId,
            @RequestParam(value = "vendorId", required = false) String vendorId,
            @RequestParam("category") String category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tags", required = false) String tags) {

        try {
            AssetDocument document = documentService.uploadDocument(
                    file, assetId, vendorId, category, description, tags);
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetDocument> get(@PathVariable String id) {
        AssetDocument document = documentService.get(id);
        return ResponseEntity.ok(document);
    }

    @GetMapping
    public ResponseEntity<List<AssetDocument>> getAll() {
        List<AssetDocument> documents = documentService.getAll();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<AssetDocument>> getByAsset(@PathVariable String assetId) {
        List<AssetDocument> documents = documentService.getByAsset(assetId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<AssetDocument>> getByVendor(@PathVariable String vendorId) {
        List<AssetDocument> documents = documentService.getByVendor(vendorId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<AssetDocument>> getByCategory(@PathVariable String category) {
        List<AssetDocument> documents = documentService.getByCategory(category);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AssetDocument>> searchDocuments(@RequestParam String keyword) {
        List<AssetDocument> documents = documentService.searchDocuments(keyword);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String id) {
        Resource resource = documentService.downloadDocument(id);
        AssetDocument document = documentService.get(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getDocumentName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/asset/{assetId}")
    public ResponseEntity<Long> countByAsset(@PathVariable String assetId) {
        long count = documentService.countByAsset(assetId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/vendor/{vendorId}")
    public ResponseEntity<Long> countByVendor(@PathVariable String vendorId) {
        long count = documentService.countByVendor(vendorId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/category/{category}")
    public ResponseEntity<Long> countByCategory(@PathVariable String category) {
        long count = documentService.countByCategory(category);
        return ResponseEntity.ok(count);
    }
}