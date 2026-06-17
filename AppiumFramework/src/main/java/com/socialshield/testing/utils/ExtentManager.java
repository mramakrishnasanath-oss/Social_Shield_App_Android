package com.socialshield.testing.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ExtentManager {
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    private static void createInstance() {
        String path = "reports/extent-reports/index.html";
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(path);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("SocialShield Test Automation Report");
        sparkReporter.config().setReportName("End-to-End Test Execution Status");
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Application", "SocialShield Android App");
        extent.setSystemInfo("OS", "Windows 11");
        extent.setSystemInfo("JDK", "Java 21");
        extent.setSystemInfo("Appium Version", "2.x");
        extent.setSystemInfo("QA Architect", "Antigravity AI");
    }

    public static synchronized ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static synchronized void setTest(ExtentTest test) {
        testThreadLocal.set(test);
    }

    public static synchronized void removeTest() {
        testThreadLocal.remove();
    }
}
