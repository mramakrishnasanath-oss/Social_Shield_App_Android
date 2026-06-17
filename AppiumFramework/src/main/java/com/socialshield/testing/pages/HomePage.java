package com.socialshield.testing.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class HomePage extends BasePage {

    // Bottom Navigation Tabs
    private final By homeTab = AppiumBy.androidUIAutomator("new UiSelector().text(\"Home\")");
    private final By historyTab = AppiumBy.androidUIAutomator("new UiSelector().text(\"History\")");
    private final By mapTab = AppiumBy.androidUIAutomator("new UiSelector().text(\"Map\")");
    private final By settingsTab = AppiumBy.androidUIAutomator("new UiSelector().text(\"Settings\")");

    // Scan Type Cards
    private final By scanImageCard = AppiumBy.androidUIAutomator("new UiSelector().text(\"Scan Image\")");
    private final By scanVideoCard = AppiumBy.androidUIAutomator("new UiSelector().text(\"Scan Video\")");
    private final By scanAudioCard = AppiumBy.androidUIAutomator("new UiSelector().text(\"Scan Audio\")");
    private final By scanTextCard = AppiumBy.androidUIAutomator("new UiSelector().text(\"Scan Text\")");
    private final By scanUrlCard = AppiumBy.androidUIAutomator("new UiSelector().text(\"Scan URL\")");
    private final By scanProfileCard = AppiumBy.androidUIAutomator("new UiSelector().text(\"Scan Profile\")");

    // Quick Action Cards
    private final By quickHistoryCard = AppiumBy.androidUIAutomator("new UiSelector().text(\"Scan History\")");
    private final By quickFraudMapCard = AppiumBy.androidUIAutomator("new UiSelector().text(\"Fraud Map\")");

    // Dashboard Info & Stats
    private final By trustScoreText = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Score\")");
    private final By trustScoreDesc = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"protection\")");
    private final By totalScansStat = AppiumBy.androidUIAutomator("new UiSelector().text(\"Total Scans\")");
    private final By safeProfilesStat = AppiumBy.androidUIAutomator("new UiSelector().text(\"Safe Profiles\")");
    private final By suspiciousStat = AppiumBy.androidUIAutomator("new UiSelector().text(\"Suspicious\")");
    private final By fakeDetectedStat = AppiumBy.androidUIAutomator("new UiSelector().text(\"Fake Detected\")");

    // Live Feed Activity
    private final By liveActivityTitle = AppiumBy.androidUIAutomator("new UiSelector().text(\"Live Activity Feed\")");
    private final By viewAllButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"View All\")");
    private final By firstRecentItem = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"2026\")"); // Timestamps contain '2026'

    public HomePage(AndroidDriver driver) {
        super(driver);
    }

    public void navigateToHome() { click(homeTab); }
    public void navigateToHistory() { click(historyTab); }
    public void navigateToMap() { click(mapTab); }
    public void navigateToSettings() { click(settingsTab); }

    public void clickScanImage() { click(scanImageCard); }
    public void clickScanVideo() { click(scanVideoCard); }
    public void clickScanAudio() { click(scanAudioCard); }
    public void clickScanText() { click(scanTextCard); }
    public void clickScanUrl() { click(scanUrlCard); }
    public void clickScanProfile() { click(scanProfileCard); }

    public void clickQuickHistory() { click(quickHistoryCard); }
    public void clickQuickFraudMap() { click(quickFraudMapCard); }

    public boolean isDashboardDisplayed() {
        return isElementDisplayed(trustScoreText) || isElementDisplayed(scanProfileCard);
    }

    public String getTrustScoreDescription() {
        if (isElementDisplayed(trustScoreDesc)) {
            return getText(trustScoreDesc);
        }
        return "N/A";
    }

    public boolean isStatsDisplayed() {
        return isElementDisplayed(totalScansStat) &&
               isElementDisplayed(safeProfilesStat) &&
               isElementDisplayed(suspiciousStat) &&
               isElementDisplayed(fakeDetectedStat);
    }

    public void clickFirstRecentScan() {
        if (isElementDisplayed(firstRecentItem)) {
            click(firstRecentItem);
        }
    }

    public boolean isLiveActivityFeedDisplayed() {
        return isElementDisplayed(liveActivityTitle);
    }
}
