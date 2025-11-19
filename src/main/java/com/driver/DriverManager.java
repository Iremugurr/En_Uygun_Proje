package com.driver;

import com.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class DriverManager {
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }

    private static void initializeDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        boolean headless = ConfigReader.isHeadless();

        logger.info("Initializing " + browser + " driver (Headless: " + headless + ")");

        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless");
                }
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--window-size=1920,1080");
                chromeOptions.addArguments("--lang=tr");
                driver.set(new ChromeDriver(chromeOptions));
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }
                firefoxOptions.addArguments("--disable-notifications");
                firefoxOptions.addArguments("--width=1920");
                firefoxOptions.addArguments("--height=1080");
                driver.set(new FirefoxDriver(firefoxOptions));
                break;

            default:
                throw new IllegalArgumentException("Browser not supported: " + browser);
        }

        configureDriver();
        logger.info("Driver initialized successfully");
    }

    private static void configureDriver() {
        driver.get().manage().window().maximize();
        driver.get().manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        driver.get().manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        driver.get().manage().deleteAllCookies();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            logger.info("Closing driver");
            driver.get().quit();
            driver.remove();
        }
    }
}