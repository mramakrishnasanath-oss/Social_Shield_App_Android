package com.socialshield.testing.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class HistoryPage extends BasePage {

    // History Locators
    private final By historyTitle = AppiumBy.androidUIAutomator("new UiSelector().text(\"Scan History\")");
    private final By searchField = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Search history\")");
    private final By filterAllChip = AppiumBy.androidUIAutomator("new UiSelector().text(\"ALL\")");
    private final By filterImageChip = AppiumBy.androidUIAutomator("new UiSelector().text(\"IMAGE\")");
    private final By filterTextChip = AppiumBy.androidUIAutomator("new UiSelector().text(\"TEXT\")");
    private final By noScansText = AppiumBy.androidUIAutomator("new UiSelector().text(\"No scans found\")");
    
    // History list item (usually displays the media type as header and has a delete action)
    private final By firstHistoryItem = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"IMAGE\")"); // Click item
    private final By deleteButton = AppiumBy.androidUIAutomator("new UiSelector().descriptionContains(\"Delete\")"); // Or text if applicable
    private final By backButton = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(0)");

    public HistoryPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isHistoryScreenDisplayed() {
        return isElementDisplayed(historyTitle);
    }

    public void searchHistory(String query) {
        logger.info("Searching history for: " + query);
        sendKeys(searchField, query);
    }

    public void selectFilterAll() { click(filterAllChip); }
    public void selectFilterImage() { click(filterImageChip); }
    public void selectFilterText() { click(filterTextChip); }

    public boolean hasScans() {
        return !isElementDisplayedQuickly(noScansText);
    }

    public void clickFirstHistoryItem() {
        if (hasScans()) {
            click(firstHistoryItem);
        }
    }

    public void deleteFirstHistoryItem() {
        if (hasScans() && isElementDisplayed(deleteButton)) {
            click(deleteButton);
            logger.info("Deleted first history item.");
        }
    }

    public void clickBack() {
        clickBackButton();
    }
}
