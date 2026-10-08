package com.bmsedge.asset.controllers;

import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.AssetDocument;
import com.bmsedge.asset.service.AssetService;
import com.bmsedge.asset.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
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

        MockHttpServletRequest request = new MockHttpServletRequest(
                "POST", "/api/assets/asset-123/image");
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8088);

        var response = controller.uploadAssetImage("asset-123", file, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("http://localhost:8088/api/documents/DOC-123/preview",
                response.getBody().getAssetImageUrl());
        verify(documentService).uploadAssetImage(file, "asset-123");
        verify(assetService).updateImageUrl(
                "asset-123", "http://localhost:8088/api/documents/DOC-123/preview");
    }

        @Test
        void getsImageFromAssetImageUrl() {
                AssetService assetService = mock(AssetService.class);
                DocumentService documentService = mock(DocumentService.class);
                AssetController controller = new AssetController(assetService, documentService);
                Asset asset = new Asset();
                asset.setAssetImageUrl("http://localhost:8088/api/documents/DOC-123/preview");
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