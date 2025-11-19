package com.utils;


import com.models.FlightData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GraphUtil {
    private static final Logger logger = LogManager.getLogger(GraphUtil.class);

    public static Map<String, Double> calculatePriceStatsByAirline(List<FlightData> flightDataList) {
        Map<String, List<Double>> pricesByAirline = flightDataList.stream()
                .collect(Collectors.groupingBy(
                        FlightData::getAirlineName,
                        Collectors.mapping(FlightData::getPrice, Collectors.toList())
                ));

        Map<String, Double> statistics = new HashMap<>();

        pricesByAirline.forEach((airline, prices) -> {
            statistics.put(airline + "_MIN", Collections.min(prices));
            statistics.put(airline + "_MAX", Collections.max(prices));
            statistics.put(airline + "_AVG", prices.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0));
        });

        logger.info("Calculated price statistics: " + statistics);
        return statistics;
    }

    public static String createPriceComparisonGraph(List<FlightData> flightDataList, String route) {
        try {
            Map<String, List<Double>> pricesByAirline = flightDataList.stream()
                    .collect(Collectors.groupingBy(
                            FlightData::getAirlineName,
                            Collectors.mapping(FlightData::getPrice, Collectors.toList())
                    ));

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            pricesByAirline.forEach((airline, prices) -> {
                double min = Collections.min(prices);
                double max = Collections.max(prices);
                double avg = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

                dataset.addValue(min, "Minimum", airline);
                dataset.addValue(max, "Maximum", airline);
                dataset.addValue(avg, "Average", airline);
            });

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Price Comparison by Airline - " + route,
                    "Airline",
                    "Price",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false
            );

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "price_comparison_" + timestamp + ".png";
            String filePath = ConfigReader.getGraphOutputFolder() + "/" + fileName;

            File chartFile = new File(filePath);
            chartFile.getParentFile().mkdirs();

            ChartUtils.saveChartAsPNG(chartFile, barChart, 800, 600);

            logger.info("Graph created: " + filePath);
            return filePath;

        } catch (Exception e) {
            logger.error("Failed to create graph: " + e.getMessage());
            return null;
        }
    }

    public static List<FlightData> findMostCostEffectiveFlights(List<FlightData> flightDataList) {
        // Sort by price and return top 5 cheapest flights
        return flightDataList.stream()
                .sorted(Comparator.comparingDouble(FlightData::getPrice))
                .limit(5)
                .collect(Collectors.toList());
    }

    public static void printPriceAnalysis(List<FlightData> flightDataList) {
        Map<String, Double> stats = calculatePriceStatsByAirline(flightDataList);

        logger.info("===== PRICE ANALYSIS =====");
        stats.forEach((key, value) ->
                logger.info(key + ": " + String.format("%.2f", value))
        );

        List<FlightData> cheapestFlights = findMostCostEffectiveFlights(flightDataList);
        logger.info("===== TOP 5 CHEAPEST FLIGHTS =====");
        cheapestFlights.forEach(flight ->
                logger.info(flight.getAirlineName() + " - " + flight.getPrice() + " TL")
        );
    }
}