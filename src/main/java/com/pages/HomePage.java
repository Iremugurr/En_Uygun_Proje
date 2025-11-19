package com.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.time.Duration;
import java.util.List;

import static java.lang.Thread.sleep;

public class HomePage extends BasePage {

    @FindBy(css = "div[data-testid='flight-origin-label']")
    public WebElement originInput;


    @FindBy(css = "div[data-testid='flight-destination-label']")
    public WebElement destinationInput;

    @FindBy(css = "input[data-testid='endesign-flight-origin-autosuggestion-input']")
    public WebElement openOriginInput;

    @FindBy(css = "input[data-testid='endesign-flight-destination-autosuggestion-input']")
    public WebElement openDestinationInput;


    @FindBy(xpath = "//div[@data-testid='enuygun-homepage-flight-departureDate-label' and text()='Gidiş Tarihi']")
    public WebElement departureDateInput;


    @FindBy(xpath = "//input[@data-testid='enuygun-homepage-flight-returnDate-datepicker-input']")
    public WebElement returnDateInput;


    @FindBy(css = "button[type='submit']")
    public WebElement searchButton;

    @FindBy(css = "div[data-testid='search-round-trip-text'][name='flightTrip']")
    public WebElement roundTripButton;


    @FindBy(css = "#close-pc-btn-handler")
    public WebElement closeButton;

    @FindBy(id = "onetrust-accept-btn-handler")
    public WebElement acceptButton;

    @FindBy(xpath = "//span[@data-testid='flight-origin-istanbul-highlight-0' and text()='Tüm havalimanları']")
    public WebElement flightOriginIstanbulHighlight;






    public HomePage() {
        super();
    }

    public void navigateToHomePage(String url) {
        driver.get(url);
        logger.info("Navigated to: " + url);
        waitForPageLoad();
        acceptCookies();
    }

    public void selectRoundTrip() {
        click(closeButton);
        click(acceptButton);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        click(roundTripButton);
    }

    public void enterOrigin(String city) {
        click(originInput);
        sendKeys(openOriginInput,"Istanbul");
        waitForSeconds(2);
        click(flightOriginIstanbulHighlight);
        waitForSeconds(1);
        logger.info("Entered origin city: " + city);
    }

    public void enterDestination(String city) {
        click(destinationInput);
        sendKeys(openDestinationInput, "Ankara");
        waitForSeconds(1);
        selectFromDropdown(city);
        logger.info("Entered destination city: " + city);
    }

    private void selectFromDropdown(String city) {
        try {
            WebElement cityOption = wait.until(driver ->
                    driver.findElement(By.xpath("//div[contains(@class, 'suggestion')]//span[contains(text(), '" + city + "')]")));
            click(cityOption);
        } catch (Exception e) {
            logger.warn("Could not select from dropdown: " + e.getMessage());
        }
    }




    public void selectDepartureDate(String date) {
        click(departureDateInput);
        waitForSeconds(1);
        selectDateFromCalendar(date);
    }



    public void selectReturnDate(String date) {
        click(returnDateInput);
        waitForSeconds(1);
        selectDateFromCalendar(date);
    }



    public void selectDateFromCalendar(String date) {
        // date format yyyy-MM-dd
        LocalDate target;
        try {
            target = LocalDate.parse(date);
        } catch (Exception e) {
            logger.error("Invalid date format, expected yyyy-MM-dd: {}", date);
            return;
        }

        String day = String.valueOf(target.getDayOfMonth());
        String ariaLabel = target.format(DateTimeFormatter.ofPattern("d MMMM yyyy EEEE", new Locale("tr")));

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Possible calendar containers (try multiple common selectors)
        By[] calendarContainers = new By[]{
                By.xpath("//div[contains(@class,'calendar') or contains(@class,'datepicker')]"),
                By.xpath("//div[@role='dialog' and contains(@class,'Calendar')]"),
                By.xpath("//div[contains(@class,'headlessui') or contains(@class,'date')]")
        };

        WebElement calendarRoot = null;
        for (By c : calendarContainers) {
            try {
                calendarRoot = shortWait.until(ExpectedConditions.presenceOfElementLocated(c));
                if (calendarRoot != null) break;
            } catch (Exception ignored) {}
        }

        if (calendarRoot == null) {
            // As last resort try to find any element that looks like a calendar grid
            try {
                calendarRoot = shortWait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//table | //div[contains(@class,'calendar-grid')] | //div[contains(@class,'datepicker')]")));
            } catch (Exception e) {
                logger.error("Calendar root not found after opening calendar. Dumping page snippet for debug.");
                dumpPageSnippetForDebug();
                logger.error("Failed to locate calendar container. Aborting date selection.");
                return;
            }
        }

        // Try switching into iframe if calendar is inside an iframe (best-effort)
        try {
            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            for (WebElement f : iframes) {
                try {
                    driver.switchTo().frame(f);
                    // if inside frame there is likely calendar markup, test quickly
                    if (driver.findElements(By.xpath("//*[contains(@class,'calendar') or //table]")).size() > 0) {
                        logger.info("Switched to iframe containing calendar");
                        break;
                    } else {
                        driver.switchTo().defaultContent();
                    }
                } catch (Exception ignored) {
                    driver.switchTo().defaultContent();
                }
            }
        } catch (Exception ignored) {}

        // Locator for next month button (try a few common variants)
        By[] nextButtons = new By[]{
                By.xpath("//button[contains(@aria-label,'Next') or contains(@aria-label,'İleri') or contains(@class,'next') or contains(@data-testid,'next')]"),
                By.xpath("//button[contains(@class,'react-datepicker__navigation--next')]"),
                By.xpath("//button[contains(@aria-label,'ileri') or contains(@title,'İleri')]")
        };

        // First try direct aria-label match (fast path)
        By exactDay = By.xpath("//td[@aria-label='" + ariaLabel + "']");
        try {
            WebElement dayEl = longWait.until(ExpectedConditions.elementToBeClickable(exactDay));
            clickElementRobust(dayEl);
            logger.info("Clicked by exact aria-label: " + ariaLabel);
            driver.switchTo().defaultContent();
            return;
        } catch (TimeoutException ignored) {
            // continue to month navigation
        } catch (StaleElementReferenceException ignored) {}

        // Try moving months forward until we find the aria-label or the day element
        boolean clicked = false;
        int maxMonthTries = 12;
        for (int i = 0; i < maxMonthTries && !clicked; i++) {
            try {
                // Try exact aria-label again
                List<WebElement> candidates = driver.findElements(exactDay);
                if (candidates.size() > 0) {
                    for (WebElement candidate : candidates) {
                        if (candidate.isDisplayed() && candidate.isEnabled()) {
                            clickElementRobust(candidate);
                            logger.info("Clicked exact aria-label after " + i + " month steps");
                            clicked = true;
                            break;
                        }
                    }
                    if (clicked) break;
                }

                // Fallback: find td that contains span with day text and is not disabled
                By fallbackDay = By.xpath("//td[.//span[normalize-space()='" + day + "'] and not(contains(@class,'disabled'))]");
                List<WebElement> fallbackCandidates = driver.findElements(fallbackDay);
                if (fallbackCandidates.size() > 0) {
                    for (WebElement cand : fallbackCandidates) {
                        if (cand.isDisplayed() && cand.isEnabled()) {
                            clickElementRobust(cand);
                            logger.info("Clicked by fallback day span: " + day);
                            clicked = true;
                            break;
                        }
                    }
                    if (clicked) break;
                }

                // If not found, click next button to go to next month
                boolean nextClicked = false;
                for (By nb : nextButtons) {
                    try {
                        WebElement nextBtn = driver.findElement(nb);
                        if (nextBtn.isDisplayed() && nextBtn.isEnabled()) {
                            clickElementRobust(nextBtn);
                            nextClicked = true;
                            // wait small time after navigation
                            try { Thread.sleep(600); } catch (InterruptedException ignored) {}
                            break;
                        }
                    } catch (NoSuchElementException ignored) {}
                }

                if (!nextClicked) {
                    // If no next button found, break to avoid infinite loop
                    logger.warn("Next button not found while searching for date; aborting month loop.");
                    break;
                }

            } catch (StaleElementReferenceException sere) {
                logger.warn("StaleElementReference while selecting date - retrying iteration. " + sere.getMessage());
            } catch (Exception e) {
                logger.error("Unexpected error while selecting date: " + e.getMessage());
            }
        }

        if (!clicked) {
            logger.error("Failed to select date after trying month navigation. Dumping calendar HTML for debug.");
            dumpCalendarHtmlForDebug();
        }

        // switch back to default content if we switched to iframe earlier
        try { driver.switchTo().defaultContent(); } catch (Exception ignored) {}
    }

    // Robust click helper: tries normal click, if fails uses JS click
    private void clickElementRobust(WebElement el) {
        try {
            el.click();
        } catch (Exception e) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            } catch (Exception jsEx) {
                logger.error("Both normal and JS click failed: " + jsEx.getMessage());
                throw jsEx;
            }
        }
    }

    // Debug helpers
    private void dumpCalendarHtmlForDebug() {
        try {
            WebElement calendar = driver.findElement(By.xpath("//div[contains(@class,'calendar') or contains(@class,'datepicker') or @role='dialog']"));
            String outer = (String)((JavascriptExecutor)driver).executeScript("return arguments[0].outerHTML;", calendar);
            logger.error("CALENDAR HTML SNIPPET:\n" + outer);
        } catch (Exception e) {
            logger.error("Could not dump calendar HTML: " + e.getMessage());
        }
    }

    private void dumpPageSnippetForDebug() {
        try {
            String snippet = (String)((JavascriptExecutor)driver).executeScript(
                    "return document.querySelector('body').innerHTML.slice(0,2000);");
            logger.error("PAGE BODY SNIPPET (first 2000 chars):\n" + snippet);
        } catch (Exception e) {
            logger.error("Could not dump page snippet: " + e.getMessage());
        }
    }


    public FlightListingPage clickSearchButton() {
        scrollToElement(searchButton);
        click(searchButton);
        logger.info("Clicked search button");
        waitForSeconds(3);
        return new FlightListingPage();
    }

    public FlightListingPage searchFlight(String originInput, String destinationInput, String departureDateInput, String returnDateInput) {
        selectRoundTrip();
        enterOrigin(originInput);
        enterDestination(destinationInput);
        selectDepartureDate(departureDateInput);
        selectReturnDate(returnDateInput);
        return clickSearchButton();
    }
}