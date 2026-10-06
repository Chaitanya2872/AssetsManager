package com.bmsedge.asset.controllers;

import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.service.AssetService;
import com.bmsedge.asset.service.ExcelService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/assets/excel")
public class ExcelController {

    private final ExcelService excelService;
    private final AssetService assetService;

    public ExcelController(ExcelService excelService, AssetService assetService) {
        this.excelService = excelService;
        this.assetService = assetService;
    }

    /**
     * Import assets from Excel file
     * POST /api/assets/excel/import
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importAssets(
            @RequestParam("file") MultipartFile file) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please upload a file");
                return ResponseEntity.badRequest().body(response);
            }

            // Check file type
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
                response.put("success", false);
                response.put("message", "Please upload an Excel file (.xlsx)");
                return ResponseEntity.badRequest().body(response);
            }

            // Import assets
            ExcelService.ImportResult result = excelService.importAssetsFromExcel(file);
            List<Asset> importedAssets = result.importedAssets();
            List<String> rowErrors = result.rowErrors();
            boolean hasErrors = !rowErrors.isEmpty();
            boolean importedAny = !importedAssets.isEmpty();

            response.put("success", !hasErrors || importedAny);
            response.put("partialSuccess", hasErrors && importedAny);
            response.put("message", hasErrors
                    ? importedAny
                        ? "Some rows were skipped. See rowErrors for details."
                        : "No assets were imported. See rowErrors for details."
                    : "Assets imported successfully");
            response.put("totalImported", importedAssets.size());
            response.put("totalSkipped", rowErrors.size());
            response.put("rowErrors", rowErrors);
            response.put("assets", importedAssets);

            return hasErrors && !importedAny
                    ? ResponseEntity.badRequest().body(response)
                    : ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "The uploaded file could not be read as an .xlsx workbook");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error importing assets: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Export all assets to Excel
     * GET /api/assets/excel/export
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAllAssets() {
        try {
            List<Asset> assets = assetService.getAll();
            byte[] excelBytes = excelService.exportAssetsToExcel(assets);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "assets-export-" + System.currentTimeMillis() + ".xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export filtered assets to Excel
     * GET /api/assets/excel/export/filtered
     */
    @GetMapping("/export/filtered")
    public ResponseEntity<byte[]> exportFilteredAssets(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String branch) {

        try {
            List<Asset> assets;

            // Apply filters (simplified - you can enhance this)
            if (category != null) {
                assets = assetService.getByCategory(category);
            } else {
                assets = assetService.getAll();
            }

            byte[] excelBytes = excelService.exportAssetsToExcel(assets);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "assets-filtered-" + System.currentTimeMillis() + ".xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Download empty template
     * GET /api/assets/excel/template
     */
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            // Create empty template
            List<Asset> emptyList = List.of();
            byte[] excelBytes = excelService.exportAssetsToExcel(emptyList);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "Asset-Management-Template.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}