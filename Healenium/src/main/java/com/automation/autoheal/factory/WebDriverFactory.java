package com.automation.autoheal.factory;

import com.automation.autoheal.core.AutoHealWebDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;

/**
 * Factory class for creating WebDriver instances wrapped with AutoHeal functionality
 */
public class WebDriverFactory {
    
    private static final String CHROME = "chrome";
    private static final String FIREFOX = "firefox";
    private static final String IE = "ie";
    
    /**
     * Create AutoHeal WebDriver instance
     */
    public static AutoHealWebDriver createDriver(String browserName) {
        WebDriver driver;
        
        switch (browserName.toLowerCase()) {
            case CHROME:
                driver = createChromeDriver();
                break;
            case FIREFOX:
                driver = createFirefoxDriver();
                break;
            case IE:
                driver = createIEDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
        
        return new AutoHealWebDriver(driver);
    }
    
    /**
     * Create Chrome driver
     */
    private static WebDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        // Add more Chrome options as needed
        return new ChromeDriver(options);
    }
    
    /**
     * Create Firefox driver
     */
    private static WebDriver createFirefoxDriver() {
        FirefoxOptions options = new FirefoxOptions();
        // Add Firefox options as needed
        return new FirefoxDriver(options);
    }
    
    /**
     * Create Internet Explorer driver
     */
    private static WebDriver createIEDriver() {
        InternetExplorerOptions options = new InternetExplorerOptions();
        options.ignoreZoomSettings();
        options.introduceFlakinessByIgnoringSecurityDomains();
        return new InternetExplorerDriver(options);
    }
}

