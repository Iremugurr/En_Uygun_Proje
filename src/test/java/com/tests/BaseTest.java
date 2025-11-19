package com.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.driver.DriverManager;
import com.utils.ConfigReader;
import com.utils.ScreenshotUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseTest {
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected static ExtentReports extent;
    protected static ExtentTest test;

    @BeforeSuite
    public void setupSuite() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportPath = ConfigReader.getReportFolder() + "/TestReport_" + timestamp + ".html";

        File reportFile = new File(reportPath);
        reportFile.getParentFile().mkdirs();

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("Enuygun Automation Test Report");
        sparkReporter.config().setReportName("Flight Search Automation");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Tester", "QA Automation Engineer");
        extent.setSystemInfo("Browser", ConfigReader.getBrowser());
        extent.setSystemInfo("Environment", "Production");

        logger.info("Test suite setup completed");
    }

    @BeforeMethod
    public void setup(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        test = extent.createTest(testName);
        logger.info("Starting test: " + testName);

        DriverManager.getDriver();
        test.log(Status.INFO, "Browser launched successfully");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "Test Failed: " + result.getThrowable());

            String screenshotPath = ScreenshotUtil.captureScreenshot(result.getMethod().getMethodName());
            if (screenshotPath != null) {
                test.addScreenCaptureFromPath(screenshotPath);
            }

            logger.error("Test failed: " + result.getMethod().getMethodName());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, "Test Passed");
            logger.info("Test passed: " + result.getMethod().getMethodName());
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.log(Status.SKIP, "Test Skipped: " + result.getThrowable());
            logger.warn("Test skipped: " + result.getMethod().getMethodName());
        }

        DriverManager.quitDriver();
    }

    @AfterSuite
    public void tearDownSuite() {
        extent.flush();
        logger.info("Test suite execution completed");
    }
}