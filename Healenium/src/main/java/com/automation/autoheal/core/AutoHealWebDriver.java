package com.automation.autoheal.core;

import com.automation.autoheal.config.AutoHealConfig;
import com.automation.autoheal.utils.XPathHealer;
import com.automation.autoheal.utils.PageLoadTracker;
import com.automation.autoheal.reporting.ExtentReportManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Auto-healing WebDriver wrapper that extends WebDriver functionality
 * with automatic XPath healing, page load tracking, and reporting
 */
public class AutoHealWebDriver implements WebDriver, TakesScreenshot, JavascriptExecutor {
    
    private WebDriver driver;
    private XPathHealer xPathHealer;
    private PageLoadTracker pageLoadTracker;
    private ExtentReportManager reportManager;
    private AutoHealConfig config;
    private java.util.Map<String, Integer> retryCountMap = new ConcurrentHashMap<>();
    
    public AutoHealWebDriver(WebDriver driver) {
        this.driver = driver;
        this.config = AutoHealConfig.getInstance();
        this.xPathHealer = new XPathHealer(driver, config);
        this.pageLoadTracker = new PageLoadTracker(driver);
        this.reportManager = ExtentReportManager.getInstance();
        
        // Set default timeouts based on tracked page load times
        updateTimeouts();
    }
    
    /**
     * Auto-healing findElement with XPath recovery
     */
    public WebElement findElement(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            retryCountMap.remove(locator.toString());
            return element;
        } catch (NoSuchElementException e) {
            return handleElementNotFound(locator, e);
        }
    }
    
    /**
     * Auto-healing findElements with XPath recovery
     */
    public List<WebElement> findElements(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            retryCountMap.remove(locator.toString());
            return elements;
        } catch (Exception e) {
            String locatorKey = locator.toString();
            int retryCount = retryCountMap.getOrDefault(locatorKey, 0);
            if (retryCount < config.getMaxRetryAttempts()) {
                retryCountMap.put(locatorKey, retryCount + 1);
                reportManager.logWarning("Element not found with locator: " + locator + ". Attempting to heal...");
                
                if (isXPathLocator(locator)) {
                    By healedLocator = xPathHealer.healXPath(locator);
                    if (healedLocator != null) {
                        reportManager.logInfo("XPath healed successfully: " + healedLocator);
                        retryCountMap.remove(locatorKey);
                        return findElements(healedLocator);
                    }
                }
            }
            retryCountMap.remove(locatorKey);
            reportManager.logError("Failed to find elements with locator: " + locator, e);
            throw e;
        }
    }
    
    /**
     * Handle element not found exception with auto-healing
     */
    private WebElement handleElementNotFound(By locator, Exception e) {
        String locatorKey = locator.toString();
        int retryCount = retryCountMap.getOrDefault(locatorKey, 0);
        
        if (retryCount < config.getMaxRetryAttempts()) {
            retryCountMap.put(locatorKey, retryCount + 1);
            reportManager.logWarning("Element not found with locator: " + locator + ". Attempting to heal...");
            
            if (isXPathLocator(locator)) {
                By healedLocator = xPathHealer.healXPath(locator);
                if (healedLocator != null) {
                    reportManager.logInfo("XPath healed successfully: " + healedLocator);
                    retryCountMap.remove(locatorKey);
                    return findElement(healedLocator);
                }
            }
        }
        
        retryCountMap.remove(locatorKey);
        reportManager.logError("Failed to find element with locator: " + locator, e);
        throw new NoSuchElementException("Element not found even after healing attempt: " + locator.toString(), e);
    }
    
    /**
     * Navigate to URL with page load time tracking
     */
    public void get(String url) {
        pageLoadTracker.startTracking();
        try {
            driver.get(url);
            long loadTime = pageLoadTracker.stopTracking();
            reportManager.logInfo("Page loaded: " + url + " in " + loadTime + "ms");
            
            // Update explicit wait timeout based on page load time
            updateTimeouts();
        } catch (Exception e) {
            pageLoadTracker.stopTracking();
            reportManager.logError("Failed to load page: " + url, e);
            throw e;
        }
    }
    
    /**
     * Update timeouts based on tracked page load times
     */
    private void updateTimeouts() {
        long avgLoadTime = pageLoadTracker.getAverageLoadTime();
        if (avgLoadTime > 0) {
            // Set explicit wait to 2x average load time, minimum 10 seconds
            long waitTime = Math.max(10000, avgLoadTime * 2);
            driver.manage().timeouts().implicitlyWait(waitTime, TimeUnit.MILLISECONDS);
            reportManager.logInfo("Updated implicit wait to: " + waitTime + "ms based on page load time");
        }
    }
    
    /**
     * Get WebDriverWait with auto-updated timeout
     */
    public WebDriverWait getWait() {
        long avgLoadTime = pageLoadTracker.getAverageLoadTime();
        long waitTime = Math.max(10, (avgLoadTime / 1000) + 5); // Convert to seconds, add buffer
        return new WebDriverWait(driver, waitTime);
    }
    
    /**
     * Wait for element with auto-healing
     */
    public WebElement waitForElement(By locator) {
        try {
            return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            reportManager.logWarning("Timeout waiting for element: " + locator + ". Attempting to heal...");
            if (isXPathLocator(locator)) {
                By healedLocator = xPathHealer.healXPath(locator);
                if (healedLocator != null) {
                    return getWait().until(ExpectedConditions.presenceOfElementLocated(healedLocator));
                }
            }
            throw e;
        }
    }
    
    // Delegate all other WebDriver methods
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    public String getTitle() {
        return driver.getTitle();
    }
    
    public String getPageSource() {
        return driver.getPageSource();
    }
    
    public void close() {
        driver.close();
    }
    
    public void quit() {
        if (pageLoadTracker != null) {
            pageLoadTracker.saveStatistics();
        }
        driver.quit();
    }
    
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }
    
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }
    
    public TargetLocator switchTo() {
        return driver.switchTo();
    }
    
    public Navigation navigate() {
        return driver.navigate();
    }
    
    public Options manage() {
        return driver.manage();
    }
    
    // TakesScreenshot implementation
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        if (driver instanceof TakesScreenshot) {
            return ((TakesScreenshot) driver).getScreenshotAs(target);
        }
        throw new WebDriverException("Driver does not support screenshots");
    }
    
    // JavascriptExecutor implementation
    public Object executeScript(String script, Object... args) {
        if (driver instanceof JavascriptExecutor) {
            return ((JavascriptExecutor) driver).executeScript(script, args);
        }
        throw new WebDriverException("Driver does not support JavaScript execution");
    }
    
    public Object executeAsyncScript(String script, Object... args) {
        if (driver instanceof JavascriptExecutor) {
            return ((JavascriptExecutor) driver).executeAsyncScript(script, args);
        }
        throw new WebDriverException("Driver does not support async JavaScript execution");
    }
    
    // Get underlying driver
    public WebDriver getDriver() {
        return driver;
    }
    
    // Get page load tracker
    public PageLoadTracker getPageLoadTracker() {
        return pageLoadTracker;
    }
    
    /**
     * Check if a locator is an XPath locator
     */
    private boolean isXPathLocator(By locator) {
        return locator.toString().startsWith("By.xpath:");
    }
}

