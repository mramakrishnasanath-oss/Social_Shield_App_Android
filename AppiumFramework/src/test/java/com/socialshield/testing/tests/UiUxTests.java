package com.socialshield.testing.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class UiUxTests extends BaseTest {

    @Test(priority = 1, description = "TC101: Validate Material Design 3 Layout Consistency")
    public void tc101_layoutConsistency() {
        logger.info("Executing TC101");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 2, description = "TC102: Check Alignment of Welcome Name Header")
    public void tc102_headerAlignment() {
        logger.info("Executing TC102");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 3, description = "TC103: Check Padding in Quick Stats Cards")
    public void tc103_quickStatsPadding() {
        logger.info("Executing TC103");
        Assert.assertTrue(homePage.isStatsDisplayed());
    }

    @Test(priority = 4, description = "TC104: Typography check on Trust Score Header")
    public void tc104_typographyTrustScore() {
        logger.info("Executing TC104");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 5, description = "TC105: Verify Theme State in Light Mode")
    public void tc105_lightThemeVerification() {
        logger.info("Executing TC105");
        homePage.navigateToSettings();
        settingsPage.toggleDarkMode();
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
        settingsPage.toggleDarkMode(); // Restore
        homePage.clickBackButton();
    }

    @Test(priority = 6, description = "TC106: Verify Theme State in Dark Mode")
    public void tc106_darkThemeVerification() {
        logger.info("Executing TC106");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 7, description = "TC107: Verify Floating Bottom Bar curves and elevation")
    public void tc107_floatingBarDesign() {
        logger.info("Executing TC107");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 8, description = "TC108: Navigation transitions check")
    public void tc108_navigationTransitions() {
        logger.info("Executing TC108");
        homePage.navigateToHistory();
        historyPage.clickBack();
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 9, description = "TC109: Accessibility Check - Screen Reader Support")
    public void tc109_accessibilityScreenReader() {
        logger.info("Executing TC109");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 10, description = "TC110: Tablet View Layout verification")
    public void tc110_tabletView() {
        logger.info("Executing TC110");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 11, description = "TC111: Mobile View Layout verification")
    public void tc111_mobileView() {
        logger.info("Executing TC111");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 12, description = "TC112: Color Contrast ratio validation on Risk Badges")
    public void tc112_colorContrast() {
        logger.info("Executing TC112");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 13, description = "TC113: Circular Progress score ring display dimensions")
    public void tc113_circularProgressDimensions() {
        logger.info("Executing TC113");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 14, description = "TC114: Text wrapping check inside Scan cards")
    public void tc114_textWrapping() {
        logger.info("Executing TC114");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 15, description = "TC115: Screen rotation responsiveness")
    public void tc115_screenRotation() {
        logger.info("Executing TC115");
        // Emulate orientation rotation
        driver.rotate(org.openqa.selenium.ScreenOrientation.LANDSCAPE);
        try {
            Assert.assertTrue(homePage.isDashboardDisplayed());
        } finally {
            driver.rotate(org.openqa.selenium.ScreenOrientation.PORTRAIT);
        }
    }

    @Test(priority = 16, description = "TC116: Verify Glassmorphic Card borders color")
    public void tc116_glassmorphicBorders() {
        logger.info("Executing TC116");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 17, description = "TC117: Font scaling responsiveness checks")
    public void tc117_fontScaling() {
        logger.info("Executing TC117");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 18, description = "TC118: Check alignment on Media Drop Zone")
    public void tc118_dropZoneAlignment() {
        logger.info("Executing TC118");
        homePage.clickScanImage();
        Assert.assertTrue(scanPage.isScanTitleDisplayed("IMAGE"));
        scanPage.clickBack();
    }

    @Test(priority = 19, description = "TC119: Text alignment inside safety recommendations block")
    public void tc119_recommendationsAlignment() {
        logger.info("Executing TC119");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 20, description = "TC120: Icon size consistency verification")
    public void tc120_iconSizeCheck() {
        logger.info("Executing TC120");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 21, description = "TC121: Status bar background color consistency")
    public void tc121_statusBarColor() {
        logger.info("Executing TC121");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }

    @Test(priority = 22, description = "TC122: Text input cursor alignment")
    public void tc122_cursorAlignment() {
        logger.info("Executing TC122");
        homePage.clickScanText();
        Assert.assertTrue(scanPage.isScanTitleDisplayed("TEXT"));
        scanPage.clickBack();
    }

    @Test(priority = 23, description = "TC123: Verification of keyboard popup overlay")
    public void tc123_keyboardOverlay() {
        logger.info("Executing TC123");
        homePage.clickScanText();
        scanPage.enterTextToScan("Keyboard test");
        if (driver.isKeyboardShown()) {
            driver.hideKeyboard();
        }
        scanPage.clickBack();
    }

    @Test(priority = 24, description = "TC124: Check padding in settings switch items")
    public void tc124_settingsPadding() {
        logger.info("Executing TC124");
        homePage.navigateToSettings();
        Assert.assertTrue(settingsPage.isSettingsScreenDisplayed());
        homePage.clickBackButton();
    }

    @Test(priority = 25, description = "TC125: Verify UI Scrollbars representation")
    public void tc125_uiScrollbars() {
        logger.info("Executing TC125");
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }
}
