package com.bmsedge.asset.service;

import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.AssetStatus;
import com.bmsedge.asset.repository.AssetRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);
    private final AssetRepository assetRepository;

    public ExcelService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    /**
     * Import assets from Excel file
     */
    public List<Asset> importAssetsFromExcel(MultipartFile file) throws IOException {

        List<Asset> savedAssets = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header
            if (rows.hasNext()) {
                rows.next();
            }

            int rowNumber = 1;

            while (rows.hasNext()) {
                Row row = rows.next();
                rowNumber++;

                try {
                    Asset asset = parseRowToAsset(row, rowNumber);

                    if (asset == null) {
                        continue;
                    }

                    // 🔑 CHECK DUPLICATE SERIAL NUMBER
                    String serialNumber = asset.getSerialNumber();
                    if (serialNumber != null &&
                            assetRepository.existsById(serialNumber)) {

                        logger.warn("Skipping row {} - duplicate serial number: {}",
                                rowNumber, serialNumber);
                        continue;
                    }

                    Asset saved = assetRepository.save(asset);
                    savedAssets.add(saved);

                } catch (Exception e) {
                    logger.error("Error processing row {}: {}", rowNumber, e.getMessage());
                }
            }

            logger.info("Successfully imported {} assets from Excel", savedAssets.size());
            return savedAssets;
        }
    }


    /**
     * Export assets to Excel file
     */
    public byte[] exportAssetsToExcel(List<Asset> assets) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Assets");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "S.No", "Model Number", "Asset Name", "Quantity", "Serial Number",
                    "Asset Category", "Asset Type", "Manufacturer", "Date of Installation",
                    "Location", "Description", "Branch", "DLP End Date", "Status"
            };

            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            CellStyle dateStyle = createDateStyle(workbook);
            int rowNum = 1;

            for (Asset asset : assets) {
                Row row = sheet.createRow(rowNum++);
                writeAssetToRow(asset, row, dateStyle, rowNum - 1);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            logger.info("Successfully exported {} assets to Excel", assets.size());

            return out.toByteArray();
        }
    }

    /**
     * Parse Excel row to Asset object
     */
    private Asset parseRowToAsset(Row row, int rowNumber) {
        // Skip empty rows
        if (isRowEmpty(row)) {
            return null;
        }

        Asset asset = new Asset();

        try {
            // S.No is auto-generated, skip column 0
            asset.setModelNumber(getCellValueAsString(row.getCell(1)));
            asset.setAssetName(getCellValueAsString(row.getCell(2)));
            asset.setQuantity(getCellValueAsInteger(row.getCell(3)));
            asset.setSerialNumber(getCellValueAsString(row.getCell(4)));
            asset.setAssetCategory(getCellValueAsString(row.getCell(5)));
            asset.setAssetType(getCellValueAsString(row.getCell(6)));
            asset.setManufacturer(getCellValueAsString(row.getCell(7)));
            asset.setDateOfInstallation(getCellValueAsDate(row.getCell(8)));
            asset.setLocation(getCellValueAsString(row.getCell(9)));
            asset.setDescription(getCellValueAsString(row.getCell(10)));
            asset.setBranch(getCellValueAsString(row.getCell(11)));
            asset.setDlpEndDate(getCellValueAsDate(row.getCell(12)));

            String statusStr = getCellValueAsString(row.getCell(13));
            asset.setStatus(parseStatus(statusStr));

            // Generate asset ID if serial number exists, otherwise use row number
            String id = asset.getSerialNumber() != null ?
                    asset.getSerialNumber() :
                    "AST-" + System.currentTimeMillis() + "-" + rowNumber;
            asset.setAssetId(id);

        } catch (Exception e) {
            logger.error("Error parsing row {}: {}", rowNumber, e.getMessage());
            return null;
        }

        return asset;
    }

    /**
     * Write Asset object to Excel row
     */
    private void writeAssetToRow(Asset asset, Row row, CellStyle dateStyle, int sNo) {
        row.createCell(0).setCellValue(sNo);
        row.createCell(1).setCellValue(asset.getModelNumber());
        row.createCell(2).setCellValue(asset.getAssetName());
        row.createCell(3).setCellValue(asset.getQuantity() != null ? asset.getQuantity() : 1);
        row.createCell(4).setCellValue(asset.getSerialNumber());
        row.createCell(5).setCellValue(asset.getAssetCategory());
        row.createCell(6).setCellValue(asset.getAssetType());
        row.createCell(7).setCellValue(asset.getManufacturer());

        if (asset.getDateOfInstallation() != null) {
            Cell dateCell = row.createCell(8);
            dateCell.setCellValue(Date.from(asset.getDateOfInstallation()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            dateCell.setCellStyle(dateStyle);
        }

        row.createCell(9).setCellValue(asset.getLocation());
        row.createCell(10).setCellValue(asset.getDescription());
        row.createCell(11).setCellValue(asset.getBranch());

        if (asset.getDlpEndDate() != null) {
            Cell dateCell = row.createCell(12);
            dateCell.setCellValue(Date.from(asset.getDlpEndDate()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            dateCell.setCellStyle(dateStyle);
        }

        row.createCell(13).setCellValue(asset.getStatus().toString());
    }

    // Helper methods
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return 1;

        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 1;
                }
            default:
                return 1;
        }
    }

    private LocalDate getCellValueAsDate(Cell cell) {
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        } catch (Exception e) {
            logger.warn("Error parsing date from cell: {}", e.getMessage());
        }

        return null;
    }

    private AssetStatus parseStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return AssetStatus.AVAILABLE;
        }

        String normalized = status.trim().toUpperCase();

        if (normalized.contains("IN USE")) {
            return AssetStatus.IN_USE;
        }

        if (normalized.contains("AVAILABLE")) {
            return AssetStatus.AVAILABLE;
        }

        if (normalized.contains("MAINTENANCE")) {
            return AssetStatus.UNDER_MAINTENANCE;
        }

        logger.warn("Unknown status '{}', defaulting to AVAILABLE", status);
        return AssetStatus.AVAILABLE;
    }


    private boolean isRowEmpty(Row row) {
        if (row == null) return true;

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd"));
        return style;
    }
}