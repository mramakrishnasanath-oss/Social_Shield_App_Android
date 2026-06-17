package com.socialshield.testing.utils;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PdfReporter {
    private static final Logger logger = LogManager.getLogger(PdfReporter.class);

    public static void generatePdfReport(String filePath, List<ExcelReporter.TestResultRecord> testRecords) {
        logger.info("Generating PDF Report at: " + filePath);
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            // Set Title & Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font passFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(0, 128, 0));
            Font failFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.RED);
            Font skipFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.ORANGE);

            // Cover/Header Paragraph
            Paragraph title = new Paragraph("SOCIALSHIELD AUTOMATION TEST REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Metadata info
            Paragraph metadata = new Paragraph();
            metadata.setFont(normalFont);
            metadata.add("Report Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
            metadata.add("Framework Version: Appium 2.x & TestNG\n");
            metadata.add("Platform: Android Emulator (UiAutomator2)\n");
            metadata.add("QA Architect: Antigravity AI\n\n");
            metadata.setSpacingAfter(20);
            document.add(metadata);

            // Statistics Calculation
            int total = testRecords.size();
            int passed = 0;
            int failed = 0;
            int skipped = 0;
            double totalTime = 0.0;
            for (ExcelReporter.TestResultRecord r : testRecords) {
                totalTime += r.executionTimeSec;
                if ("PASS".equalsIgnoreCase(r.status)) passed++;
                else if ("FAIL".equalsIgnoreCase(r.status)) failed++;
                else skipped++;
            }
            double passRate = total > 0 ? ((double) passed / total) * 100 : 0.0;

            // Summary Table
            Paragraph summaryTitle = new Paragraph("EXECUTIVE TEST EXECUTION SUMMARY", sectionFont);
            summaryTitle.setSpacingAfter(8);
            document.add(summaryTitle);

            PdfPTable summaryTable = new PdfPTable(5);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingAfter(20);

            String[] sumHeaders = {"Total Tests", "Passed", "Failed", "Skipped", "Pass Rate (%)"};
            for (String sh : sumHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(sh, headerFont));
                cell.setBackgroundColor(Color.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                summaryTable.addCell(cell);
            }

            summaryTable.addCell(createCenterCell(String.valueOf(total), normalFont));
            summaryTable.addCell(createCenterCell(String.valueOf(passed), normalFont));
            summaryTable.addCell(createCenterCell(String.valueOf(failed), normalFont));
            summaryTable.addCell(createCenterCell(String.valueOf(skipped), normalFont));
            summaryTable.addCell(createCenterCell(String.format("%.2f%%", passRate), normalFont));
            document.add(summaryTable);

            // Detailed Test Cases Table
            Paragraph detailTitle = new Paragraph("DETAILED TEST CASE EXECUTION LOG", sectionFont);
            detailTitle.setSpacingAfter(8);
            document.add(detailTitle);

            float[] widths = {1.5f, 2.0f, 1.0f, 1.2f, 3.3f, 1.0f};
            PdfPTable logTable = new PdfPTable(widths);
            logTable.setWidthPercentage(100);

            String[] logHeaders = {"Case ID", "Module", "Result", "Duration", "Execution Message / Error Details", "Defect"};
            for (String lh : logHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(lh, headerFont));
                cell.setBackgroundColor(Color.BLUE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                logTable.addCell(cell);
            }

            for (ExcelReporter.TestResultRecord r : testRecords) {
                logTable.addCell(createCell(r.testCaseId, normalFont));
                logTable.addCell(createCell(r.module, normalFont));
                
                PdfPCell resCell = new PdfPCell();
                resCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                resCell.setPadding(5);
                if ("PASS".equalsIgnoreCase(r.status)) {
                    resCell.setPhrase(new Phrase(r.status, passFont));
                } else if ("FAIL".equalsIgnoreCase(r.status)) {
                    resCell.setPhrase(new Phrase(r.status, failFont));
                } else {
                    resCell.setPhrase(new Phrase(r.status, skipFont));
                }
                logTable.addCell(resCell);

                logTable.addCell(createCenterCell(String.format("%.2fs", r.executionTimeSec), normalFont));
                logTable.addCell(createCell(r.message != null ? r.message : "Success", normalFont));
                logTable.addCell(createCell(r.defectId != null ? r.defectId : "N/A", normalFont));
            }

            document.add(logTable);
            document.close();
            logger.info("PDF Report generated successfully.");
        } catch (DocumentException | IOException e) {
            logger.error("Failed to generate PDF Report", e);
        }
    }

    private static PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }

    private static PdfPCell createCenterCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }
}
