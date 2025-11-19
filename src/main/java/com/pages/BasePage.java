package com.pages;

import com.driver.DriverManager;
import com.utils.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected static final Logger logger = LogManager.getLogger(BasePage.class);

    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this);
    }

    protected void click(WebElement element) {
        try {
            waitForElementToBeClickable(element);
            element.click();
            logger.info("Clicked on element: " + element);
        } catch (Exception e) {
            logger.error("Failed to click element: " + e.getMessage());
            throw e;
        }
    }
    protected WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }


    protected void sendKeys(WebElement element, String text) {
        try {
            waitForElementToBeVisible(element);
            element.clear();
            element.sendKeys(text);
            logger.info("Entered text '" + text + "' into element: " + element);
        } catch (Exception e) {
            logger.error("Failed to send keys to element: " + e.getMessage());
            throw e;
        }
    }

    protected String getText(WebElement element) {
        try {
            waitForElementToBeVisible(element);
            String text = element.getText();
            logger.info("Retrieved text: " + text);
            return text;
        } catch (Exception e) {
            logger.error("Failed to get text from element: " + e.getMessage());
            throw e;
        }
    }

    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected void waitForElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected void waitForElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected void waitForElementsToBeVisible(List<WebElement> elements) {
        wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    protected void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(500);
        } catch (Exception e) {
            logger.error("Failed to scroll to element: " + e.getMessage());
        }
    }

    protected void clickWithJS(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            logger.info("Clicked element using JavaScript");
        } catch (Exception e) {
            logger.error("Failed to click with JS: " + e.getMessage());
            throw e;
        }
    }

    protected void waitForPageLoad() {
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }

    protected void acceptCookies() {
        try {
            WebElement cookieButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[class*='cookie'], button[id*='cookie'], button[class*='accept']")));
            click(cookieButton);
            logger.info("Cookies accepted");
        } catch (TimeoutException e) {
            logger.info("No cookie popup found or already accepted");
        }
    }

    protected void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            logger.error("Wait interrupted: " + e.getMessage());
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }
}