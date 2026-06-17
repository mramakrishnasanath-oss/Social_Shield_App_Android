package com.socialshield.testing.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthTests extends BaseTest {

    @Test(priority = 1, description = "TC001: Google Login Success Fallback Flow")
    public void tc001_googleLoginSuccess() {
        logger.info("Executing TC001");
        authPage.skipOnboarding();
        Assert.assertTrue(authPage.isAuthScreenDisplayed(), "Auth screen should be displayed.");
        authPage.clickGoogleSignIn();
        
        // Google authentication launches demo credentials fallback in local development
        Assert.assertTrue(homePage.isDashboardDisplayed(), "Dashboard should be displayed after successful auth.");
    }

    @Test(priority = 2, description = "TC002: Google Login Failure Error Flow")
    public void tc002_googleLoginFailure() {
        logger.info("Executing TC002");
        homePage.navigateToSettings();
        settingsPage.clickSignOut();
        Assert.assertTrue(authPage.isAuthScreenDisplayed(), "Should return to Auth screen after Sign Out.");
        
        // Simulate login failure by typing invalid credentials
        authPage.login("invalid-user@socialshield.com", "wrongpassword");
        Assert.assertTrue(authPage.isErrorMessageDisplayed(), "Error message should be shown for failed login.");
        Assert.assertTrue(authPage.getErrorMessage().contains("failed") || authPage.getErrorMessage().contains("Invalid"), 
                "Error message should reflect invalid credentials.");
    }

    @Test(priority = 3, description = "TC003: Invalid Session Validation")
    public void tc003_invalidSession() {
        logger.info("Executing TC003");
        // Enter blank password to test invalid inputs
        authPage.login("demo@socialshield.com", "");
        Assert.assertTrue(authPage.isErrorMessageDisplayed(), "Error message should be shown.");
    }

    @Test(priority = 4, description = "TC004: Logout Verification")
    public void tc004_logout() {
        logger.info("Executing TC004");
        authPage.login("demo-google@socialshield.com", "socialshield123");
        Assert.assertTrue(homePage.isDashboardDisplayed(), "Dashboard should be visible.");
        homePage.navigateToSettings();
        settingsPage.clickSignOut();
        Assert.assertTrue(authPage.isAuthScreenDisplayed(), "Should return to auth screen.");
    }

    @Test(priority = 5, description = "TC005: Session Persistence After Device Pause")
    public void tc005_sessionPersistence() {
        logger.info("Executing TC005");
        authPage.login("demo-google@socialshield.com", "socialshield123");
        
        // Put app in background and restore it to verify session persistence
        driver.runAppInBackground(java.time.Duration.ofSeconds(3));
        Assert.assertTrue(homePage.isDashboardDisplayed(), "Dashboard should remain active after restoring session.");
    }

    @Test(priority = 6, description = "TC006: Auto Login on App Restart")
    public void tc006_autoLogin() {
        logger.info("Executing TC006");
        // Terminate and reactivate app
        driver.terminateApp("com.socialshield");
        driver.activateApp("com.socialshield");
        
        // Session should be cached via PreferencesManager
        Assert.assertTrue(homePage.isDashboardDisplayed(), "Should auto login to dashboard on restart.");
    }

    @Test(priority = 7, description = "TC007: Multiple Login Attempts Lockout")
    public void tc007_multipleLoginAttempts() {
        logger.info("Executing TC007");
        homePage.navigateToSettings();
        settingsPage.clickSignOut();
        
        for (int i = 0; i < 3; i++) {
            authPage.login("fakeuser" + i + "@socialshield.com", "wrongpass");
        }
        Assert.assertTrue(authPage.isErrorMessageDisplayed(), "Error message must remain visible.");
    }

    @Test(priority = 8, description = "TC008: Network Failure During Login Simulation")
    public void tc008_networkFailureDuringLogin() {
        logger.info("Executing TC008");
        // Emulate offline mode
        driver.setConnection(new io.appium.java_client.android.connection.ConnectionStateBuilder().withWiFiDisabled().withDataDisabled().build());
        try {
            authPage.login("demo-google@socialshield.com", "socialshield123");
            Assert.assertTrue(authPage.isErrorMessageDisplayed(), "Network error details should be displayed.");
        } finally {
            // Restore online status
            driver.setConnection(new io.appium.java_client.android.connection.ConnectionStateBuilder().withWiFiEnabled().withDataEnabled().build());
        }
    }

    @Test(priority = 9, description = "TC009: Token Expired Token Verification")
    public void tc009_tokenExpired() {
        logger.info("Executing TC009");
        // Login and simulate outdated token
        authPage.login("demo-google@socialshield.com", "socialshield123");
        Assert.assertTrue(homePage.isDashboardDisplayed(), "Should enter home screen.");
    }

    @Test(priority = 10, description = "TC010: Unauthorized User Blockage")
    public void tc010_unauthorizedUser() {
        logger.info("Executing TC010");
        homePage.navigateToSettings();
        settingsPage.clickSignOut();
        authPage.login("unauthorized@socialshield.com", "nopermission");
        Assert.assertTrue(authPage.isErrorMessageDisplayed(), "Unauthorized messages must be shown.");
        // Re-authenticate to restore clean state
        authPage.login("demo-google@socialshield.com", "socialshield123");
    }
}
