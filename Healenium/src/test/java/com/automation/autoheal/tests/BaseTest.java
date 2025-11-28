package com.automation.autoheal.tests;

import com.automation.autoheal.core.AutoHealWebDriver;
import com.automation.autoheal.factory.WebDriverFactory;
import com.automation.autoheal.reporting.ExtentReportManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

/**
 * Base test class for all test classes
 */
public class BaseTest {
    
    protected AutoHealWebDriver driver;
    protected ExtentReportManager reportManager;
    
    @BeforeMethod
    @Parameters("browser")
    public void setUp(String browser) {
        if (browser == null || browser.isEmpty()) {
            browser = "chrome"; // Default browser
        }
        driver = WebDriverFactory.createDriver(browser);
        reportManager = ExtentReportManager.getInstance();
        reportManager.logInfo("Browser initialized: " + browser);
    }
    
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            // Log page load statistics before closing
            if (driver.getPageLoadTracker() != null) {
                reportManager.logInfo("Page load statistics: " + 
                    driver.getPageLoadTracker().getStatistics().toString());
            }
            driver.quit();
            reportManager.logInfo("Browser closed");
        }
    }
}

