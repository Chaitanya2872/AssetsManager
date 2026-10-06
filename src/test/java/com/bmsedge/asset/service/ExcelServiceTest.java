package com.bmsedge.asset.service;

import com.bmsedge.asset.model.Asset;
import com.bmsedge.asset.repository.AssetRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExcelServiceTest {

    @Test
    void importsValidRowsAndReportsDuplicateAndInvalidRows() throws IOException {
        AssetRepository repository = mock(AssetRepository.class);
        when(repository.existsBySerialNumber("SN-1001")).thenReturn(false);
        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        ExcelService service = new ExcelService(repository);

        ExcelService.ImportResult result = service.importAssetsFromExcel(createWorkbook());

        assertEquals(2, result.importedAssets().size());
        assertEquals("SN-1001", result.importedAssets().get(0).getSerialNumber());
        assertEquals(LocalDate.of(2027, 1, 5),
                result.importedAssets().get(0).getDateOfInstallation());
        assertEquals(LocalDate.of(2028, 6, 15),
                result.importedAssets().get(1).getDateOfInstallation());
        assertEquals(2, result.rowErrors().size());
        assertTrue(result.rowErrors().get(0).contains("Row 4"));
        assertTrue(result.rowErrors().get(1).contains("Asset Category is required"));
        verify(repository).existsBySerialNumber("SN-1001");
        verify(repository).saveAll(anyList());
    }

    @Test
    void skipsSerialNumberAlreadyInDatabase() throws IOException {
        AssetRepository repository = mock(AssetRepository.class);
        when(repository.existsBySerialNumber("SN-1001")).thenReturn(true);
        ExcelService service = new ExcelService(repository);

        ExcelService.ImportResult result = service.importAssetsFromExcel(createSingleAssetWorkbook());

        assertTrue(result.importedAssets().isEmpty());
        assertEquals(1, result.rowErrors().size());
        assertTrue(result.rowErrors().get(0).contains("Serial number already exists"));
        verify(repository, never()).saveAll(anyList());
    }

    private MockMultipartFile createWorkbook() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Assets");
            createHeader(sheet.createRow(0));

            Row firstAsset = sheet.createRow(1);
            setValues(firstAsset, "IT", "Laptop", "SN-1001", "4", null, "In Use");
            Cell dateCell = firstAsset.getCell(4);
            dateCell.setCellValue(java.sql.Date.valueOf(LocalDate.of(2027, 1, 5)));
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd"));
            dateCell.setCellStyle(dateStyle);

            Row secondAsset = sheet.createRow(2);
            setValues(secondAsset, "IT", "Monitor", "", "2", "2028-06-15", "Available");

            Row duplicate = sheet.createRow(3);
            setValues(duplicate, "IT", "Spare laptop", "SN-1001", "1", "", "Available");

            Row invalid = sheet.createRow(4);
            setValues(invalid, "", "Missing category", "SN-1002", "1", "", "Available");

            workbook.write(output);
            return new MockMultipartFile("file", "assets.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    output.toByteArray());
        }
    }

    private MockMultipartFile createSingleAssetWorkbook() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Assets");
            createHeader(sheet.createRow(0));
            setValues(sheet.createRow(1), "IT", "Laptop", "SN-1001", "1", "2027-01-05", "Available");
            workbook.write(output);
            return new MockMultipartFile("file", "assets.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    output.toByteArray());
        }
    }

    private void createHeader(Row row) {
        String[] headers = {
                "Asset Category", "Asset Name", "Serial Number", "Quantity",
                "Date of Installation", "Status"
        };
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
        }
    }

    private void setValues(
            Row row,
            String category,
            String name,
            String serialNumber,
            String quantity,
            String installationDate,
            String status) {
        row.createCell(0).setCellValue(category);
        row.createCell(1).setCellValue(name);
        row.createCell(2).setCellValue(serialNumber);
        row.createCell(3).setCellValue(quantity);
        row.createCell(4).setCellValue(installationDate == null ? "" : installationDate);
        row.createCell(5).setCellValue(status);
    }
}