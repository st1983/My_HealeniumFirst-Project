package com.automation.autoheal.listeners;

import com.automation.autoheal.core.AutoHealWebDriver;
import com.automation.autoheal.reporting.ExtentReportManager;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener for automatic test reporting and failure handling
 */
public class TestListener implements ITestListener {
    
    private ExtentReportManager reportManager = ExtentReportManager.getInstance();
    
    @Override
    public void onStart(ITestContext context) {
        // Initialize report if needed
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        if (description == null || description.isEmpty()) {
            description = testName;
        }
        reportManager.createTest(testName, description);
        reportManager.logInfo("Test started: " + testName);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        reportManager.logPass("Test passed: " + result.getMethod().getMethodName());
        reportManager.removeTest();
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        
        reportManager.logFail("Test failed: " + testName);
        
        // Try to get WebDriver from test instance and capture screenshot
        Object testInstance = result.getInstance();
        try {
            // Try to get AutoHealWebDriver from test class
            java.lang.reflect.Field[] fields = testInstance.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.getType().equals(AutoHealWebDriver.class) || 
                    field.getType().getName().contains("WebDriver")) {
                    field.setAccessible(true);
                    Object driver = field.get(testInstance);
                    if (driver instanceof AutoHealWebDriver) {
                        AutoHealWebDriver autoHealDriver = (AutoHealWebDriver) driver;
                        reportManager.logFailureWithScreenshot(
                            autoHealDriver.getDriver(), 
                            "Test failed with exception", 
                            throwable
                        );
                        break;
                    } else if (driver instanceof org.openqa.selenium.WebDriver) {
                        reportManager.logFailureWithScreenshot(
                            (org.openqa.selenium.WebDriver) driver, 
                            "Test failed with exception", 
                            throwable
                        );
                        break;
                    }
                }
            }
        } catch (Exception e) {
            reportManager.logError("Failed to capture screenshot", e);
        }
        
        // Log the exception
        reportManager.logError("Test failure details", throwable);
        reportManager.removeTest();
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        reportManager.logWarning("Test skipped: " + result.getMethod().getMethodName());
        reportManager.removeTest();
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Handle flaky tests if needed
    }
    
    @Override
    public void onFinish(ITestContext context) {
        reportManager.flush();
        reportManager.logInfo("Test execution completed. Report available at: " + reportManager.getReportPath());
    }
}

