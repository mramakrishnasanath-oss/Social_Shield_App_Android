package com.socialshield.testing.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class ScanPage extends BasePage {

    // Text & URL inputs
    private final By pasteTextInput = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Paste suspicious\")");
    private final By urlInput = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"https://\")");
    
    // Analyze Buttons
    private final By analyzeWithAiButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Analyze with AI\")");
    private final By analyzeProfileButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Analyze Profile\")");

    // Profile Input Fields
    private final By usernameField = AppiumBy.androidUIAutomator("new UiSelector().text(\"Username\")");
    private final By followersField = AppiumBy.androidUIAutomator("new UiSelector().text(\"Followers\")");
    private final By followingField = AppiumBy.androidUIAutomator("new UiSelector().text(\"Following\")");
    private final By accountAgeField = AppiumBy.androidUIAutomator("new UiSelector().text(\"Account Age (days)\")");
    private final By postCountField = AppiumBy.androidUIAutomator("new UiSelector().text(\"Post Count\")");
    private final By avgLikesField = AppiumBy.androidUIAutomator("new UiSelector().text(\"Average Likes per Post\")");
    private final By avgCommentsField = AppiumBy.androidUIAutomator("new UiSelector().text(\"Average Comments per Post\")");
    private final By bioField = AppiumBy.androidUIAutomator("new UiSelector().text(\"Bio\")");

    // Media Drop Zone
    private final By mediaDropZone = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Upload\")");

    // Common elements
    private final By scanTitle = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Scan\")");
    private final By backButton = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(0)"); // Top-left back button
    private final By scanErrorText = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Error\")");

    public ScanPage(AndroidDriver driver) {
        super(driver);
    }

    public void enterTextToScan(String text) {
        sendKeys(pasteTextInput, text);
    }

    public void enterUrlToScan(String url) {
        sendKeys(urlInput, url);
    }

    public void clickAnalyzeWithAi() {
        click(analyzeWithAiButton);
    }

    public void enterProfileDetails(String username, String followers, String following, 
                                    String age, String posts, String likes, String comments, String bio) {
        logger.info("Entering profile details for: " + username);
        sendKeys(usernameField, username);
        sendKeys(followersField, followers);
        sendKeys(followingField, following);
        
        // Scroll down to access the rest of the profile fields
        scrollDown();
        
        sendKeys(accountAgeField, age);
        sendKeys(postCountField, posts);
        sendKeys(avgLikesField, likes);
        sendKeys(avgCommentsField, comments);
        sendKeys(bioField, bio);
    }

    public void clickAnalyzeProfile() {
        click(analyzeProfileButton);
    }

    public void clickMediaDropZone() {
        click(mediaDropZone);
    }

    public void clickBack() {
        clickBackButton();
    }

    public boolean isScanTitleDisplayed(String typeName) {
        return isElementDisplayed(scanTitle);
    }

    public boolean isErrorDisplayed() {
        return isElementDisplayed(scanErrorText);
    }

    public String getErrorMessage() {
        return getText(scanErrorText);
    }
}
