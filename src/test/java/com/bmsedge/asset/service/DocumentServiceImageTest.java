package com.bmsedge.asset.service;

import com.bmsedge.asset.model.AssetDocument;
import com.bmsedge.asset.repository.AssetDocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DocumentServiceImageTest {

    @TempDir
    Path uploadDirectory;

    @Test
    void storesExtensionlessImageAsAssetImage() throws IOException {
        AssetDocumentRepository repository = mock(AssetDocumentRepository.class);
        when(repository.save(any(AssetDocument.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        DocumentService service = new DocumentService(repository, uploadDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file", "camera upload", "image/webp", new byte[]{4, 5, 6});

        AssetDocument document = service.uploadAssetImage(file, "asset-123");

        assertEquals("asset-123", document.getAssetId());
        assertEquals("IMAGE", document.getDocumentType());
        assertEquals("image/webp", document.getMimeType());
        assertEquals("ASSET_IMAGE", document.getCategory());
        assertTrue(Files.exists(Path.of(document.getFilePath())));
    }

    @Test
    void rejectsNonImageFilesForAssetImages() {
        AssetDocumentRepository repository = mock(AssetDocumentRepository.class);
        DocumentService service = new DocumentService(repository, uploadDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file", "notes.txt", "text/plain", new byte[]{1});

        assertThrows(IllegalArgumentException.class,
                () -> service.uploadAssetImage(file, "asset-123"));
    }
}