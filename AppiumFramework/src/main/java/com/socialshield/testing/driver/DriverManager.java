package com.socialshield.testing.driver;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

public class DriverManager {
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<AndroidDriver> driverThreadLocal = new ThreadLocal<>();
    private static Properties properties;

    static {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            logger.info("Configuration properties loaded successfully.");
        } catch (IOException e) {
            logger.error("Failed to load config.properties file.", e);
            throw new RuntimeException("Could not load config.properties", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static synchronized AndroidDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            initializeDriver();
        }
        return driverThreadLocal.get();
    }

    private static void initializeDriver() {
        try {
            logger.info("Initializing Appium Android Driver...");
            UiAutomator2Options options = new UiAutomator2Options();
            options.setPlatformName(getProperty("platform.name"));
            options.setDeviceName(getProperty("device.name"));
            options.setPlatformVersion(getProperty("platform.version"));
            options.setAutomationName(getProperty("automation.name"));
            
            // Set the absolute path of the APK
            String appPath = getProperty("app.path");
            options.setApp(appPath);
            
            options.setAppPackage(getProperty("app.package"));
            options.setAppActivity(getProperty("app.activity"));
            
            // Allow auto grant permissions
            options.setAutoGrantPermissions(true);
            
            // Ensure noReset is false for complete clean state test unless specific session testing is done
            options.setNoReset(false);
            
            // Appium Server URL
            String serverUrl = getProperty("appium.server.url");
            URL url = URI.create(serverUrl).toURL();
            
            AndroidDriver driver = new AndroidDriver(url, options);
            
            // Set implicit wait
            int implicitWait = Integer.parseInt(getProperty("implicit.wait"));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
            
            driverThreadLocal.set(driver);
            logger.info("Appium Android Driver initialized successfully. Session ID: " + driver.getSessionId());
            
            dismissSystemPopups(driver);
        } catch (Exception e) {
            logger.error("Error occurred while initializing Android Driver", e);
            throw new RuntimeException("Driver initialization failed", e);
        }
    }

    private static void dismissSystemPopups(AndroidDriver driver) {
        try {
            logger.info("Checking for any system/compatibility popups at startup...");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            
            // Loop up to 3 times to handle chained popups (e.g. System UI isn't responding followed by Compatibility warning)
            for (int i = 0; i < 3; i++) {
                boolean popupDismissed = false;
                
                // 1. Check for unresponsive dialogs (e.g., "Wait" button)
                java.util.List<org.openqa.selenium.WebElement> waitButtons = driver.findElements(io.appium.java_client.AppiumBy.xpath("//*[@text='Wait']"));
                if (!waitButtons.isEmpty() && waitButtons.get(0).isDisplayed()) {
                    logger.info("Clicking 'Wait' to dismiss unresponsive system dialog.");
                    waitButtons.get(0).click();
                    popupDismissed = true;
                    Thread.sleep(1000);
                }
                
                // 2. Check for Compatibility dialog (Don't Show Again)
                java.util.List<org.openqa.selenium.WebElement> dontShow = driver.findElements(io.appium.java_client.AppiumBy.xpath("//*[@text=\"Don't Show Again\"]"));
                if (!dontShow.isEmpty() && dontShow.get(0).isDisplayed()) {
                    logger.info("Clicking 'Don't Show Again' to dismiss compatibility dialog permanently.");
                    dontShow.get(0).click();
                    popupDismissed = true;
                    Thread.sleep(1000);
                }
                
                // 3. Check for Compatibility dialog (OK)
                java.util.List<org.openqa.selenium.WebElement> okButtons = driver.findElements(io.appium.java_client.AppiumBy.xpath("//*[@text='OK' or @resource-id='android:id/button1']"));
                if (!okButtons.isEmpty() && okButtons.get(0).isDisplayed()) {
                    logger.info("Clicking 'OK' to dismiss compatibility dialog.");
                    okButtons.get(0).click();
                    popupDismissed = true;
                    Thread.sleep(1000);
                }
                
                if (!popupDismissed) {
                    break; // No popups found in this iteration
                }
            }
            
            // Restore original timeout from properties
            int implicitWait = Integer.parseInt(getProperty("implicit.wait"));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        } catch (Exception e) {
            logger.warn("No system dialog found or error while trying to dismiss it: " + e.getMessage());
        }
    }

    public static synchronized void quitDriver() {
        AndroidDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                logger.info("Quitting Appium Driver Session: " + driver.getSessionId());
                driver.quit();
            } catch (Exception e) {
                logger.error("Error occurred while quitting Appium Driver", e);
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}
