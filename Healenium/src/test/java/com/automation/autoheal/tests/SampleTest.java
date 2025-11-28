package com.automation.autoheal.tests;

import com.automation.autoheal.reporting.ExtentReportManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/**
 * Sample test class demonstrating AutoHeal framework usage
 */
public class SampleTest extends BaseTest {
    
    private ExtentReportManager reportManager = ExtentReportManager.getInstance();
    
    @Test(description = "Test auto-healing XPath functionality")
    public void testAutoHealXPath() {
        reportManager.logInfo("Starting test: testAutoHealXPath");
        
        // Navigate to a test page
        driver.get("https://the-internet.herokuapp.com/");
        reportManager.logInfo("Navigated to test page");
        
        // Try to find element with XPath (this will be auto-healed if it fails)
        try {
            // This XPath might fail, but will be auto-healed
            WebElement element = driver.findElement(By.xpath("//a[text()='Form Authentication']"));
            reportManager.logPass("Element found successfully");
            element.click();
            reportManager.logInfo("Clicked on Form Authentication link");
        } catch (Exception e) {
            reportManager.logError("Test failed", e);
            throw e;
        }
    }
    
    @Test(description = "Test page load time tracking")
    public void testPageLoadTracking() {
        reportManager.logInfo("Starting test: testPageLoadTracking");
        
        // Navigate to multiple pages to track load times
        driver.get("https://the-internet.herokuapp.com/");
        reportManager.logInfo("Page 1 loaded");
        
        driver.get("https://the-internet.herokuapp.com/login");
        reportManager.logInfo("Page 2 loaded");
        
        // Get page load statistics
        if (driver.getPageLoadTracker() != null) {
            long avgLoadTime = driver.getPageLoadTracker().getAverageLoadTime();
            reportManager.logInfo("Average page load time: " + avgLoadTime + "ms");
        }
    }
    
    @Test(description = "Test explicit wait with auto-updated timeout")
    public void testExplicitWait() {
        reportManager.logInfo("Starting test: testExplicitWait");
        
        driver.get("https://the-internet.herokuapp.com/dynamic_loading/1");
        reportManager.logInfo("Navigated to dynamic loading page");
        
        // Use waitForElement which uses auto-updated timeout
        try {
            WebElement startButton = driver.waitForElement(By.xpath("//button[text()='Start']"));
            reportManager.logPass("Start button found using auto-updated wait");
            startButton.click();
            
            // Wait for the finish element
            WebElement finishElement = driver.waitForElement(By.id("finish"));
            reportManager.logPass("Finish element appeared after dynamic load");
        } catch (Exception e) {
            reportManager.logError("Test failed", e);
            throw e;
        }
    }
    
    @Test(description = "Test broken XPath healing")
    public void testBrokenXPathHealing() {
        reportManager.logInfo("Starting test: testBrokenXPathHealing");
        
        driver.get("https://the-internet.herokuapp.com/");
        
        // Try with a potentially broken XPath
        // The framework will attempt to heal it automatically
        try {
            // This XPath might be broken, but will be healed
            WebElement element = driver.findElement(
                By.xpath("//a[@href='/login' and text()='Form Authentication']")
            );
            reportManager.logPass("Element found (possibly after healing)");
            element.click();
        } catch (Exception e) {
            reportManager.logError("Element not found even after healing attempt", e);
            throw e;
        }
    }
}

