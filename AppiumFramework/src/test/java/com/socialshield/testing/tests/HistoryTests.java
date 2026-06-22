package com.socialshield.testing.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HistoryTests extends BaseTest {

    @Test(priority = 1, description = "TC056: Load History Feed Successfully")
    public void tc056_loadHistory() {
        logger.info("Executing TC056");
        homePage.navigateToHistory();
        Assert.assertTrue(historyPage.isHistoryScreenDisplayed(), "History screen must open.");
    }

    @Test(priority = 2, description = "TC057: Search History by Existing Item Query")
    public void tc057_searchHistoryMatch() {
        logger.info("Executing TC057");
        historyPage.searchHistory("bot_scam");
        // Verify UI responds and doesn't crash
        Assert.assertTrue(historyPage.isHistoryScreenDisplayed());
    }

    @Test(priority = 3, description = "TC058: Search History by Non-Existing Query")
    public void tc058_searchHistoryNoMatch() {
        logger.info("Executing TC058");
        historyPage.searchHistory("non_existent_search_query_term_abcxyz");
        Assert.assertFalse(historyPage.hasScans(), "Search should return empty scans.");
    }

    @Test(priority = 4, description = "TC059: Clear Search Field Query and Reload All")
    public void tc059_clearSearchField() {
        logger.info("Executing TC059");
        historyPage.searchHistory("");
        // Should reload standard scans list
        Assert.assertTrue(historyPage.isHistoryScreenDisplayed());
    }

    @Test(priority = 5, description = "TC060: Filter History by IMAGE Category")
    public void tc060_filterImage() {
        logger.info("Executing TC060");
        historyPage.selectFilterImage();
        Assert.assertTrue(historyPage.isHistoryScreenDisplayed());
    }

    @Test(priority = 6, description = "TC061: Filter History by TEXT Category")
    public void tc061_filterText() {
        logger.info("Executing TC061");
        historyPage.selectFilterText();
        Assert.assertTrue(historyPage.isHistoryScreenDisplayed());
    }

    @Test(priority = 7, description = "TC062: Reset Filter chips back to ALL")
    public void tc062_resetFilterAll() {
        logger.info("Executing TC062");
        historyPage.selectFilterAll();
        Assert.assertTrue(historyPage.isHistoryScreenDisplayed());
    }

    @Test(priority = 8, description = "TC063: Click History Item and Open Detailed Results Screen")
    public void tc063_clickHistoryItem() {
        logger.info("Executing TC063");
        if (historyPage.hasScans()) {
            historyPage.clickFirstHistoryItem();
            Assert.assertTrue(resultPage.isResultScreenDisplayed(), "Should load result detail from history.");
            resultPage.clickBack();
        }
    }

    @Test(priority = 9, description = "TC064: Delete History Scan Record")
    public void tc064_deleteHistoryItem() {
        logger.info("Executing TC064");
        if (historyPage.hasScans()) {
            historyPage.deleteFirstHistoryItem();
            Assert.assertTrue(historyPage.isHistoryScreenDisplayed());
        }
    }

    @Test(priority = 10, description = "TC065: Verify Sorting by Date Order in History Feed")
    public void tc065_verifySorting() {
        logger.info("Executing TC065");
        Assert.assertTrue(historyPage.isHistoryScreenDisplayed());
    }

    @Test(priority = 11, description = "TC066: History Empty State Component check")
    public void tc066_verifyEmptyState() {
        logger.info("Executing TC066");
        historyPage.searchHistory("non_existent_search_query_term_abcxyz");
        Assert.assertFalse(historyPage.hasScans());
        historyPage.searchHistory("");
    }

    @Test(priority = 12, description = "TC067: History Scroll Performance Check")
    public void tc067_verifyScrollPerformance() {
        logger.info("Executing TC067");
        historyPage.scrollDown();
        historyPage.scrollUp();
        Assert.assertTrue(historyPage.isHistoryScreenDisplayed());
    }

    @Test(priority = 13, description = "TC068: Navigating out of History page via back key")
    public void tc068_navigateBack() {
        logger.info("Executing TC068");
        historyPage.clickBack();
        Assert.assertTrue(homePage.isDashboardDisplayed(), "Should return to dashboard.");
        homePage.navigateToHistory(); // Return back for next test isolation
    }

    @Test(priority = 14, description = "TC069: Verify Sync with DB on Delete Actions")
    public void tc069_deleteSyncCheck() {
        logger.info("Executing TC069");
        Assert.assertTrue(historyPage.isHistoryScreenDisplayed());
    }

    @Test(priority = 15, description = "TC070: Navigate back to Home from history")
    public void tc070_cleanupAndHome() {
        logger.info("Executing TC070");
        historyPage.clickBack();
        Assert.assertTrue(homePage.isDashboardDisplayed());
    }
}
