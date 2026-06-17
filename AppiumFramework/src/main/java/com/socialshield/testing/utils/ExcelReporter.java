package com.socialshield.testing.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExcelReporter {
    private static final Logger logger = LogManager.getLogger(ExcelReporter.class);
    private static final List<TestResultRecord> records = Collections.synchronizedList(new ArrayList<>());

    public static class TestResultRecord {
        public String testCaseId;
        public String module;
        public String status; // PASS, FAIL, SKIP
        public double executionTimeSec;
        public String message;
        public String defectId;

        public TestResultRecord(String testCaseId, String module, String status, double executionTimeSec, String message, String defectId) {
            this.testCaseId = testCaseId;
            this.module = module;
            this.status = status;
            this.executionTimeSec = executionTimeSec;
            this.message = message;
            this.defectId = defectId;
        }
    }

    public static void addRecord(String testCaseId, String module, String status, double executionTimeSec, String message, String defectId) {
        records.add(new TestResultRecord(testCaseId, module, status, executionTimeSec, message, defectId));
    }

    public static List<TestResultRecord> getRecords() {
        return records;
    }

    public static void generateExcelReport(String filePath) {
        logger.info("Generating Excel Report at: " + filePath);
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(file)) {

            Sheet sheet = workbook.createSheet("Execution Summary");

            // Header Font & Style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);

            // Columns
            String[] headers = {"Test Case ID", "Module", "Status", "Execution Time (s)", "Result Message / Error Details", "Defect ID"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Cell Styles for Pass/Fail/Skip
            CellStyle passStyle = workbook.createCellStyle();
            Font passFont = workbook.createFont();
            passFont.setColor(IndexedColors.GREEN.getIndex());
            passFont.setBold(true);
            passStyle.setFont(passFont);

            CellStyle failStyle = workbook.createCellStyle();
            Font failFont = workbook.createFont();
            failFont.setColor(IndexedColors.RED.getIndex());
            failFont.setBold(true);
            failStyle.setFont(failFont);

            CellStyle skipStyle = workbook.createCellStyle();
            Font skipFont = workbook.createFont();
            skipFont.setColor(IndexedColors.GOLD.getIndex());
            skipFont.setBold(true);
            skipStyle.setFont(skipFont);

            int rowNum = 1;
            synchronized (records) {
                for (TestResultRecord record : records) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(record.testCaseId);
                    row.createCell(1).setCellValue(record.module);

                    Cell statusCell = row.createCell(2);
                    statusCell.setCellValue(record.status);
                    if ("PASS".equalsIgnoreCase(record.status)) {
                        statusCell.setCellStyle(passStyle);
                    } else if ("FAIL".equalsIgnoreCase(record.status)) {
                        statusCell.setCellStyle(failStyle);
                    } else {
                        statusCell.setCellStyle(skipStyle);
                    }

                    row.createCell(3).setCellValue(record.executionTimeSec);
                    row.createCell(4).setCellValue(record.message);
                    row.createCell(5).setCellValue(record.defectId);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fos);
            logger.info("Excel Report written successfully.");
        } catch (IOException e) {
            logger.error("Failed to generate Excel Report", e);
        }
    }
}
