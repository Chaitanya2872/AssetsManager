package com.bmsedge.asset.controllers;

import com.bmsedge.asset.model.AssetDocument;
import com.bmsedge.asset.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocumentControllerTest {

    @Test
    void previewsUploadedImageInline() {
        DocumentService documentService = mock(DocumentService.class);
        DocumentController controller = new DocumentController(documentService);
        AssetDocument document = new AssetDocument();
        document.setDocumentType("IMAGE");
        document.setMimeType("image/png");
        document.setDocumentName("asset photo.png");
        ByteArrayResource image = new ByteArrayResource(new byte[]{1, 2, 3});

        when(documentService.get("doc-1")).thenReturn(document);
        when(documentService.downloadDocument("doc-1")).thenReturn(image);

        var response = controller.previewImage("doc-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
        String contentDisposition =
            response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertTrue(contentDisposition.startsWith("inline;"));
        assertTrue(contentDisposition.contains("filename*=UTF-8''asset%20photo.png"));
        assertEquals(image, response.getBody());
        verify(documentService).downloadDocument("doc-1");
    }

    @Test
    void rejectsPreviewForNonImageDocument() {
        DocumentService documentService = mock(DocumentService.class);
        DocumentController controller = new DocumentController(documentService);
        AssetDocument document = new AssetDocument();
        document.setDocumentType("PDF");
        document.setMimeType("application/pdf");
        when(documentService.get("doc-2")).thenReturn(document);

        var response = controller.previewImage("doc-2");

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        verify(documentService).get("doc-2");
        verify(documentService, never()).downloadDocument("doc-2");
    }
}