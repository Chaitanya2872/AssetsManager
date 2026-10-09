package com.bmsedge.asset.controllers;

import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.AssetDocument;
import com.bmsedge.asset.service.AssetService;
import com.bmsedge.asset.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssetImageUploadTest {

    @Test
    void uploadsImageAndStoresItsPreviewUrl() {
        AssetService assetService = mock(AssetService.class);
        DocumentService documentService = mock(DocumentService.class);
        AssetController controller = new AssetController(assetService, documentService);
        MockMultipartFile file = new MockMultipartFile(
                "file", "device.png", "image/png", new byte[]{1, 2, 3});
        AssetDocument document = new AssetDocument();
        document.setDocumentId("DOC-123");
        Asset asset = new Asset();

        when(assetService.get("asset-123")).thenReturn(asset);
        when(documentService.uploadAssetImage(file, "asset-123")).thenReturn(document);
        when(assetService.updateImageUrl(eq("asset-123"), anyString())).thenAnswer(invocation -> {
            asset.setAssetImageUrl(invocation.getArgument(1));
            return asset;
        });

        var response = controller.uploadAssetImage("asset-123", file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("/api/documents/DOC-123/preview",
                response.getBody().getAssetImageUrl());
        verify(documentService).uploadAssetImage(file, "asset-123");
        verify(assetService).updateImageUrl(
                "asset-123", "/api/documents/DOC-123/preview");
    }

    @Test
    void createsAssetWithOptionalImageUsingMultipartRequest() {
        AssetService assetService = mock(AssetService.class);
        DocumentService documentService = mock(DocumentService.class);
        AssetController controller = new AssetController(assetService, documentService);
        Asset request = new Asset();
        request.setAssetName("Laptop");
        request.setAssetCategory("IT");
        request.setLocation("HQ");

        Asset created = new Asset();
        created.setAssetId("asset-123");
        created.setAssetName("Laptop");
        created.setAssetCategory("IT");
        created.setLocation("HQ");

        MockMultipartFile image = new MockMultipartFile(
                "image", "device.png", "image/png", new byte[]{1, 2, 3});
        AssetDocument document = new AssetDocument();
        document.setDocumentId("DOC-123");

        when(assetService.create("Laptop", "IT", "HQ", null)).thenReturn(created);
        when(documentService.uploadAssetImage(image, "asset-123")).thenReturn(document);
        when(assetService.updateImageUrl("asset-123", "/api/documents/DOC-123/preview")).thenAnswer(inv -> {
            created.setAssetImageUrl(inv.getArgument(1));
            return created;
        });
        when(assetService.update(eq("asset-123"), eq(created))).thenAnswer(inv -> {
            created.setAssetImageUrl(created.getAssetImageUrl());
            return created;
        });

        var response = controller.createAsset(request, image);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("asset-123", response.getBody().getAssetId());
        assertEquals("/api/documents/DOC-123/preview", response.getBody().getAssetImageUrl());
        verify(documentService).uploadAssetImage(image, "asset-123");
        verify(assetService).updateImageUrl("asset-123", "/api/documents/DOC-123/preview");
    }

    @Test
    void getsImageFromAssetImageUrl() {
        AssetService assetService = mock(AssetService.class);
        DocumentService documentService = mock(DocumentService.class);
        AssetController controller = new AssetController(assetService, documentService);
        Asset asset = new Asset();
        asset.setAssetImageUrl("/api/documents/DOC-123/preview");
        AssetDocument image = new AssetDocument();
        image.setAssetId("asset-123");
        image.setDocumentType("IMAGE");
        image.setMimeType("image/png");
        image.setDocumentName("device.png");
        ByteArrayResource bytes = new ByteArrayResource(new byte[]{1, 2, 3});
        when(assetService.get("asset-123")).thenReturn(asset);
        when(documentService.get("DOC-123")).thenReturn(image);
        when(documentService.downloadDocument("DOC-123")).thenReturn(bytes);

        var response = controller.getAssetImage("asset-123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
        assertEquals(bytes, response.getBody());
        verify(documentService).downloadDocument("DOC-123");
    }

    @Test
    void doesNotServeImageDocumentFromAnotherAsset() {
        AssetService assetService = mock(AssetService.class);
        DocumentService documentService = mock(DocumentService.class);
        AssetController controller = new AssetController(assetService, documentService);
        Asset asset = new Asset();
        asset.setAssetImageUrl("/api/documents/DOC-123/preview");
        AssetDocument image = new AssetDocument();
        image.setAssetId("different-asset");
        image.setDocumentType("IMAGE");
        image.setMimeType("image/png");
        when(assetService.get("asset-123")).thenReturn(asset);
        when(documentService.get("DOC-123")).thenReturn(image);

        var response = controller.getAssetImage("asset-123");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(documentService, never()).downloadDocument("DOC-123");
    }
}