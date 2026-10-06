package com.bmsedge.asset.service;

import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.model.AssetStatus;
import com.bmsedge.asset.repository.AssetRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
    @Transactional
    public ImportResult importAssetsFromExcel(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please upload a non-empty Excel file");
        }

        List<Asset> assetsToSave = new ArrayList<>();
        List<String> rowErrors = new ArrayList<>();
        Set<String> serialNumbersInFile = new HashSet<>();

        try (InputStream input = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(input)) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new IllegalArgumentException("The workbook contains no sheets");
            }

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter(Locale.ROOT);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("The worksheet must contain a header row");
            }

            Map<String, Integer> columns = new HashMap<>();
            for (Cell cell : headerRow) {
                String header = normalizeHeader(formatter.formatCellValue(cell, evaluator));
                if (!header.isEmpty()) {
                    columns.put(header, cell.getColumnIndex());
                }
            }
            requireHeader(columns, "Asset Name");
            requireHeader(columns, "Asset Category");

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (isRowEmpty(row, formatter, evaluator)) {
                    continue;
                }

                int rowNumber = rowIndex + 1;
                try {
                    Asset asset = parseRowToAsset(row, columns, formatter, evaluator);
                    String serialNumber = asset.getSerialNumber();

                    if (serialNumber != null) {
                        if (!serialNumbersInFile.add(serialNumber)) {
                            throw new IllegalArgumentException(
                                    "Duplicate serial number in this file: " + serialNumber);
                        }
                        if (assetRepository.existsBySerialNumber(serialNumber)) {
                            throw new IllegalArgumentException(
                                    "Serial number already exists: " + serialNumber);
                        }
                    }

                    assetsToSave.add(asset);
                } catch (IllegalArgumentException e) {
                    String message = "Row " + rowNumber + ": " + e.getMessage();
                    rowErrors.add(message);
                    logger.warn("{}", message);
                }
            }
        }

        List<Asset> importedAssets = assetsToSave.isEmpty()
                ? List.of()
                : assetRepository.saveAll(assetsToSave);

        logger.info("Imported {} assets; skipped {} rows", importedAssets.size(), rowErrors.size());
        return new ImportResult(importedAssets, rowErrors);
    }

    public record ImportResult(List<Asset> importedAssets, List<String> rowErrors) {
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
    private Asset parseRowToAsset(
            Row row,
            Map<String, Integer> columns,
            DataFormatter formatter,
            FormulaEvaluator evaluator) {
        Asset asset = new Asset();
        String assetName = getCellValueAsString(row, columns, "Asset Name", formatter, evaluator);
        String assetCategory = getCellValueAsString(row, columns, "Asset Category", formatter, evaluator);
        if (assetName == null) {
            throw new IllegalArgumentException("Asset Name is required");
        }
        if (assetCategory == null) {
            throw new IllegalArgumentException("Asset Category is required");
        }

        asset.setAssetName(assetName);
        asset.setAssetCategory(assetCategory);
        asset.setModelNumber(getCellValueAsString(row, columns, "Model Number", formatter, evaluator));
        asset.setQuantity(getCellValueAsInteger(row, columns, "Quantity", formatter, evaluator));
        if (asset.getQuantity() < 1) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        asset.setSerialNumber(getCellValueAsString(row, columns, "Serial Number", formatter, evaluator));
        asset.setAssetType(getCellValueAsString(row, columns, "Asset Type", formatter, evaluator));
        asset.setManufacturer(getCellValueAsString(row, columns, "Manufacturer", formatter, evaluator));
        asset.setDateOfInstallation(getCellValueAsDate(row, columns, "Date of Installation", formatter, evaluator));
        asset.setLocation(getCellValueAsString(row, columns, "Location", formatter, evaluator));
        asset.setDescription(getCellValueAsString(row, columns, "Description", formatter, evaluator));
        asset.setBranch(getCellValueAsString(row, columns, "Branch", formatter, evaluator));
        asset.setDlpEndDate(getCellValueAsDate(row, columns, "DLP End Date", formatter, evaluator));

        String status = getCellValueAsString(row, columns, "Status", formatter, evaluator);
        try {
            asset.setStatus(AssetStatus.fromValue(status));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Status: " + status);
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
    private String getCellValueAsString(
            Row row,
            Map<String, Integer> columns,
            String header,
            DataFormatter formatter,
            FormulaEvaluator evaluator) {
        Cell cell = getCell(row, columns, header);
        if (cell == null) {
            return null;
        }

        String value = formatter.formatCellValue(cell, evaluator).trim();
        return value.isEmpty() ? null : value;
    }

    private Integer getCellValueAsInteger(
            Row row,
            Map<String, Integer> columns,
            String header,
            DataFormatter formatter,
            FormulaEvaluator evaluator) {
        String value = getCellValueAsString(row, columns, header, formatter, evaluator);
        if (value == null) {
            return 1;
        }

        try {
            return new BigDecimal(value.replace(",", "")).intValueExact();
        } catch (NumberFormatException | ArithmeticException e) {
            throw new IllegalArgumentException("Quantity must be a whole number");
        }
    }

    private LocalDate getCellValueAsDate(
            Row row,
            Map<String, Integer> columns,
            String header,
            DataFormatter formatter,
            FormulaEvaluator evaluator) {
        Cell cell = getCell(row, columns, header);
        if (cell == null) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return DateUtil.getLocalDateTime(cell.getNumericCellValue()).toLocalDate();
            }
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(header + " is not a valid date");
        }

        String value = formatter.formatCellValue(cell, evaluator).trim();
        if (value.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(value, DateTimeFormatter.ofPattern("M/d/yyyy", Locale.US));
            } catch (DateTimeParseException ignored) {
                throw new IllegalArgumentException(header + " must use yyyy-MM-dd or an Excel date cell");
            }
        }
    }

    private Cell getCell(Row row, Map<String, Integer> columns, String header) {
        Integer index = columns.get(normalizeHeader(header));
        return index == null ? null : row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
    }

    private String normalizeHeader(String header) {
        return header == null
                ? ""
                : header.replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ROOT);
    }

    private void requireHeader(Map<String, Integer> columns, String header) {
        if (!columns.containsKey(normalizeHeader(header))) {
            throw new IllegalArgumentException("Missing required column: " + header);
        }
    }

    private boolean isRowEmpty(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (row == null) return true;

        for (Cell cell : row) {
            if (!formatter.formatCellValue(cell, evaluator).isBlank()) {
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