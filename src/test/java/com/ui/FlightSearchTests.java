package com.ui;

import com.aventstack.extentreports.Status;
import com.pages.FlightListingPage;
import com.pages.HomePage;
import com.tests.BaseTest;
import com.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FlightSearchTests extends BaseTest {

    @Test(priority = 1, description = "Case 1: Basic Flight Search and Time Filter")
    public void testBasicFlightSearchWithTimeFilter() {
        test.log(Status.INFO, "Starting basic flight search test with time filter");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage(ConfigReader.getBaseUrl());
        test.log(Status.INFO, "Navigated to homepage");

        String originInput = ConfigReader.getDepartureCity();
        String destinationInput = ConfigReader.getArrivalCity();
        String departureDateInput = ConfigReader.getDepartureDate();
        String returnDateInput = ConfigReader.getReturnDate();

        test.log(Status.INFO, "Searching flight: " + originInput + " to " + destinationInput);
        FlightListingPage listingPage = homePage.searchFlight(originInput, destinationInput, departureDateInput, returnDateInput);


        String timeStart = ConfigReader.getDepartureTimeStart();
        String timeEnd = ConfigReader.getDepartureTimeEnd();
        listingPage.applyDepartureTimeFilter(timeStart, timeEnd);
        test.log(Status.INFO, "Applied time filter: " + timeStart + " - " + timeEnd);

        boolean timesInRange = listingPage.verifyFlightTimesInRange(timeStart, timeEnd);
        Assert.assertTrue(timesInRange, "All flights should be within the specified time range");
        test.log(Status.PASS, "All flights are within the time range");

        int flightCount = listingPage.getFlightCount();
        Assert.assertTrue(flightCount > 0, "At least one flight should be displayed");
        test.log(Status.PASS, "Found " + flightCount + " flights matching the criteria");
    }

    @Test(priority = 2, description = "Case 2: Price Sorting for Turkish Airlines")
    public void testPriceSortingForTurkishAirlines() {
        test.log(Status.INFO, "Starting price sorting test for Turkish Airlines");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage(ConfigReader.getBaseUrl());

        String originInput = ConfigReader.getDepartureCity();
        String destinationInput = ConfigReader.getArrivalCity();
        String departureDateInput = ConfigReader.getDepartureDate();
        String returnDateInput = ConfigReader.getReturnDate();

        FlightListingPage listingPage = homePage.searchFlight(originInput, destinationInput, departureDateInput, returnDateInput);
        test.log(Status.INFO, "Flight search completed");

        String timeStart = ConfigReader.getDepartureTimeStart();
        String timeEnd = ConfigReader.getDepartureTimeEnd();
        listingPage.applyDepartureTimeFilter(timeStart, timeEnd);
        test.log(Status.INFO, "Time filter applied");

        listingPage.selectTurkishAirlinesFilter();
        test.log(Status.INFO, "Turkish Airlines filter applied");

        boolean allTurkishAirlines = listingPage.verifyAllFlightsAreTurkishAirlines();
        Assert.assertTrue(allTurkishAirlines, "All displayed flights should be Turkish Airlines");
        test.log(Status.PASS, "All flights are Turkish Airlines");

        listingPage.sortByPrice();
        test.log(Status.INFO, "Sorted by price");

        boolean pricesSorted = listingPage.verifyPricesSortedAscending();
        Assert.assertTrue(pricesSorted, "Prices should be sorted in ascending order");
        test.log(Status.PASS, "Prices are correctly sorted in ascending order");
    }

    @Test(priority = 3, description = "Case 3: Critical Path - Complete Booking Flow")
    public void testCriticalUserPath() {
        test.log(Status.INFO, "Starting critical user path test - Complete booking flow");

        // Step 1: Navigate to homepage
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage(ConfigReader.getBaseUrl());
        test.log(Status.PASS, "Step 1: Successfully navigated to homepage");

        // Step 2: Search for flights
        String origin = ConfigReader.getDepartureCity();
        String destination = ConfigReader.getArrivalCity();
        String departureDate = ConfigReader.getDepartureDate();
        String returnDate = ConfigReader.getReturnDate();

        FlightListingPage listingPage = homePage.searchFlight(origin, destination, departureDate, returnDate);
        Assert.assertTrue(listingPage.isFlightListDisplayed(), "Flight list must be displayed");
        test.log(Status.PASS, "Step 2: Flight search successful, results displayed");

        // Step 3: Apply filters (critical for user to find suitable flight)
        String timeStart = ConfigReader.getDepartureTimeStart();
        String timeEnd = ConfigReader.getDepartureTimeEnd();
        listingPage.applyDepartureTimeFilter(timeStart, timeEnd);
        test.log(Status.PASS, "Step 3: Filters applied successfully");

        // Step 4: Verify filtered results
        boolean timesInRange = listingPage.verifyFlightTimesInRange(timeStart, timeEnd);
        Assert.assertTrue(timesInRange, "Critical: Filtered results must match user criteria");
        test.log(Status.PASS, "Step 4: Filtered results verified successfully");

        // Step 5: Verify flight count
        int flightCount = listingPage.getFlightCount();
        Assert.assertTrue(flightCount > 0, "Critical: At least one flight option must be available");
        test.log(Status.PASS, "Step 5: " + flightCount + " flight options available for booking");

        test.log(Status.PASS, "Critical user path completed successfully - User can proceed to booking");
    }
}