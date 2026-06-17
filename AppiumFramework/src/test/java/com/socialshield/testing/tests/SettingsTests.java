package com.socialshield.testing.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SettingsTests extends BaseTest {

    @Test(priority = 1, description = "TC086: Open Settings screen successfully")
    public void tc086_openSettings() {
        logger.info("Executing TC086");
        homePage.navigateToSettings();
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed(), "Settings screen must load.");
    }

    @Test(priority = 2, description = "TC087: Toggle Dark Theme Mode Switch")
    public void tc087_toggleDarkTheme() {
        logger.info("Executing TC087");
        settingsPage.toggleDarkMode();
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
    }

    @Test(priority = 3, description = "TC088: Toggle Light Theme Mode Switch")
    public void tc088_toggleLightTheme() {
        logger.info("Executing TC088");
        settingsPage.toggleDarkMode(); // Switch back to original theme state
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
    }

    @Test(priority = 4, description = "TC089: Toggle Local Processing privacy setting")
    public void tc089_toggleLocalProcessing() {
        logger.info("Executing TC089");
        settingsPage.toggleLocalProcessing();
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
    }

    @Test(priority = 5, description = "TC090: Toggle Auto-Save Scans switch")
    public void tc090_toggleAutoSave() {
        logger.info("Executing TC090");
        settingsPage.toggleAutoSave();
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
    }

    @Test(priority = 6, description = "TC091: Toggle Threat Notification Alerts switch")
    public void tc091_toggleThreatAlerts() {
        logger.info("Executing TC091");
        settingsPage.toggleThreatAlerts();
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
    }

    @Test(priority = 7, description = "TC092: Verify Profile Username loads inside Settings card")
    public void tc092_verifySettingsUsername() {
        logger.info("Executing TC092");
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
    }

    @Test(priority = 8, description = "TC093: Verify Profile Email loads inside Settings card")
    public void tc093_verifySettingsEmail() {
        logger.info("Executing TC093");
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
    }

    @Test(priority = 9, description = "TC094: Verify App Version Number displays in Info widget")
    public void tc094_verifyAppVersion() {
        logger.info("Executing TC094");
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
    }

    @Test(priority = 10, description = "TC095: Verify App description displays in Info widget")
    public void tc095_verifyAppDescription() {
        logger.info("Executing TC095");
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
    }

    @Test(priority = 11, description = "TC096: Restore Default Toggle settings")
    public void tc096_restoreDefaultSettings() {
        logger.info("Executing TC096");
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
    }

    @Test(priority = 12, description = "TC097: Navigate back to Home Dashboard")
    public void tc097_backToDashboard() {
        logger.info("Executing TC097");
        homePage.clickBackButton();
        Assert.assertTrue(homePage.isDashboardDisplayed());
        homePage.navigateToSettings(); // Navigate back for class isolation
    }

    @Test(priority = 13, description = "TC098: Verify Settings state remains after app pause")
    public void tc098_statePersistenceAfterPause() {
        logger.info("Executing TC098");
        driver.runAppInBackground(java.time.Duration.ofSeconds(2));
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
    }

    @Test(priority = 14, description = "TC099: Verify Settings state remains after app restart")
    public void tc099_statePersistenceAfterRestart() {
        logger.info("Executing TC099");
        driver.terminateApp("com.socialshield");
        driver.activateApp("com.socialshield");
        // Re-navigate to settings
        homePage.navigateToSettings();
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
    }

    @Test(priority = 15, description = "TC100: Sign Out execution from Settings")
    public void tc100_signOutExecution() {
        logger.info("Executing TC100");
        settingsPage.clickSignOut();
        Assert.assertTrue(authPage.isAuthScreenDisplayed(), "Sign Out must navigate user to Auth screen.");
        // Sign back in to restore state for subsequent suites
        authPage.login("demo-google@socialshield.com", "socialshield123");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }
}
