package com.pages;

import com.models.FlightData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlightListingPage extends BasePage {

    @FindBy(css = "div[class*='flight-list']")
    private WebElement flightListContainer;

    @FindBy(xpath = "//div[contains(@class, 'flight-item')]")
    private List<WebElement> flightItems;

    @FindBy(css = "button[class*='filter'], div[class*='filter']")
    private WebElement filterButton;

    @FindBy(css = "input[type='checkbox'][value*='Turkish']")
    private WebElement turkishAirlinesCheckbox;

    @FindBy(css = "select[class*='sort'], button[class*='sort']")
    private WebElement sortDropdown;

    public FlightListingPage() {
        super();
        waitForFlightListToLoad();
    }

    private void waitForFlightListToLoad() {
        try {
            waitForSeconds(5);
            waitForElementToBeVisible(flightListContainer);
            logger.info("Flight list loaded successfully");
        } catch (Exception e) {
            logger.warn("Flight list container not found, continuing anyway");
        }
    }

    public void applyDepartureTimeFilter(String startTime, String endTime) {
        try {
            scrollToElement(filterButton);

            WebElement timeFilterStart = driver.findElement(
                    By.xpath("//input[@type='time' or contains(@placeholder, 'başlangıç')]"));
            sendKeys(timeFilterStart, startTime);

            WebElement timeFilterEnd = driver.findElement(
                    By.xpath("//input[@type='time' or contains(@placeholder, 'bitiş')]"));
            sendKeys(timeFilterEnd, endTime);

            waitForSeconds(2);
            logger.info("Applied departure time filter: " + startTime + " - " + endTime);
        } catch (Exception e) {
            logger.error("Failed to apply time filter: " + e.getMessage());
        }
    }

    public void selectTurkishAirlinesFilter() {
        try {
            scrollToElement(turkishAirlinesCheckbox);
            if (!turkishAirlinesCheckbox.isSelected()) {
                click(turkishAirlinesCheckbox);
            }
            waitForSeconds(2);
            logger.info("Selected Turkish Airlines filter");
        } catch (Exception e) {
            logger.error("Failed to select Turkish Airlines filter: " + e.getMessage());
        }
    }

    public void sortByPrice() {
        try {
            click(sortDropdown);
            WebElement priceOption = driver.findElement(
                    By.xpath("//option[contains(text(), 'Fiyat')] | //button[contains(text(), 'Fiyat')]"));
            click(priceOption);
            waitForSeconds(2);
            logger.info("Sorted by price");
        } catch (Exception e) {
            logger.error("Failed to sort by price: " + e.getMessage());
        }
    }

    public boolean verifyFlightTimesInRange(String startTime, String endTime) {
        try {
            List<WebElement> flights = driver.findElements(
                    By.xpath("//div[contains(@class, 'flight')]//span[contains(@class, 'time')]"));

            LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));

            for (WebElement flight : flights) {
                String timeText = flight.getText().replaceAll("[^0-9:]", "");
                if (timeText.contains(":")) {
                    LocalTime flightTime = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"));

                    if (flightTime.isBefore(start) || flightTime.isAfter(end)) {
                        logger.error("Flight time out of range: " + timeText);
                        return false;
                    }
                }
            }

            logger.info("All flights are within the specified time range");
            return true;
        } catch (Exception e) {
            logger.error("Failed to verify flight times: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyAllFlightsAreTurkishAirlines() {
        try {
            List<WebElement> airlines = driver.findElements(
                    By.xpath("//div[contains(@class, 'airline')]//span"));

            for (WebElement airline : airlines) {
                String airlineName = airline.getText();
                if (!airlineName.toLowerCase().contains("turkish")) {
                    logger.error("Non-Turkish Airlines flight found: " + airlineName);
                    return false;
                }
            }

            logger.info("All flights are Turkish Airlines");
            return true;
        } catch (Exception e) {
            logger.error("Failed to verify airlines: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyPricesSortedAscending() {
        try {
            List<WebElement> prices = driver.findElements(
                    By.xpath("//div[contains(@class, 'price')]//span[contains(@class, 'amount')]"));

            double previousPrice = 0;
            for (WebElement priceElement : prices) {
                String priceText = priceElement.getText().replaceAll("[^0-9.,]", "").replace(",", ".");
                double currentPrice = Double.parseDouble(priceText);

                if (currentPrice < previousPrice) {
                    logger.error("Prices not sorted correctly: " + previousPrice + " > " + currentPrice);
                    return false;
                }
                previousPrice = currentPrice;
            }

            logger.info("Prices are sorted in ascending order");
            return true;
        } catch (Exception e) {
            logger.error("Failed to verify price sorting: " + e.getMessage());
            return false;
        }
    }

    public List<FlightData> extractAllFlightData() {
        List<FlightData> flightDataList = new ArrayList<>();

        try {
            List<WebElement> flights = driver.findElements(
                    By.xpath("//div[contains(@class, 'flight-item')]"));

            for (WebElement flight : flights) {
                FlightData data = new FlightData();

                try {
                    data.setDepartureTime(flight.findElement(
                            By.xpath(".//span[contains(@class, 'departure-time')]")).getText());
                    data.setArrivalTime(flight.findElement(
                            By.xpath(".//span[contains(@class, 'arrival-time')]")).getText());
                    data.setAirlineName(flight.findElement(
                            By.xpath(".//span[contains(@class, 'airline')]")).getText());

                    String priceText = flight.findElement(
                                    By.xpath(".//span[contains(@class, 'price')]")).getText()
                            .replaceAll("[^0-9.,]", "").replace(",", ".");
                    data.setPrice(Double.parseDouble(priceText));

                    data.setConnectionInfo(flight.findElement(
                            By.xpath(".//span[contains(@class, 'connection')]")).getText());
                    data.setDuration(flight.findElement(
                            By.xpath(".//span[contains(@class, 'duration')]")).getText());

                    flightDataList.add(data);
                } catch (Exception e) {
                    logger.warn("Could not extract all data for a flight: " + e.getMessage());
                }
            }

            logger.info("Extracted " + flightDataList.size() + " flight data entries");
        } catch (Exception e) {
            logger.error("Failed to extract flight data: " + e.getMessage());
        }

        return flightDataList;
    }

    public boolean isFlightListDisplayed() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Container görünür mü?
            wait.until(ExpectedConditions.visibilityOf(flightListContainer));

            // Flight items listesi boş olmasın diye bekliyoruz
            wait.until(d -> flightItems.size() > 0);

            return flightListContainer.isDisplayed() && !flightItems.isEmpty();

        } catch (Exception e) {
            return false;
        }
    }


    public int getFlightCount() {
        return flightItems.size();
    }
}