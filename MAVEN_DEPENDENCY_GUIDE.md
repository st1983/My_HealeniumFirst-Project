# Maven Dependency Guide for AutoHeal Framework

This guide explains how to build, install, and use the AutoHeal Framework as a Maven dependency in other projects.

## Table of Contents
1. [Building the Project](#building-the-project)
2. [Installing to Local Maven Repository](#installing-to-local-maven-repository)
3. [Using as Dependency in Other Projects](#using-as-dependency-in-other-projects)
4. [Deploying to Remote Repository](#deploying-to-remote-repository)
5. [Troubleshooting](#troubleshooting)

## Building the Project

### Step 1: Clean and Compile
```bash
mvn clean compile
```

### Step 2: Run Tests (Optional)
```bash
mvn test
```

### Step 3: Package the JAR
```bash
mvn package
```

This will create:
- `target/autoheal-framework-1.0.0.jar` - Main library JAR
- `target/autoheal-framework-1.0.0-sources.jar` - Source code JAR
- `target/autoheal-framework-1.0.0-javadoc.jar` - JavaDoc JAR

## Installing to Local Maven Repository

To make the library available for other projects on your machine, install it to your local Maven repository:

```bash
mvn clean install
```

This command will:
1. Clean previous builds
2. Compile the source code
3. Run tests
4. Package the JAR files
5. Install to local repository: `~/.m2/repository/com/automation/autoheal-framework/1.0.0/`

**Note:** On Windows, the local repository is typically at:
`C:\Users\<YourUsername>\.m2\repository\com\automation\autoheal-framework\1.0.0\`

### Install Without Running Tests
If you want to skip tests during installation:
```bash
mvn clean install -DskipTests
```

## Using as Dependency in Other Projects

Once installed to your local repository, you can use it in other Maven projects.

### Step 1: Add Dependency to pom.xml

Add the following dependency to your project's `pom.xml`:

```xml
<dependencies>
    <!-- AutoHeal Framework -->
    <dependency>
        <groupId>com.automation</groupId>
        <artifactId>autoheal-framework</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- Required Dependencies (if not already present) -->
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>3.141.59</version>
    </dependency>
    
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>7.5</version>
    </dependency>
    
    <dependency>
        <groupId>com.aventstack</groupId>
        <artifactId>extentreports</artifactId>
        <version>4.1.7</version>
    </dependency>
</dependencies>
```

### Step 2: Use in Your Code

```java
import com.automation.autoheal.core.AutoHealWebDriver;
import com.automation.autoheal.factory.WebDriverFactory;

public class MyTest {
    @Test
    public void myTest() {
        // Create AutoHeal WebDriver
        AutoHealWebDriver driver = WebDriverFactory.createDriver("chrome");
        
        // Use it like regular WebDriver
        driver.get("https://example.com");
        WebElement element = driver.findElement(By.id("element"));
        
        driver.quit();
    }
}
```

## Deploying to Remote Repository

### Option 1: Deploy to Maven Central (Recommended for Public Use)

1. **Sign up for Sonatype OSSRH**
   - Create account at https://issues.sonatype.org/
   - Create a new project ticket

2. **Configure GPG Signing**
   ```bash
   # Install GPG
   # Generate key pair
   gpg --gen-key
   ```

3. **Update pom.xml with Distribution Management**
   ```xml
   <distributionManagement>
       <snapshotRepository>
           <id>ossrh</id>
           <url>https://oss.sonatype.org/content/repositories/snapshots</url>
       </snapshotRepository>
       <repository>
           <id>ossrh</id>
           <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
       </repository>
   </distributionManagement>
   ```

4. **Deploy**
   ```bash
   mvn clean deploy
   ```

### Option 2: Deploy to Local/Company Repository

If you have a local Maven repository (Nexus, Artifactory, etc.):

1. **Add Repository Configuration to pom.xml**
   ```xml
   <distributionManagement>
       <repository>
           <id>company-repo</id>
           <url>http://your-repo-url/repository/maven-releases/</url>
       </repository>
       <snapshotRepository>
           <id>company-repo</id>
           <url>http://your-repo-url/repository/maven-snapshots/</url>
       </snapshotRepository>
   </distributionManagement>
   ```

2. **Configure settings.xml**
   Add to `~/.m2/settings.xml`:
   ```xml
   <settings>
       <servers>
           <server>
               <id>company-repo</id>
               <username>your-username</username>
               <password>your-password</password>
               </server>
       </servers>
   </settings>
   ```

3. **Deploy**
   ```bash
   mvn clean deploy
   ```

### Option 3: Install to Project Repository

You can also install the JAR directly to a project's local repository:

```bash
mvn install:install-file \
  -Dfile=target/autoheal-framework-1.0.0.jar \
  -DgroupId=com.automation \
  -DartifactId=autoheal-framework \
  -Dversion=1.0.0 \
  -Dpackaging=jar
```

## Complete Example: Using in Another Project

### Project Structure
```
my-test-project/
├── pom.xml
└── src/
    └── test/
        └── java/
            └── com/
                └── mycompany/
                    └── tests/
                        └── MyTest.java
```

### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.mycompany</groupId>
    <artifactId>my-test-project</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- AutoHeal Framework -->
        <dependency>
            <groupId>com.automation</groupId>
            <artifactId>autoheal-framework</artifactId>
            <version>1.0.0</version>
        </dependency>
        
        <!-- Selenium -->
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>3.141.59</version>
        </dependency>
        
        <!-- TestNG -->
        <dependency>
            <groupId>org.testng</groupId>
            <artifactId>testng</artifactId>
            <version>7.5</version>
        </dependency>
    </dependencies>
</project>
```

### MyTest.java
```java
package com.mycompany.tests;

import com.automation.autoheal.core.AutoHealWebDriver;
import com.automation.autoheal.factory.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MyTest {
    private AutoHealWebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = WebDriverFactory.createDriver("chrome");
    }

    @Test
    public void testExample() {
        driver.get("https://example.com");
        WebElement element = driver.findElement(By.xpath("//h1"));
        // XPath will be auto-healed if it fails!
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

## Troubleshooting

### Issue: Dependency Not Found
**Error:** `Could not find artifact com.automation:autoheal-framework:jar:1.0.0`

**Solution:**
1. Ensure you've run `mvn clean install` in the AutoHeal project
2. Check local repository: `~/.m2/repository/com/automation/autoheal-framework/1.0.0/`
3. Verify the JAR file exists in that directory

### Issue: ClassNotFoundException
**Error:** `java.lang.ClassNotFoundException: com.automation.autoheal.core.AutoHealWebDriver`

**Solution:**
1. Clean and rebuild: `mvn clean install`
2. Refresh Maven dependencies in your IDE
3. Ensure all transitive dependencies are included

### Issue: Version Conflicts
**Error:** Dependency version conflicts

**Solution:**
Use Maven's dependency management to override versions:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>3.141.59</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## Quick Reference

### Build Commands
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package JAR
mvn package

# Install to local repository
mvn clean install

# Install without tests
mvn clean install -DskipTests

# Deploy to remote repository
mvn clean deploy
```

### Maven Coordinates
- **GroupId:** `com.automation`
- **ArtifactId:** `autoheal-framework`
- **Version:** `1.0.0`
- **Packaging:** `jar`

### Dependency Declaration
```xml
<dependency>
    <groupId>com.automation</groupId>
    <artifactId>autoheal-framework</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Next Steps

1. Build and install the project: `mvn clean install`
2. Create a test project and add the dependency
3. Start using AutoHeal Framework in your tests!

For more information, see:
- [README.md](README.md) - Framework overview
- [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) - Integration instructions

