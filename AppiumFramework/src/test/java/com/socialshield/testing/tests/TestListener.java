package com.socialshield.testing.tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.socialshield.testing.driver.DriverManager;
import com.socialshield.testing.utils.*;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;

public class TestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);
    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test Suite Execution Started: " + context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        startTime.set(System.currentTimeMillis());
        String methodName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        logger.info("Starting Test Case: " + methodName);

        // Create Extent Test
        ExtentTest test = ExtentManager.getInstance().createTest(methodName, description);
        ExtentManager.setTest(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long end = System.currentTimeMillis();
        double durationSec = (end - startTime.get()) / 1000.0;
        String methodName = result.getMethod().getMethodName();
        String caseId = parseCaseId(methodName);
        String module = parseModule(result.getTestClass().getName());

        logger.info("Test Case PASSED: " + methodName + " in " + durationSec + "s");
        ExtentManager.getTest().pass("Test Passed Successfully.");
        ExtentManager.removeTest();

        ExcelReporter.addRecord(caseId, module, "PASS", durationSec, "Success", "N/A");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long end = System.currentTimeMillis();
        double durationSec = (end - startTime.get()) / 1000.0;
        String methodName = result.getMethod().getMethodName();
        String caseId = parseCaseId(methodName);
        String module = parseModule(result.getTestClass().getName());
        String errorMsg = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown Failure";
        
        logger.error("Test Case FAILED: " + methodName + " - Error: " + errorMsg);
        ExtentManager.getTest().fail(result.getThrowable());

        // Capture Screenshot
        try {
            AndroidDriver driver = DriverManager.getDriver();
            String screenshotPath = ScreenshotUtil.captureScreenshot(driver, methodName);
            if (screenshotPath != null) {
                // Attach to extent report
                ExtentManager.getTest().fail("Failure Screenshot", 
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot during test failure", e);
        }

        ExtentManager.removeTest();
        
        String defectId = "DEF-" + caseId;
        ExcelReporter.addRecord(caseId, module, "FAIL", durationSec, errorMsg, defectId);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        long end = System.currentTimeMillis();
        double durationSec = startTime.get() != null ? (end - startTime.get()) / 1000.0 : 0.0;
        String methodName = result.getMethod().getMethodName();
        String caseId = parseCaseId(methodName);
        String module = parseModule(result.getTestClass().getName());
        String skipMsg = result.getThrowable() != null ? result.getThrowable().getMessage() : "Skipped";

        logger.warn("Test Case SKIPPED: " + methodName);
        ExtentManager.getTest().skip(skipMsg);
        ExtentManager.removeTest();

        ExcelReporter.addRecord(caseId, module, "SKIP", durationSec, skipMsg, "N/A");
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test Suite Execution Finished: " + context.getName());
        
        // Flush Extent Reports
        ExtentManager.getInstance().flush();

        // Retrieve properties
        String excelPath = DriverManager.getProperty("excel.report.path");
        String pdfPath = DriverManager.getProperty("pdf.report.path");
        String deploymentTxtPath = DriverManager.getProperty("defect.report.path");
        String deploymentJsonPath = DriverManager.getProperty("deployment.report.path");

        // Generate Reports
        ExcelReporter.generateExcelReport(excelPath);
        PdfReporter.generatePdfReport(pdfPath, ExcelReporter.getRecords());
        DeploymentReporter.generateReadinessReport(deploymentTxtPath, deploymentJsonPath, ExcelReporter.getRecords());
        
        logger.info("All reports flushed and saved.");
    }

    private String parseCaseId(String methodName) {
        // Method name structure: tc001_googleLoginSuccess -> TC001
        if (methodName.startsWith("tc") && methodName.length() >= 5) {
            try {
                return methodName.substring(0, 5).toUpperCase();
            } catch (Exception e) {
                return "TC_UNKNOWN";
            }
        }
        return "TC_UNKNOWN";
    }

    private String parseModule(String className) {
        if (className.contains("AuthTests")) return "Authentication";
        if (className.contains("DashboardTests")) return "Dashboard";
        if (className.contains("ProfileScanTests")) return "Profile Detection";
        if (className.contains("HistoryTests")) return "History";
        if (className.contains("ReportsTests")) return "Reports";
        if (className.contains("SettingsTests")) return "Settings";
        if (className.contains("UiUxTests")) return "UI/UX";
        if (className.contains("FunctionalTests")) return "Functional";
        if (className.contains("ValidationTests")) return "Validation";
        return "General";
    }
}
