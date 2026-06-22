package com.socialshield.testing.tests;

import com.socialshield.testing.driver.DriverManager;
import com.socialshield.testing.pages.*;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    protected Logger logger = LogManager.getLogger(this.getClass());
    protected AndroidDriver driver;

    protected AuthPage authPage;
    protected HomePage homePage;
    protected ScanPage scanPage;
    protected ResultPage resultPage;
    protected HistoryPage historyPage;
    protected SettingsPage settingsPage;

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        logger.info("Setting up driver for test class: " + this.getClass().getSimpleName());
        try {
            driver = DriverManager.getDriver();
            
            // Initialize Page Objects
            authPage = new AuthPage(driver);
            homePage = new HomePage(driver);
            scanPage = new ScanPage(driver);
            resultPage = new ResultPage(driver);
            historyPage = new HistoryPage(driver);
            settingsPage = new SettingsPage(driver);
            
            logger.info("Page objects initialized successfully.");
            
            // Auto-login for non-Auth tests to avoid test isolation/reset issues in GHA
            if (!this.getClass().getSimpleName().equals("AuthTests")) {
                logger.info("Performing auto-login setup for " + this.getClass().getSimpleName());
                try {
                    authPage.skipOnboarding();
                    if (authPage.isAuthScreenDisplayed()) {
                        authPage.login("demo-google@socialshield.com", "socialshield123");
                    }
                    if (!homePage.isDashboardDisplayed()) {
                        logger.warn("Auto-login did not reach dashboard, trying fallback signup...");
                        authPage.signUp("demo-google@socialshield.com", "socialshield123");
                    }
                } catch (Exception e) {
                    logger.warn("Auto-login failed in setUpClass: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to set up Appium driver", e);
            throw e;
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        logger.info("Tearing down driver for test class: " + this.getClass().getSimpleName());
        DriverManager.quitDriver();
    }
}
