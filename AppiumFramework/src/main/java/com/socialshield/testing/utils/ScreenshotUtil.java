package com.socialshield.testing.utils;

import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtil.class);

    public static String captureScreenshot(AndroidDriver driver, String testName) {
        if (driver == null) {
            logger.error("Driver is null. Cannot capture screenshot.");
            return null;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = testName + "_" + timestamp + ".png";
        
        // Define directory paths
        File failedDir = new File("reports/failedScreenshots");
        if (!failedDir.exists()) {
            failedDir.mkdirs();
        }

        File destination = new File(failedDir, fileName);
        String destinationPath = destination.getAbsolutePath();

        try {
            logger.info("Capturing screenshot for failed test: " + testName);
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, destination);
            logger.info("Screenshot saved to: " + destinationPath);
            return destinationPath;
        } catch (IOException e) {
            logger.error("Failed to save screenshot to disk", e);
            return null;
        }
    }
}
