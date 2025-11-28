package com.automation.autoheal.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Extent Reports manager for automatic log attachment and reporting
 */
public class ExtentReportManager {
    
    private static ExtentReportManager instance;
    private ExtentReports extent;
    private Map<Long, ExtentTest> testMap;
    private String reportPath;
    
    private ExtentReportManager() {
        testMap = new HashMap<>();
        initializeReport();
    }
    
    public static synchronized ExtentReportManager getInstance() {
        if (instance == null) {
            instance = new ExtentReportManager();
        }
        return instance;
    }
    
    /**
     * Initialize Extent Reports
     */
    private void initializeReport() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        reportPath = "test-output/ExtentReport_" + timestamp + ".html";
        
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("AutoHeal Test Report");
        sparkReporter.config().setReportName("AutoHeal Framework Test Execution Report");
        sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
        
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Framework", "AutoHeal Framework");
        extent.setSystemInfo("Selenium Version", "3.141.59");
        extent.setSystemInfo("TestNG Version", "7.5");
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
    }
    
    /**
     * Create a new test in the report
     */
    public ExtentTest createTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        testMap.put(Thread.currentThread().getId(), test);
        return test;
    }
    
    /**
     * Create a new test with description
     */
    public ExtentTest createTest(String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        testMap.put(Thread.currentThread().getId(), test);
        return test;
    }
    
    /**
     * Get current test
     */
    public ExtentTest getTest() {
        return testMap.get(Thread.currentThread().getId());
    }
    
    /**
     * Log info message
     */
    public void logInfo(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.INFO, message);
        }
    }
    
    /**
     * Log pass message
     */
    public void logPass(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.PASS, MarkupHelper.createLabel(message, ExtentColor.GREEN));
        }
    }
    
    /**
     * Log fail message
     */
    public void logFail(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.FAIL, MarkupHelper.createLabel(message, ExtentColor.RED));
        }
    }
    
    /**
     * Log warning message
     */
    public void logWarning(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.WARNING, MarkupHelper.createLabel(message, ExtentColor.YELLOW));
        }
    }
    
    /**
     * Log error with exception
     */
    public void logError(String message, Throwable throwable) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.FAIL, message);
            test.log(Status.FAIL, throwable);
        }
    }
    
    /**
     * Attach screenshot to report
     */
    public void attachScreenshot(WebDriver driver, String screenshotName) {
        ExtentTest test = getTest();
        if (test != null && driver instanceof TakesScreenshot) {
            try {
                String screenshotPath = captureScreenshot(driver, screenshotName);
                test.addScreenCaptureFromPath(screenshotPath);
                logInfo("Screenshot attached: " + screenshotName);
            } catch (Exception e) {
                logError("Failed to attach screenshot: " + screenshotName, e);
            }
        }
    }
    
    /**
     * Capture screenshot
     */
    private String captureScreenshot(WebDriver driver, String screenshotName) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotPath = "test-output/screenshots/" + screenshotName + "_" + timestamp + ".png";
        
        // Create screenshots directory if it doesn't exist
        Files.createDirectories(Paths.get("test-output/screenshots"));
        
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Files.copy(screenshotFile.toPath(), Paths.get(screenshotPath));
        
        return screenshotPath;
    }
    
    /**
     * Log failure with screenshot
     */
    public void logFailureWithScreenshot(WebDriver driver, String message, Throwable throwable) {
        logError(message, throwable);
        attachScreenshot(driver, "failure_screenshot");
    }
    
    /**
     * Flush report
     */
    public void flush() {
        extent.flush();
    }
    
    /**
     * Get report path
     */
    public String getReportPath() {
        return reportPath;
    }
    
    /**
     * Remove test from map
     */
    public void removeTest() {
        testMap.remove(Thread.currentThread().getId());
    }
}

