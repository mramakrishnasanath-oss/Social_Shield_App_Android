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
            options.setGrantPermissions(true);
            
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
        } catch (Exception e) {
            logger.error("Error occurred while initializing Android Driver", e);
            throw new RuntimeException("Driver initialization failed", e);
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
