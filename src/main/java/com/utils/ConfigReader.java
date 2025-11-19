package com.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";

    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH);
            properties.load(fis);
            fis.close();
            logger.info("Configuration file loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load configuration file: " + e.getMessage());
            throw new RuntimeException("Configuration file not found at: " + CONFIG_FILE_PATH);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property not found for key: " + key);
        }
        return value;
    }

    public static String getBrowser() {
        return getProperty("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless"));
    }

    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait"));
    }

    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("page.load.timeout"));
    }

    public static String getBaseUrl() {
        return getProperty("base.url");
    }

    public static String getPetStoreApiUrl() {
        return getProperty("petstore.api.url");
    }

    public static String getDepartureCity() {
        return getProperty("departure.city");
    }

    public static String getArrivalCity() {
        return getProperty("arrival.city");
    }

    public static String getDepartureDate() {
        return getProperty("departure.date");
    }

    public static String getReturnDate() {
        return getProperty("return.date");
    }

    public static String getDepartureTimeStart() {
        return getProperty("departure.time.start");
    }

    public static String getDepartureTimeEnd() {
        return getProperty("departure.time.end");
    }

    public static String getPreferredAirline() {
        return getProperty("preferred.airline");
    }

    public static String getScreenshotFolder() {
        return getProperty("screenshot.folder");
    }

    public static String getReportFolder() {
        return getProperty("report.folder");
    }

    public static String getCsvOutputFolder() {
        return getProperty("csv.output.folder");
    }

    public static String getGraphOutputFolder() {
        return getProperty("graph.output.folder");
    }

    public static int getMaxRetryCount() {
        return Integer.parseInt(getProperty("max.retry.count"));
    }
}