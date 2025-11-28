# AutoHeal Framework - Project Summary

## Overview
This project is an auto-healing framework for Selenium WebDriver, similar to Healenium, that automatically fixes broken XPath locators, tracks page load times, and integrates with Extent Reports.

## Requirements Fulfilled

✅ **1. Automatic update of failed XPath from current DOM data**
   - Implemented in `XPathHealer.java`
   - Analyzes DOM structure using JSoup
   - Extracts attributes from broken XPath
   - Generates new XPath based on matching elements

✅ **2. Automatic update of page load time in explicit wait**
   - Implemented in `PageLoadTracker.java`
   - Tracks page load times for each navigation
   - Automatically updates explicit wait timeouts based on average load time
   - Integrated in `AutoHealWebDriver.java`

✅ **3. Automatic update of failed logs and attach to Extent Report**
   - Implemented in `ExtentReportManager.java`
   - TestNG listener captures failures automatically
   - Screenshots attached on failure
   - Detailed logs with healing attempts

✅ **4. Selenium 3.14, Java 8, TestNG 7.5**
   - Selenium: 3.141.59 (latest 3.14.x)
   - Java: 1.8 (configured in pom.xml)
   - TestNG: 7.5

✅ **5. Compatible with existing Selenium Java TestNG Maven projects**
   - Can be integrated as a dependency
   - Minimal code changes required
   - Works with existing Page Object Model
   - See INTEGRATION_GUIDE.md for details

✅ **6. Copy of Healenium tool for auto-healing failure XPath**
   - Similar functionality to Healenium
   - Automatic XPath healing on failure
   - DOM analysis and XPath regeneration
   - Caching of healed XPaths

## Project Structure

```
autoheal-framework/
├── src/
│   ├── main/
│   │   ├── java/com/automation/autoheal/
│   │   │   ├── config/
│   │   │   │   └── AutoHealConfig.java          # Configuration manager
│   │   │   ├── core/
│   │   │   │   └── AutoHealWebDriver.java       # Main WebDriver wrapper
│   │   │   ├── factory/
│   │   │   │   └── WebDriverFactory.java        # WebDriver factory
│   │   │   ├── listeners/
│   │   │   │   └── TestListener.java            # TestNG listener
│   │   │   ├── reporting/
│   │   │   │   └── ExtentReportManager.java     # Extent Reports manager
│   │   │   └── utils/
│   │   │       ├── PageLoadTracker.java         # Page load time tracker
│   │   │       └── XPathHealer.java             # XPath healing utility
│   │   └── resources/
│   │       └── autoheal.properties              # Configuration file
│   └── test/
│       └── java/com/automation/autoheal/tests/
│           ├── BaseTest.java                    # Base test class
│           └── SampleTest.java                  # Sample tests
├── pom.xml                                      # Maven configuration
├── testng.xml                                   # TestNG suite configuration
├── README.md                                    # Main documentation
├── INTEGRATION_GUIDE.md                         # Integration guide
└── .gitignore                                   # Git ignore file
```

## Key Features

### 1. Auto-Healing XPath
- Automatically detects failed XPath locators
- Analyzes current DOM structure
- Extracts element attributes (id, name, class, text)
- Generates new XPath based on best match
- Caches healed XPaths for performance

### 2. Page Load Time Tracking
- Tracks load time for each page navigation
- Calculates statistics (average, median, min, max)
- Automatically updates explicit wait timeouts
- Logs statistics in Extent Reports

### 3. Extent Reports Integration
- Automatic test reporting
- Screenshot capture on failure
- Detailed logs with healing attempts
- Page load statistics
- Error stack traces

### 4. Seamless Integration
- Works with existing WebDriver code
- Minimal code changes required
- Compatible with Page Object Model
- Supports all WebDriver methods

## Usage Example

```java
// Simple usage - just replace WebDriver with AutoHealWebDriver
AutoHealWebDriver driver = new AutoHealWebDriver(new ChromeDriver());
driver.get("https://example.com");

// XPath will be auto-healed if it fails
WebElement element = driver.findElement(By.xpath("//button[@id='submit']"));

// Explicit wait with auto-updated timeout
WebElement element = driver.waitForElement(By.id("element"));
```

## Configuration

Edit `src/main/resources/autoheal.properties`:
- `autoheal.enabled`: Enable/disable auto-healing
- `autoheal.maxRetryAttempts`: Maximum healing attempts
- `pageLoad.tracking.enabled`: Enable page load tracking
- `pageLoad.defaultTimeout`: Default timeout in milliseconds

## Building and Running

```bash
# Build the project
mvn clean install

# Run tests
mvn test

# Run specific test class
mvn test -Dtest=SampleTest
```

## Reports

Extent Reports are generated in `test-output/` directory after test execution.

## Next Steps

1. Install dependencies: `mvn clean install`
2. Configure `autoheal.properties` as needed
3. Run sample tests: `mvn test`
4. Integrate into your existing project (see INTEGRATION_GUIDE.md)

## Notes

- Ensure ChromeDriver/FirefoxDriver is in PATH or project directory
- TestNG listener must be added to testng.xml for automatic reporting
- XPath healing works best with locators containing identifiable attributes
- Page load tracking requires JavaScript-enabled browsers

