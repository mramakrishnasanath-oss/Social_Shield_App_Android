package com.socialshield.testing.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class SettingsPage extends BasePage {

    // Settings elements
    private final By settingsTitle = AppiumBy.androidUIAutomator("new UiSelector().text(\"Settings\")");
    
    // Toggles/Switches (Find switch inside card with specific text or label)
    private final By darkModeToggle = AppiumBy.androidUIAutomator(
        "new UiSelector().text(\"Dark Mode\").fromParent(new UiSelector().className(\"android.widget.Switch\"))"
    );
    private final By localProcessingToggle = AppiumBy.androidUIAutomator(
        "new UiSelector().text(\"Local Processing\").fromParent(new UiSelector().className(\"android.widget.Switch\"))"
    );
    private final By autoSaveToggle = AppiumBy.androidUIAutomator(
        "new UiSelector().text(\"Auto-Save Scans\").fromParent(new UiSelector().className(\"android.widget.Switch\"))"
    );
    private final By threatAlertsToggle = AppiumBy.androidUIAutomator(
        "new UiSelector().text(\"Threat Alerts\").fromParent(new UiSelector().className(\"android.widget.Switch\"))"
    );

    // Alternative switch selectors using xpath if fromParent has limitations in Compose UI hierarchy
    private final By darkModeSwitchXpath = AppiumBy.xpath("//*[contains(@text,'Dark Mode')]/following-sibling::android.widget.Switch | //*[contains(@text,'Dark Mode')]/../android.widget.Switch");
    private final By localProcessingSwitchXpath = AppiumBy.xpath("//*[contains(@text,'Local Processing')]/following-sibling::android.widget.Switch | //*[contains(@text,'Local Processing')]/../android.widget.Switch");
    private final By autoSaveSwitchXpath = AppiumBy.xpath("//*[contains(@text,'Auto-Save')]/following-sibling::android.widget.Switch | //*[contains(@text,'Auto-Save')]/../android.widget.Switch");
    private final By threatAlertsSwitchXpath = AppiumBy.xpath("//*[contains(@text,'Threat Alerts')]/following-sibling::android.widget.Switch | //*[contains(@text,'Threat Alerts')]/../android.widget.Switch");

    // Account Section
    private final By signOutButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Sign Out\")");
    private final By userDisplayName = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"User\")");

    public SettingsPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isSettingsScreenDisplayed() {
        return isElementDisplayed(settingsTitle);
    }

    public void toggleDarkMode() {
        logger.info("Toggling Dark Mode...");
        try {
            click(darkModeSwitchXpath);
        } catch (Exception e) {
            click(darkModeToggle);
        }
    }

    public void toggleLocalProcessing() {
        logger.info("Toggling Local Processing...");
        try {
            click(localProcessingSwitchXpath);
        } catch (Exception e) {
            click(localProcessingToggle);
        }
    }

    public void toggleAutoSave() {
        logger.info("Toggling Auto-Save...");
        try {
            click(autoSaveSwitchXpath);
        } catch (Exception e) {
            click(autoSaveToggle);
        }
    }

    public void toggleThreatAlerts() {
        logger.info("Toggling Threat Alerts...");
        try {
            click(threatAlertsSwitchXpath);
        } catch (Exception e) {
            click(threatAlertsToggle);
        }
    }

    public void clickSignOut() {
        logger.info("Clicking Sign Out button...");
        scrollDown();
        click(signOutButton);
    }

    public boolean isUserLoggedIn(String email) {
        By emailLabel = AppiumBy.androidUIAutomator("new UiSelector().text(\"" + email + "\")");
        return isElementDisplayedQuickly(emailLabel);
    }
}
