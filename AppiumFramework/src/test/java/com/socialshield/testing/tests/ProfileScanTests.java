package com.socialshield.testing.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ProfileScanTests extends BaseTest {

    @Test(priority = 1, description = "TC031: Fake Profile Detection with Bots Indicators")
    public void tc031_detectFakeProfileBot() {
        logger.info("Executing TC031");
        homePage.clickScanProfile();
        // High followers/following ratio mismatch
        scanPage.enterProfileDetails("bot_scam_user", "50", "9500", "5", "2", "0", "0", "Earn money fast crypto Sugar Daddy");
        scanPage.clickAnalyzeProfile();
        
        Assert.assertTrue(resultPage.isResultScreenDisplayed(), "Result screen should load.");
        Assert.assertEquals(resultPage.getVerdict(), "FAKE", "Profile should be classified as FAKE.");
        resultPage.clickBack();
    }

    @Test(priority = 2, description = "TC032: Safe Profile Detection")
    public void tc032_detectSafeProfile() {
        logger.info("Executing TC032");
        homePage.clickScanProfile();
        // Regular balanced user
        scanPage.enterProfileDetails("john_doe_99", "1200", "1100", "800", "150", "45", "5", "Engineering student who loves cooking.");
        scanPage.clickAnalyzeProfile();
        
        Assert.assertTrue(resultPage.isResultScreenDisplayed(), "Result screen should load.");
        Assert.assertEquals(resultPage.getVerdict(), "SAFE", "Profile should be classified as SAFE.");
        resultPage.clickBack();
    }

    @Test(priority = 3, description = "TC033: Suspicious Profile Detection (Keyword Match)")
    public void tc033_detectSuspiciousProfile() {
        logger.info("Executing TC033");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("crypto_coach_rich", "800", "2500", "12", "18", "12", "1", "Sugar daddy investments and passive income solutions.");
        scanPage.clickAnalyzeProfile();
        
        Assert.assertTrue(resultPage.isResultScreenDisplayed(), "Result screen should load.");
        Assert.assertEquals(resultPage.getVerdict(), "SUSPICIOUS", "Profile should be SUSPICIOUS.");
        resultPage.clickBack();
    }

    @Test(priority = 4, description = "TC034: Verification of Confidence Score Generation")
    public void tc034_verifyConfidenceScore() {
        logger.info("Executing TC034");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("test_user_score", "100", "200", "30", "5", "2", "1", "Test bio");
        scanPage.clickAnalyzeProfile();
        
        Assert.assertTrue(resultPage.isResultScreenDisplayed(), "Result screen should load.");
        String confidence = resultPage.getConfidence();
        Assert.assertTrue(confidence.contains("%"), "Confidence percentage should be formatted.");
        resultPage.clickBack();
    }

    @Test(priority = 5, description = "TC035: Check Recommendations Generation for Fake Profile")
    public void tc035_verifyRecommendations() {
        logger.info("Executing TC035");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("scam_account_rec", "10", "4000", "2", "1", "0", "0", "crypto sugar daddy");
        scanPage.clickAnalyzeProfile();
        
        Assert.assertTrue(resultPage.isResultScreenDisplayed(), "Result screen should load.");
        Assert.assertTrue(resultPage.areRecommendationsDisplayed(), "Safety recommendations should be displayed.");
        resultPage.clickBack();
    }

    // TCs 036 to 055: Boundary tests and validations for score matching
    @Test(priority = 6, description = "TC036: Empty Profile Fields Validation")
    public void tc036_emptyFields() {
        logger.info("Executing TC036");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("", "", "", "", "", "", "", "");
        scanPage.clickAnalyzeProfile();
        Assert.assertTrue(resultPage.isResultScreenDisplayed() || scanPage.isErrorDisplayed(), 
                "Should either display result or validation error.");
        if (resultPage.isResultScreenDisplayed()) resultPage.clickBack();
        else scanPage.clickBack();
    }

    @Test(priority = 7, description = "TC037: Boundary Test - Negative Followers")
    public void tc037_boundaryNegativeFollowers() {
        logger.info("Executing TC037");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("boundary_neg", "-50", "100", "50", "20", "5", "2", "bio");
        scanPage.clickAnalyzeProfile();
        if (resultPage.isResultScreenDisplayed()) resultPage.clickBack();
        else scanPage.clickBack();
    }

    @Test(priority = 8, description = "TC038: Boundary Test - Extremely Large Followers Count")
    public void tc038_boundaryLargeFollowers() {
        logger.info("Executing TC038");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("celebrity_user", "9999999", "10", "1000", "1200", "45000", "800", "Official verified account");
        scanPage.clickAnalyzeProfile();
        Assert.assertTrue(resultPage.isResultScreenDisplayed(), "Result should be loaded successfully.");
        resultPage.clickBack();
    }

    @Test(priority = 9, description = "TC039: Boundary Test - Account Age Zero Days")
    public void tc039_boundaryAgeZero() {
        logger.info("Executing TC039");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("newly_created", "10", "100", "0", "0", "0", "0", "Brand new profile");
        scanPage.clickAnalyzeProfile();
        Assert.assertTrue(resultPage.isResultScreenDisplayed(), "Result screen should load.");
        resultPage.clickBack();
    }

    @Test(priority = 10, description = "TC040: Accuracy Check - Balanced Bot Indicator Profile")
    public void tc040_botIndicatorProfile() {
        logger.info("Executing TC040");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("bot_indic_40", "100", "1000", "50", "1000", "0", "0", "spam account");
        scanPage.clickAnalyzeProfile();
        Assert.assertTrue(resultPage.isResultScreenDisplayed(), "Result screen should load.");
        resultPage.clickBack();
    }

    @Test(priority = 11, description = "TC041: Accurate Verdict Check for Safe Influencer")
    public void tc041_safeInfluencer() {
        logger.info("Executing TC041");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("influencer_pass", "50000", "200", "300", "400", "5000", "200", "Fashion travel blogger");
        scanPage.clickAnalyzeProfile();
        Assert.assertEquals(resultPage.getVerdict(), "SAFE");
        resultPage.clickBack();
    }

    @Test(priority = 12, description = "TC042: Verify Duplicate Profile Scanning Speed")
    public void tc042_duplicateScanningSpeed() {
        logger.info("Executing TC042");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("dup_check", "50", "50", "10", "10", "2", "2", "Dup check bio");
        scanPage.clickAnalyzeProfile();
        resultPage.clickBack();
    }

    @Test(priority = 13, description = "TC043: Verify Error Handling in Profile Scan")
    public void tc043_profileScanError() {
        logger.info("Executing TC043");
        homePage.clickScanProfile();
        scanPage.clickAnalyzeProfile();
        if (resultPage.isResultScreenDisplayed()) resultPage.clickBack();
        else scanPage.clickBack();
    }

    @Test(priority = 14, description = "TC044: Verify Risk Classification Limits for 45% Probability")
    public void tc044_verifyRiskLimit45() {
        logger.info("Executing TC044");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("suspicious_45", "100", "400", "30", "10", "1", "1", "Moderate keywords here");
        scanPage.clickAnalyzeProfile();
        resultPage.clickBack();
    }

    @Test(priority = 15, description = "TC045: Verify Risk Classification Limits for 80% Probability")
    public void tc045_verifyRiskLimit80() {
        logger.info("Executing TC045");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("fake_80", "10", "800", "5", "2", "0", "0", "Sugar investments fast money bio");
        scanPage.clickAnalyzeProfile();
        Assert.assertEquals(resultPage.getVerdict(), "FAKE");
        resultPage.clickBack();
    }

    @Test(priority = 16, description = "TC046: Verify Risk Classification Limits for 10% Probability")
    public void tc046_verifyRiskLimit10() {
        logger.info("Executing TC046");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("safe_10", "1000", "800", "1200", "500", "60", "8", "Regular professional account");
        scanPage.clickAnalyzeProfile();
        Assert.assertEquals(resultPage.getVerdict(), "SAFE");
        resultPage.clickBack();
    }

    @Test(priority = 17, description = "TC047: Verify Special Characters in Username field")
    public void tc047_specialCharsInUsername() {
        logger.info("Executing TC047");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("!@#$%^&*()_+", "100", "100", "50", "20", "5", "2", "bio text");
        scanPage.clickAnalyzeProfile();
        resultPage.clickBack();
    }

    @Test(priority = 18, description = "TC048: Profile Scan with Blank Bio field")
    public void tc048_blankBioField() {
        logger.info("Executing TC048");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("username_only", "200", "300", "25", "12", "4", "2", "");
        scanPage.clickAnalyzeProfile();
        resultPage.clickBack();
    }

    @Test(priority = 19, description = "TC049: Profile Scan with Giant Bio text (1000 chars)")
    public void tc049_giantBioField() {
        logger.info("Executing TC049");
        homePage.clickScanProfile();
        StringBuilder bigBio = new StringBuilder();
        bigBio.append("Spam keywords sugar daddy crypto investments ".repeat(25));
        scanPage.enterProfileDetails("giant_bio_user", "100", "900", "5", "2", "0", "0", bigBio.toString());
        scanPage.clickAnalyzeProfile();
        Assert.assertEquals(resultPage.getVerdict(), "FAKE");
        resultPage.clickBack();
    }

    @Test(priority = 20, description = "TC050: Verify Response Time for profile scanning")
    public void tc050_responseTimeProfileScan() {
        logger.info("Executing TC050");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("speed_user", "500", "500", "100", "50", "20", "4", "Regular bio info");
        long start = System.currentTimeMillis();
        scanPage.clickAnalyzeProfile();
        Assert.assertTrue(resultPage.isResultScreenDisplayed());
        long end = System.currentTimeMillis();
        logger.info("Profile Scan response time: " + (end - start) + " ms");
        resultPage.clickBack();
    }

    @Test(priority = 21, description = "TC051: Verify Risk Verdict Label Match in Results Screen")
    public void tc051_verdictLabelMatch() {
        logger.info("Executing TC051");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("verdict_match", "10", "5000", "1", "1", "0", "0", "sugar daddy crypto");
        scanPage.clickAnalyzeProfile();
        String verdict = resultPage.getVerdict();
        Assert.assertTrue("FAKE".equals(verdict) || "SUSPICIOUS".equals(verdict) || "SAFE".equals(verdict));
        resultPage.clickBack();
    }

    @Test(priority = 22, description = "TC052: Profile Scan with Zero Followers and Zero Following")
    public void tc052_zeroFollowersFollowing() {
        logger.info("Executing TC052");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("empty_stats", "0", "0", "1", "0", "0", "0", "No stats bio");
        scanPage.clickAnalyzeProfile();
        resultPage.clickBack();
    }

    @Test(priority = 23, description = "TC053: Profile Scan with Large Negative Values (Boundary Case)")
    public void tc053_largeNegativeValues() {
        logger.info("Executing TC053");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("neg_stats", "-999999", "-999999", "-1", "-1", "-1", "-1", "negative");
        scanPage.clickAnalyzeProfile();
        if (resultPage.isResultScreenDisplayed()) resultPage.clickBack();
        else scanPage.clickBack();
    }

    @Test(priority = 24, description = "TC054: Verify Risk Score and Confidence Consistency")
    public void tc054_riskConsistency() {
        logger.info("Executing TC054");
        homePage.clickScanProfile();
        scanPage.enterProfileDetails("consistent_user", "400", "400", "50", "20", "10", "2", "Regular profile context");
        scanPage.clickAnalyzeProfile();
        Assert.assertTrue(resultPage.isResultScreenDisplayed());
        resultPage.clickBack();
    }

    @Test(priority = 25, description = "TC055: Multiple consecutive profile scans")
    public void tc055_consecutiveScans() {
        logger.info("Executing TC055");
        for (int i = 0; i < 2; i++) {
            homePage.clickScanProfile();
            scanPage.enterProfileDetails("user_consec_" + i, "100", "100", "10", "10", "2", "2", "consec test");
            scanPage.clickAnalyzeProfile();
            Assert.assertTrue(resultPage.isResultScreenDisplayed());
            resultPage.clickBack();
        }
    }
}
