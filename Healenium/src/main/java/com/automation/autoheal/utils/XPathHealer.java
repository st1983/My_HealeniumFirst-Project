package com.automation.autoheal.utils;

import com.automation.autoheal.config.AutoHealConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XPath healing utility that attempts to regenerate broken XPath locators
 * by analyzing the current DOM structure
 */
public class XPathHealer {
    
    private WebDriver driver;
    private AutoHealConfig config;
    private Map<String, String> healedXPaths; // Cache for healed XPaths
    
    public XPathHealer(WebDriver driver, AutoHealConfig config) {
        this.driver = driver;
        this.config = config;
        this.healedXPaths = new HashMap<>();
    }
    
    /**
     * Attempt to heal a broken XPath locator
     */
    public By healXPath(By originalLocator) {
        if (!isXPathLocator(originalLocator)) {
            return null;
        }
        
        String originalXPath = originalLocator.toString().replace("By.xpath: ", "");
        
        // Check cache first
        if (healedXPaths.containsKey(originalXPath)) {
            String cachedXPath = healedXPaths.get(originalXPath);
            try {
                driver.findElement(By.xpath(cachedXPath));
                return By.xpath(cachedXPath);
            } catch (Exception e) {
                // Cached XPath is also broken, remove from cache
                healedXPaths.remove(originalXPath);
            }
        }
        
        try {
            // Get current DOM
            String pageSource = driver.getPageSource();
            Document doc = Jsoup.parse(pageSource);
            
            // Extract element attributes from original XPath
            XPathAttributes attributes = extractAttributesFromXPath(originalXPath);
            
            // Try to find element using different strategies
            By healedLocator = tryHealingStrategies(doc, attributes, originalXPath);
            
            if (healedLocator != null) {
                // Verify the healed locator works
                try {
                    driver.findElement(healedLocator);
                    // Cache the healed XPath
                    healedXPaths.put(originalXPath, healedLocator.toString().replace("By.xpath: ", ""));
                    return healedLocator;
                } catch (Exception e) {
                    // Healed locator doesn't work
                    return null;
                }
            }
        } catch (Exception e) {
            // Healing failed
            return null;
        }
        
        return null;
    }
    
    /**
     * Extract attributes from original XPath for matching
     */
    private XPathAttributes extractAttributesFromXPath(String xPath) {
        XPathAttributes attributes = new XPathAttributes();
        
        // Extract text content
        Pattern textPattern = Pattern.compile("text\\(\\)\\s*=\\s*['\"]([^'\"]+)['\"]", Pattern.CASE_INSENSITIVE);
        Matcher textMatcher = textPattern.matcher(xPath);
        if (textMatcher.find()) {
            attributes.text = textMatcher.group(1);
        }
        
        // Extract id attribute
        Pattern idPattern = Pattern.compile("@id\\s*=\\s*['\"]([^'\"]+)['\"]", Pattern.CASE_INSENSITIVE);
        Matcher idMatcher = idPattern.matcher(xPath);
        if (idMatcher.find()) {
            attributes.id = idMatcher.group(1);
        }
        
        // Extract class attribute
        Pattern classPattern = Pattern.compile("@class\\s*=\\s*['\"]([^'\"]+)['\"]", Pattern.CASE_INSENSITIVE);
        Matcher classMatcher = classPattern.matcher(xPath);
        if (classMatcher.find()) {
            attributes.className = classMatcher.group(1);
        }
        
        // Extract name attribute
        Pattern namePattern = Pattern.compile("@name\\s*=\\s*['\"]([^'\"]+)['\"]", Pattern.CASE_INSENSITIVE);
        Matcher nameMatcher = namePattern.matcher(xPath);
        if (nameMatcher.find()) {
            attributes.name = nameMatcher.group(1);
        }
        
        // Extract tag name (last part of XPath)
        String[] parts = xPath.split("/");
        if (parts.length > 0) {
            String lastPart = parts[parts.length - 1];
            if (!lastPart.contains("@") && !lastPart.contains("text()")) {
                attributes.tagName = lastPart.replaceAll("[\\[\\]()]", "");
            }
        }
        
        return attributes;
    }
    
    /**
     * Try different healing strategies
     */
    private By tryHealingStrategies(Document doc, XPathAttributes attributes, String originalXPath) {
        List<Element> candidates = new ArrayList<>();
        
        // Strategy 1: Find by ID
        if (attributes.id != null && !attributes.id.isEmpty()) {
            Element element = doc.getElementById(attributes.id);
            if (element != null) {
                candidates.add(element);
            }
        }
        
        // Strategy 2: Find by text content
        if (attributes.text != null && !attributes.text.isEmpty()) {
            Elements elements = doc.getElementsContainingText(attributes.text);
            candidates.addAll(elements);
        }
        
        // Strategy 3: Find by class name
        if (attributes.className != null && !attributes.className.isEmpty()) {
            Elements elements = doc.getElementsByClass(attributes.className);
            candidates.addAll(elements);
        }
        
        // Strategy 4: Find by name attribute
        if (attributes.name != null && !attributes.name.isEmpty()) {
            Elements elements = doc.getElementsByAttributeValue("name", attributes.name);
            candidates.addAll(elements);
        }
        
        // Strategy 5: Find by tag name
        if (attributes.tagName != null && !attributes.tagName.isEmpty()) {
            Elements elements = doc.getElementsByTag(attributes.tagName);
            candidates.addAll(elements);
        }
        
        // If we found candidates, generate XPath for the first unique one
        if (!candidates.isEmpty()) {
            Element bestMatch = findBestMatch(candidates, attributes);
            if (bestMatch != null) {
                String newXPath = generateXPath(bestMatch);
                return By.xpath(newXPath);
            }
        }
        
        return null;
    }
    
    /**
     * Find the best matching element from candidates
     */
    private Element findBestMatch(List<Element> candidates, XPathAttributes attributes) {
        if (candidates.isEmpty()) {
            return null;
        }
        
        // Score each candidate based on attribute matches
        Element bestMatch = null;
        int bestScore = 0;
        
        for (Element candidate : candidates) {
            int score = 0;
            
            if (attributes.id != null && attributes.id.equals(candidate.id())) {
                score += 10;
            }
            if (attributes.name != null && attributes.name.equals(candidate.attr("name"))) {
                score += 8;
            }
            if (attributes.className != null && candidate.hasClass(attributes.className)) {
                score += 6;
            }
            if (attributes.text != null && candidate.text().contains(attributes.text)) {
                score += 5;
            }
            if (attributes.tagName != null && attributes.tagName.equals(candidate.tagName())) {
                score += 3;
            }
            
            if (score > bestScore) {
                bestScore = score;
                bestMatch = candidate;
            }
        }
        
        return bestMatch;
    }
    
    /**
     * Generate XPath for an element
     */
    private String generateXPath(Element element) {
        List<String> path = new ArrayList<>();
        Element current = element;
        
        while (current != null && !current.tagName().equals("html")) {
            String tagName = current.tagName();
            String id = current.id();
            String className = current.className();
            String name = current.attr("name");
            
            // Build XPath segment
            StringBuilder segment = new StringBuilder(tagName);
            
            // Prefer ID if available
            if (id != null && !id.isEmpty()) {
                segment.append("[@id='").append(id).append("']");
            } else if (name != null && !name.isEmpty()) {
                segment.append("[@name='").append(name).append("']");
            } else if (className != null && !className.isEmpty()) {
                String firstClass = className.split("\\s+")[0];
                segment.append("[contains(@class,'").append(firstClass).append("')]");
            } else {
                // Add index if needed
                Element parent = current.parent();
                if (parent != null) {
                    Elements siblings = parent.children();
                    int index = 1;
                    for (int i = 0; i < siblings.size(); i++) {
                        if (siblings.get(i).equals(current)) {
                            index = i + 1;
                            break;
                        }
                    }
                    if (siblings.size() > 1) {
                        segment.append("[").append(index).append("]");
                    }
                }
            }
            
            path.add(0, segment.toString());
            current = current.parent();
        }
        
        return "/" + String.join("/", path);
    }
    
    /**
     * Inner class to hold XPath attributes
     */
    private static class XPathAttributes {
        String id;
        String name;
        String className;
        String text;
        String tagName;
    }
    
    /**
     * Clear cache
     */
    public void clearCache() {
        healedXPaths.clear();
    }
    
    /**
     * Check if a locator is an XPath locator
     */
    private boolean isXPathLocator(By locator) {
        return locator.toString().startsWith("By.xpath:");
    }
}

