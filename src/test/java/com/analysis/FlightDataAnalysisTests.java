package com.analysis;


import com.aventstack.extentreports.Status;
import com.models.FlightData;
import com.pages.FlightListingPage;
import com.pages.HomePage;
import com.tests.BaseTest;
import com.utils.CSVUtil;
import com.utils.ConfigReader;
import com.utils.GraphUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class FlightDataAnalysisTests extends BaseTest {

    @Test(priority = 1, description = "Extract flight data and perform analysis")
    public void testFlightDataExtraction() {
        test.log(Status.INFO, "Starting flight data extraction and analysis test");

        // Navigate and search
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage(ConfigReader.getBaseUrl());

        String origin = ConfigReader.getProperty("analysis.departure.city");
        String destination = ConfigReader.getProperty("analysis.arrival.city");
        String departureDate = ConfigReader.getProperty("analysis.departure.date");
        String returnDate = ConfigReader.getProperty("analysis.return.date");

        test.log(Status.INFO, "Searching flights: " + origin + " to " + destination);
        FlightListingPage listingPage = homePage.searchFlight(origin, destination, departureDate, returnDate);

        // Extract data
        test.log(Status.INFO, "Extracting flight data from search results");
        List<FlightData> flightDataList = listingPage.extractAllFlightData();

        Assert.assertFalse(flightDataList.isEmpty(), "Flight data list should not be empty");
        test.log(Status.PASS, "Extracted " + flightDataList.size() + " flight records");

        // Save to CSV
        String route = origin + " - " + destination;
        String csvPath = CSVUtil.writeFlightDataToCSV(flightDataList, route);
        Assert.assertNotNull(csvPath, "CSV file should be created");
        test.log(Status.PASS, "Flight data saved to CSV: " + csvPath);

        // Calculate statistics
        test.log(Status.INFO, "Calculating price statistics by airline");
        Map<String, Double> statistics = GraphUtil.calculatePriceStatsByAirline(flightDataList);
        Assert.assertFalse(statistics.isEmpty(), "Statistics should be calculated");
        test.log(Status.PASS, "Price statistics calculated successfully");

        // Print analysis
        GraphUtil.printPriceAnalysis(flightDataList);

        // Create graph
        test.log(Status.INFO, "Creating price comparison graph");
        String graphPath = GraphUtil.createPriceComparisonGraph(flightDataList, route);
        Assert.assertNotNull(graphPath, "Graph should be created");
        test.log(Status.PASS, "Price comparison graph created: " + graphPath);

        // Find most cost-effective flights
        List<FlightData> cheapestFlights = GraphUtil.findMostCostEffectiveFlights(flightDataList);
        Assert.assertFalse(cheapestFlights.isEmpty(), "Should find cost-effective flights");
        test.log(Status.PASS, "Found " + cheapestFlights.size() + " most cost-effective flights");

        // Log results
        test.log(Status.INFO, "=== TOP 5 MOST COST-EFFECTIVE FLIGHTS ===");
        for (int i = 0; i < cheapestFlights.size(); i++) {
            FlightData flight = cheapestFlights.get(i);
            test.log(Status.INFO, (i + 1) + ". " + flight.getAirlineName() +
                    " - " + flight.getPrice() + " TL" +
                    " (Duration: " + flight.getDuration() + ")");
        }

        test.log(Status.PASS, "Flight data analysis completed successfully");
    }

    @Test(priority = 2, description = "Compare flights between different routes")
    public void testMultiRouteComparison() {
        test.log(Status.INFO, "Testing multi-route comparison functionality");

        // This test demonstrates the repeatability of the analysis for different routes
        String[] routes = {
                "Istanbul|Ankara|2025-12-01|2025-12-08",
                "Istanbul|Izmir|2025-12-05|2025-12-12"
        };

        for (String routeConfig : routes) {
            String[] parts = routeConfig.split("\\|");
            String origin = parts[0];
            String destination = parts[1];
            String depDate = parts[2];
            String retDate = parts[3];

            test.log(Status.INFO, "Analyzing route: " + origin + " to " + destination);

            HomePage homePage = new HomePage();
            homePage.navigateToHomePage(ConfigReader.getBaseUrl());

            FlightListingPage listingPage = homePage.searchFlight(origin, destination, depDate, retDate);
            List<FlightData> flightDataList = listingPage.extractAllFlightData();

            if (!flightDataList.isEmpty()) {
                String route = origin + " - " + destination;
                CSVUtil.writeFlightDataToCSV(flightDataList, route);
                GraphUtil.createPriceComparisonGraph(flightDataList, route);

                test.log(Status.PASS, "Route analysis completed: " + route);
            } else {
                test.log(Status.WARNING, "No flights found for route: " + origin + " to " + destination);
            }
        }

        test.log(Status.PASS, "Multi-route comparison completed");
    }
}