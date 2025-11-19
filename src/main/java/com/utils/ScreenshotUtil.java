package com.utils;

import com.driver.DriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtil.class);

    public static String captureScreenshot(String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotName = testName + "_" + timestamp + ".png";
        String screenshotPath = ConfigReader.getScreenshotFolder() + "/" + screenshotName;

        try {
            File screenshotFile = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.FILE);

            File destinationFile = new File(screenshotPath);
            destinationFile.getParentFile().mkdirs();

            FileUtils.copyFile(screenshotFile, destinationFile);
            logger.info("Screenshot captured: " + screenshotPath);

            return screenshotPath;
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }

    public static String captureScreenshotBase64() {
        try {
            return ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as Base64: " + e.getMessage());
            return null;
        }
    }
}
