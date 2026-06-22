package com.socialshield.testing.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class AuthPage extends BasePage {

    // Onboarding Locators
    private final By skipButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Skip\")");
    private final By nextButton = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Next\")");
    private final By getStartedButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Get Started\")");

    // Auth Screen Locators
    private final By emailField = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(0)");
    private final By passwordField = AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(1)");
    private final By signInButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Sign In\")");
    private final By createAccountButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Create Account\")");
    private final By googleSignInButton = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Google\")");
    private final By toggleAuthModeLink = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"account?\")");
    private final By errorMessageText = AppiumBy.androidUIAutomator("new UiSelector().textContains(\"failed\")"); // Or general error display text

    public AuthPage(AndroidDriver driver) {
        super(driver);
    }

    public void skipOnboarding() {
        logger.info("Attempting to skip onboarding...");
        if (isElementDisplayedQuickly(skipButton)) {
            click(skipButton);
            logger.info("Skipped onboarding via Skip button.");
        } else if (isElementDisplayedQuickly(getStartedButton)) {
            click(getStartedButton);
            logger.info("Onboarding already at last page, clicked Get Started.");
        } else {
            logger.info("Onboarding skip button not found, assuming already on auth screen.");
        }
    }

    public void completeOnboarding() {
        logger.info("Completing onboarding step by step...");
        for (int i = 0; i < 4; i++) {
            if (isElementDisplayedQuickly(nextButton)) {
                click(nextButton);
            } else if (isElementDisplayedQuickly(getStartedButton)) {
                click(getStartedButton);
                break;
            }
        }
    }

    public void login(String email, String password) {
        logger.info("Performing login with email: " + email);
        hideKeyboard();
        if (isElementDisplayed(toggleAuthModeLink)) {
            String linkText = getText(toggleAuthModeLink);
            if (linkText.contains("Sign In")) {
                click(toggleAuthModeLink);
            }
        }
        sendKeys(emailField, email);
        sendKeys(passwordField, password);
        hideKeyboard();
        click(signInButton);
    }

    public void signUp(String email, String password) {
        logger.info("Performing signup with email: " + email);
        hideKeyboard();
        if (isElementDisplayed(toggleAuthModeLink)) {
            String linkText = getText(toggleAuthModeLink);
            if (linkText.contains("Sign Up")) {
                click(toggleAuthModeLink);
            }
        }
        sendKeys(emailField, email);
        sendKeys(passwordField, password);
        hideKeyboard();
        click(createAccountButton);
    }

    public void clickGoogleSignIn() {
        logger.info("Clicking Google sign in...");
        click(googleSignInButton);
    }

    public void toggleAuthMode() {
        logger.info("Toggling authentication mode...");
        click(toggleAuthModeLink);
    }

    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessageText);
    }

    public String getErrorMessage() {
        return getText(errorMessageText);
    }

    public boolean isAuthScreenDisplayed() {
        return isElementDisplayed(emailField) || isElementDisplayed(signInButton) || isElementDisplayed(googleSignInButton);
    }

    public void handleGoogleAccountChooser() {
        try {
            logger.info("Waiting for Google account chooser dialog to appear...");
            org.openqa.selenium.support.ui.WebDriverWait explicitWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15));
            
            By accountSelector = AppiumBy.xpath("//*[contains(@text, '@') or contains(@text, 'RAMA KRISHNA') or @resource-id='com.google.android.gms:id/account_name' or @resource-id='com.google.android.gms:id/account_display_name']");
            
            org.openqa.selenium.WebElement accountElement = explicitWait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(accountSelector)
            );
            
            logger.info("Found Google account: " + accountElement.getText() + ", clicking it.");
            accountElement.click();
        } catch (Exception e) {
            logger.warn("Google account chooser dialog did not appear or was not clickable: " + e.getMessage());
            try {
                logger.info("Dismissing external Google Sign-In overlay via system back key...");
                driver.navigate().back();
            } catch (Exception ex) {
                logger.warn("Failed to press back key: " + ex.getMessage());
            }
        }
    }
}
