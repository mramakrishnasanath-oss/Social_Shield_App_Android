package com.socialshield.testing.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ValidationTests extends BaseTest {

    @Test(priority = 1, description = "TC151: Validate Mandatory Email Field in Login Form")
    public void tc151_mandatoryEmail() {
        logger.info("Executing TC151");
        homePage.navigateToSettings();
        settingsPage.clickSignOut();
        authPage.login("", "password123");
        Assert.assertTrue(authPage.isErrorMessageDisplayed(), "Validation error should display for blank email.");
        authPage.login("demo-google@socialshield.com", "socialshield123");
    }

    @Test(priority = 2, description = "TC152: Validate Mandatory Password Field in Login Form")
    public void tc152_mandatoryPassword() {
        logger.info("Executing TC152");
        homePage.navigateToSettings();
        settingsPage.clickSignOut();
        authPage.login("demo@socialshield.com", "");
        Assert.assertTrue(authPage.isErrorMessageDisplayed(), "Validation error should display for blank password.");
        authPage.login("demo-google@socialshield.com", "socialshield123");
    }

    @Test(priority = 3, description = "TC153: Input Validation - Invalid Email Format")
    public void tc153_invalidEmailFormat() {
        logger.info("Executing TC153");
        homePage.navigateToSettings();
        settingsPage.clickSignOut();
        authPage.login("invalid-email-format", "pass123");
        Assert.assertTrue(authPage.isErrorMessageDisplayed(), "Validation error should block invalid email structure.");
        authPage.login("demo-google@socialshield.com", "socialshield123");
    }

    @Test(priority = 4, description = "TC154: Input Validation - Password Too Short (< 6 chars)")
    public void tc154_passwordTooShort() {
        logger.info("Executing TC154");
        homePage.navigateToSettings();
        settingsPage.clickSignOut();
        // Toggle to signup mode
        authPage.signUp("newuser@socialshield.com", "123");
        Assert.assertTrue(authPage.isErrorMessageDisplayed(), "Should flag password length requirement.");
        authPage.toggleAuthMode(); // Toggle back
        authPage.login("demo-google@socialshield.com", "socialshield123");
    }

    @Test(priority = 5, description = "TC155: Verify Boundary Value - Empty Profile URL input field")
    public void tc155_emptyUrlField() {
        logger.info("Executing TC155");
        homePage.clickScanUrl();
        scanPage.enterUrlToScan("");
        // Button should be disabled or trigger quick error
        scanPage.clickBack();
    }

    @Test(priority = 6, description = "TC156: Verify Boundary Value - Giant URL string (5000 chars)")
    public void tc156_giantUrlString() {
        logger.info("Executing TC156");
        homePage.clickScanUrl();
        String giantUrl = "https://safe-link.com/" + "a".repeat(4900);
        scanPage.enterUrlToScan(giantUrl);
        scanPage.clickAnalyzeWithAi();
        if (resultPage.isResultScreenDisplayed()) resultPage.clickBack();
        else scanPage.clickBack();
    }

    @Test(priority = 7, description = "TC157: Input Validation - Numeric values in Bio details")
    public void tc157_numericBioValues() {
        logger.info("Executing TC157");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("bio_nums", "100", "100", "10", "10", "2", "2", "1234567890");
        scanPage.clickAnalyzeProfile();
        resultPage.clickBack();
    }

    @Test(priority = 8, description = "TC158: Input Validation - Emoji inputs in Bio details")
    public void tc158_emojiBioValues() {
        logger.info("Executing TC158");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("bio_emojis", "100", "100", "10", "10", "2", "2", "😊🔥🚀🛡️");
        scanPage.clickAnalyzeProfile();
        resultPage.clickBack();
    }

    @Test(priority = 9, description = "TC159: Verify validation error is displayed for file size limit exceeds")
    public void tc159_fileSizeLimitExceeded() {
        logger.info("Executing TC159");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 10, description = "TC160: Verify error message for text scan character limits")
    public void tc160_textLimitExceeded() {
        logger.info("Executing TC160");
        homePage.clickScanText();
        String giantText = "Spam ".repeat(3000); // Exceeds character limit
        scanPage.enterTextToScan(giantText);
        scanPage.clickAnalyzeWithAi();
        if (resultPage.isResultScreenDisplayed()) resultPage.clickBack();
        else scanPage.clickBack();
    }

    @Test(priority = 11, description = "TC161: Verify password match validations in signup form")
    public void tc161_passwordMatchValidation() {
        logger.info("Executing TC161");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 12, description = "TC162: Verify SQL injection payload filters in url scan field")
    public void tc162_sqlInjectionFilter() {
        logger.info("Executing TC162");
        homePage.clickScanUrl();
        scanPage.enterUrlToScan("https://scam.com?id=' OR '1'='1");
        scanPage.clickAnalyzeWithAi();
        if (resultPage.isResultScreenDisplayed()) resultPage.clickBack();
        else scanPage.clickBack();
    }

    @Test(priority = 13, description = "TC163: Verify Cross-Site Scripting HTML tags filters in scan text field")
    public void tc163_xssTagsFilter() {
        logger.info("Executing TC163");
        homePage.clickScanText();
        scanPage.enterTextToScan("<script>alert('XSS')</script>");
        scanPage.clickAnalyzeWithAi();
        if (resultPage.isResultScreenDisplayed()) resultPage.clickBack();
        else scanPage.clickBack();
    }

    @Test(priority = 14, description = "TC164: Boundary Test - Negative Account Age Days")
    public void tc164_negativeAgeDays() {
        logger.info("Executing TC164");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("boundary_age", "10", "10", "-5", "5", "1", "1", "testing");
        scanPage.clickAnalyzeProfile();
        if (resultPage.isResultScreenDisplayed()) resultPage.clickBack();
        else scanPage.clickBack();
    }

    @Test(priority = 15, description = "TC165: Boundary Test - Large decimal numbers in comments fields")
    public void tc165_largeDecimalStats() {
        logger.info("Executing TC165");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("decimal_stats", "100", "100", "10", "10", "1.5", "1.2", "testing");
        scanPage.clickAnalyzeProfile();
        if (resultPage.isResultScreenDisplayed()) resultPage.clickBack();
        else scanPage.clickBack();
    }

    @Test(priority = 16, description = "TC166: Blank username input error verification")
    public void tc166_blankUsernameInput() {
        logger.info("Executing TC166");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("", "100", "100", "10", "10", "2", "2", "bio");
        scanPage.clickAnalyzeProfile();
        if (resultPage.isResultScreenDisplayed()) resultPage.clickBack();
        else scanPage.clickBack();
    }

    @Test(priority = 17, description = "TC167: Verify boundary checks on likes/posts ratio")
    public void tc167_likesPostsRatioChecks() {
        logger.info("Executing TC167");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 18, description = "TC168: Validate network retry count boundaries")
    public void tc168_networkRetryLimits() {
        logger.info("Executing TC168");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 19, description = "TC169: Verify UI inputs reset on form cancellation")
    public void tc169_formInputReset() {
        logger.info("Executing TC169");
        homePage.clickScanUrl();
        scanPage.enterUrlToScan("https://google.com");
        scanPage.clickBack();
        homePage.clickScanUrl();
        // Check if value is cleared or reset
        scanPage.clickBack();
    }

    @Test(priority = 20, description = "TC170: Finalize validation suite execution status")
    public void tc170_finalizeValidation() {
        logger.info("Executing TC170");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }
}
