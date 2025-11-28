# AutoHeal Framework - Integration Guide

This guide explains how to integrate the AutoHeal Framework into your existing Selenium Java TestNG Maven project.

## Quick Integration Steps

### Step 1: Add Dependency

Add the AutoHeal Framework as a dependency in your existing project's `pom.xml`:

```xml
<dependency>
    <groupId>com.automation</groupId>
    <artifactId>autoheal-framework</artifactId>
    <version>1.0.0</version>
</dependency>
```

Or if you're using this as a library, install it first:
```bash
mvn clean install
```

### Step 2: Update Your Test Base Class

Replace your WebDriver initialization with AutoHealWebDriver:

**Before:**
```java
public class BaseTest {
    protected WebDriver driver;
    
    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
    }
}
```

**After:**
```java
import com.automation.autoheal.core.AutoHealWebDriver;
import com.automation.autoheal.factory.WebDriverFactory;

public class BaseTest {
    protected AutoHealWebDriver driver;
    
    @BeforeMethod
    @Parameters("browser")
    public void setUp(String browser) {
        if (browser == null || browser.isEmpty()) {
            browser = "chrome";
        }
        driver = WebDriverFactory.createDriver(browser);
    }
}
```

### Step 3: Add TestNG Listener

Add the TestListener to your `testng.xml`:

```xml
<suite name="Your Test Suite">
    <listeners>
        <listener class-name="com.automation.autoheal.listeners.TestListener"/>
    </listeners>
    
    <test name="Your Tests">
        <classes>
            <class name="your.package.YourTestClass"/>
        </classes>
    </test>
</suite>
```

### Step 4: Copy Configuration File

Copy `src/main/resources/autoheal.properties` to your project's resources folder, or create it with:

```properties
autoheal.enabled=true
autoheal.maxRetryAttempts=3
pageLoad.tracking.enabled=true
pageLoad.defaultTimeout=30000
```

## Migration Examples

### Example 1: Simple Element Finding

**Before:**
```java
@Test
public void testLogin() {
    driver.get("https://example.com/login");
    WebElement username = driver.findElement(By.xpath("//input[@id='username']"));
    username.sendKeys("testuser");
}
```

**After:**
```java
@Test
public void testLogin() {
    driver.get("https://example.com/login");
    // XPath will be auto-healed if it fails
    WebElement username = driver.findElement(By.xpath("//input[@id='username']"));
    username.sendKeys("testuser");
}
```

No code changes needed! The framework automatically handles healing.

### Example 2: Explicit Waits

**Before:**
```java
@Test
public void testDynamicContent() {
    driver.get("https://example.com");
    WebDriverWait wait = new WebDriverWait(driver, 30);
    WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("dynamic")));
}
```

**After:**
```java
@Test
public void testDynamicContent() {
    driver.get("https://example.com");
    // Timeout is automatically calculated based on page load times
    WebElement element = driver.waitForElement(By.id("dynamic"));
}
```

Or use the auto-updated wait:
```java
WebDriverWait wait = driver.getWait(); // Uses auto-calculated timeout
WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("dynamic")));
```

### Example 3: Page Object Model

**Before:**
```java
public class LoginPage {
    private WebDriver driver;
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }
    
    public void enterUsername(String username) {
        driver.findElement(By.id("username")).sendKeys(username);
    }
}
```

**After:**
```java
import com.automation.autoheal.core.AutoHealWebDriver;

public class LoginPage {
    private AutoHealWebDriver driver;
    
    public LoginPage(AutoHealWebDriver driver) {
        this.driver = driver;
    }
    
    public void enterUsername(String username) {
        // Auto-healing is automatic
        driver.findElement(By.id("username")).sendKeys(username);
    }
}
```

## Benefits After Integration

1. **Automatic XPath Healing**: Broken XPaths are automatically fixed
2. **Smart Timeouts**: Wait times are automatically adjusted based on page load performance
3. **Enhanced Reporting**: Automatic screenshots and detailed logs in Extent Reports
4. **Reduced Maintenance**: Less time spent fixing broken locators

## Configuration Options

Edit `autoheal.properties` to customize behavior:

- `autoheal.enabled`: Enable/disable auto-healing (default: true)
- `autoheal.maxRetryAttempts`: Maximum healing attempts (default: 3)
- `pageLoad.tracking.enabled`: Enable page load tracking (default: true)
- `pageLoad.defaultTimeout`: Default timeout in milliseconds (default: 30000)

## Troubleshooting

### Issue: XPath not healing
**Solution**: Ensure the element still exists in the DOM with similar attributes (id, name, class, text)

### Issue: Reports not generating
**Solution**: Check that TestListener is added to testng.xml and test-output directory has write permissions

### Issue: Timeouts not updating
**Solution**: Verify `pageLoad.tracking.enabled=true` in properties file

## Best Practices

1. **Use descriptive locators**: XPaths with id, name, or text attributes heal better
2. **Review reports**: Check Extent Reports to see healing attempts and optimize locators
3. **Monitor statistics**: Use page load statistics to identify performance issues
4. **Gradual migration**: Start with a few test classes, then expand

## Support

For more information, refer to the main README.md file.

