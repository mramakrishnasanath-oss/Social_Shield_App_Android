package com.socialshield.testing.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class ResultPage extends BasePage {

    // Verdict Locators
    private final By fakeVerdict = AppiumBy.androidUIAutomator("new UiSelector().text(\"FAKE\")");
    private final By suspiciousVerdict = AppiumBy.androidUIAutomator("new UiSelector().text(\"SUSPICIOUS\")");
    private final By safeVerdict = AppiumBy.androidUIAutomator("new UiSelector().text(\"SAFE\")");
    private final By verdictTitle = AppiumBy.androidUIAutomator("new UiSelector().text(\"Scan Result\")");
    
    // Confidence & Details
    private final By confidenceValue = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"%\")");
    private final By recommendationsTitle = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Recommendation\")");
    private final By backButton = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(0)"); // Top-left back button
    
    public ResultPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isResultScreenDisplayed() {
        return isElementDisplayed(verdictTitle);
    }

    public String getVerdict() {
        logger.info("Detecting verdict on screen...");
        if (isElementDisplayedQuickly(fakeVerdict)) {
            return "FAKE";
        } else if (isElementDisplayedQuickly(suspiciousVerdict)) {
            return "SUSPICIOUS";
        } else if (isElementDisplayedQuickly(safeVerdict)) {
            return "SAFE";
        }
        return "UNKNOWN";
    }

    public String getConfidence() {
        if (isElementDisplayed(confidenceValue)) {
            return getText(confidenceValue);
        }
        return "0%";
    }

    public boolean areRecommendationsDisplayed() {
        // Scroll down to check recommendations if needed
        scrollDown();
        boolean displayed = isElementDisplayed(recommendationsTitle);
        scrollUp(); // Return to top
        return displayed;
    }

    public void clickBack() {
        click(backButton);
    }
}
