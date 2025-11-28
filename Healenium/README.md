# AutoHeal Framework

An intelligent auto-healing framework for Selenium WebDriver that automatically fixes broken XPath locators, tracks page load times, and integrates seamlessly with Extent Reports.

## Features

1. **Automatic XPath Healing**: Automatically updates failed XPath locators by analyzing the current DOM structure
2. **Page Load Time Tracking**: Tracks page load times and automatically updates explicit wait timeouts
3. **Automatic Failure Logging**: Automatically captures and attaches failure logs and screenshots to Extent Reports
4. **Compatible with Existing Projects**: Works seamlessly with existing Selenium Java TestNG Maven projects

## Technology Stack

- **Selenium**: 3.141.59
- **Java**: 8
- **TestNG**: 7.5
- **Extent Reports**: 4.1.7
- **JSoup**: 1.14.3 (for HTML parsing)
- **Maven**: For dependency management

## Project Structure

```
autoheal-framework/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/automation/autoheal/
│   │   │       ├── core/
│   │   │       │   └── AutoHealWebDriver.java
│   │   │       ├── config/
│   │   │       │   └── AutoHealConfig.java
│   │   │       ├── factory/
│   │   │       │   └── WebDriverFactory.java
│   │   │       ├── listeners/
│   │   │       │   └── TestListener.java
│   │   │       ├── reporting/
│   │   │       │   └── ExtentReportManager.java
│   │   │       └── utils/
│   │   │           ├── PageLoadTracker.java
│   │   │           └── XPathHealer.java
│   │   └── resources/
│   │       └── autoheal.properties
│   └── test/
│       └── java/
│           └── com/automation/autoheal/tests/
│               ├── BaseTest.java
│               └── SampleTest.java
├── pom.xml
├── testng.xml
└── README.md
```

## Installation

1. **Prerequisites**:
   - Java 8 or higher
   - Maven 3.6 or higher
   - ChromeDriver/FirefoxDriver (add to PATH or project)

2. **Clone or download the project**

3. **Build the project**:
   ```bash
   mvn clean install
   ```

## Configuration

Edit `src/main/resources/autoheal.properties` to configure the framework:

```properties
# Enable/disable auto-healing feature
autoheal.enabled=true

# Maximum number of retry attempts for healing broken XPath
autoheal.maxRetryAttempts=3

# Enable/disable page load time tracking
pageLoad.tracking.enabled=true

# Default page load timeout in milliseconds
pageLoad.defaultTimeout=30000
```

## Usage

### Basic Usage

1. **Extend BaseTest** in your test class:
   ```java
   public class MyTest extends BaseTest {
       @Test
       public void myTest() {
           driver.get("https://example.com");
           WebElement element = driver.findElement(By.xpath("//button[@id='submit']"));
           element.click();
       }
   }
   ```

2. **Use AutoHealWebDriver directly**:
   ```java
   WebDriver baseDriver = new ChromeDriver();
   AutoHealWebDriver driver = new AutoHealWebDriver(baseDriver);
   driver.get("https://example.com");
   ```

### Auto-Healing XPath

The framework automatically attempts to heal broken XPath locators:

```java
// If this XPath fails, the framework will try to heal it automatically
WebElement element = driver.findElement(By.xpath("//button[@id='oldId']"));
```

### Page Load Time Tracking

Page load times are automatically tracked and used to update wait timeouts:

```java
driver.get("https://example.com"); // Load time is tracked
WebDriverWait wait = driver.getWait(); // Uses auto-updated timeout
WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("element")));
```

### Explicit Wait with Auto-Updated Timeout

```java
// Uses automatically calculated timeout based on page load times
WebElement element = driver.waitForElement(By.id("element"));
```

## Integration with Existing Projects

To integrate AutoHeal Framework into your existing Selenium project:

1. **Add dependency** to your `pom.xml`:
   ```xml
   <dependency>
       <groupId>com.automation</groupId>
       <artifactId>autoheal-framework</artifactId>
       <version>1.0.0</version>
   </dependency>
   ```

2. **Replace WebDriver with AutoHealWebDriver**:
   ```java
   // Before
   WebDriver driver = new ChromeDriver();
   
   // After
   AutoHealWebDriver driver = new AutoHealWebDriver(new ChromeDriver());
   ```

3. **Add TestListener** to your `testng.xml`:
   ```xml
   <listeners>
       <listener class-name="com.automation.autoheal.listeners.TestListener"/>
   </listeners>
   ```

## Running Tests

Run tests using Maven:
```bash
mvn test
```

Or run specific test class:
```bash
mvn test -Dtest=SampleTest
```

## Reports

Extent Reports are automatically generated in the `test-output` directory after test execution. The report includes:
- Test execution status
- Screenshots on failure
- Page load time statistics
- XPath healing attempts
- Detailed error logs

## How It Works

### XPath Healing Process

1. When an element is not found, the framework captures the exception
2. It extracts attributes (id, name, class, text) from the original XPath
3. It parses the current DOM using JSoup
4. It searches for matching elements using multiple strategies
5. It generates a new XPath for the best matching element
6. It retries the operation with the healed XPath

### Page Load Time Tracking

1. Page load time is tracked for each navigation
2. Statistics (average, median, min, max) are calculated
3. Explicit wait timeouts are automatically updated based on average load time
4. Statistics are logged in the Extent Report

### Failure Reporting

1. TestNG listener captures test failures
2. Screenshots are automatically captured on failure
3. Failure logs are attached to Extent Report
4. XPath healing attempts are logged

## Best Practices

1. **Use descriptive XPath locators** with meaningful attributes (id, name, text)
2. **Let the framework handle timeouts** - don't hardcode wait times
3. **Review Extent Reports** to understand healing attempts and optimize locators
4. **Monitor page load statistics** to identify performance issues

## Limitations

- XPath healing works best with XPath locators that contain identifiable attributes (id, name, class, text)
- Complex XPath expressions with multiple conditions may not always be healed successfully
- Healing requires the element to still exist in the DOM with similar attributes

## Troubleshooting

1. **XPath not healing**: Check if the element still exists in the DOM with similar attributes
2. **Page load tracking not working**: Ensure `pageLoad.tracking.enabled=true` in properties
3. **Reports not generating**: Check `test-output` directory permissions

## License

This project is provided as-is for automation testing purposes.

## Support

For issues or questions, please review the code comments and configuration files.

# My_HealeniumFirst-Project
