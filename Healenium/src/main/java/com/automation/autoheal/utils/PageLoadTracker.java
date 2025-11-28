package com.automation.autoheal.utils;

import com.automation.autoheal.config.AutoHealConfig;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Tracks page load times and calculates statistics for automatic timeout updates
 */
public class PageLoadTracker {
    
    private WebDriver driver;
    private long startTime;
    private List<Long> loadTimes;
    private Map<String, Long> pageLoadTimes; // URL -> load time mapping
    private AutoHealConfig config;
    
    public PageLoadTracker(WebDriver driver) {
        this.driver = driver;
        this.loadTimes = new ArrayList<>();
        this.pageLoadTimes = new ConcurrentHashMap<>();
        this.config = AutoHealConfig.getInstance();
    }
    
    /**
     * Start tracking page load time
     */
    public void startTracking() {
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Stop tracking and record page load time
     */
    public long stopTracking() {
        if (startTime == 0) {
            return 0;
        }
        
        long loadTime = System.currentTimeMillis() - startTime;
        
        // Wait for page to be fully loaded using JavaScript
        try {
            if (driver instanceof JavascriptExecutor) {
                ((JavascriptExecutor) driver).executeScript(
                    "return document.readyState"
                ).equals("complete");
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
        
        loadTimes.add(loadTime);
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl != null) {
            pageLoadTimes.put(currentUrl, loadTime);
        }
        
        startTime = 0;
        return loadTime;
    }
    
    /**
     * Get average page load time
     */
    public long getAverageLoadTime() {
        if (loadTimes.isEmpty()) {
            return config.getDefaultPageLoadTimeout();
        }
        
        long sum = 0;
        for (Long time : loadTimes) {
            sum += time;
        }
        return sum / loadTimes.size();
    }
    
    /**
     * Get median page load time
     */
    public long getMedianLoadTime() {
        if (loadTimes.isEmpty()) {
            return config.getDefaultPageLoadTimeout();
        }
        
        List<Long> sorted = new ArrayList<>(loadTimes);
        sorted.sort(Long::compareTo);
        
        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2;
        } else {
            return sorted.get(size / 2);
        }
    }
    
    /**
     * Get maximum page load time
     */
    public long getMaxLoadTime() {
        if (loadTimes.isEmpty()) {
            return config.getDefaultPageLoadTimeout();
        }
        
        return loadTimes.stream().max(Long::compareTo).orElse(config.getDefaultPageLoadTimeout());
    }
    
    /**
     * Get minimum page load time
     */
    public long getMinLoadTime() {
        if (loadTimes.isEmpty()) {
            return config.getDefaultPageLoadTimeout();
        }
        
        return loadTimes.stream().min(Long::compareTo).orElse(config.getDefaultPageLoadTimeout());
    }
    
    /**
     * Get load time for specific URL
     */
    public Long getLoadTimeForUrl(String url) {
        return pageLoadTimes.get(url);
    }
    
    /**
     * Get all recorded load times
     */
    public List<Long> getAllLoadTimes() {
        return new ArrayList<>(loadTimes);
    }
    
    /**
     * Get statistics as a map
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("average", getAverageLoadTime());
        stats.put("median", getMedianLoadTime());
        stats.put("max", getMaxLoadTime());
        stats.put("min", getMinLoadTime());
        stats.put("count", loadTimes.size());
        stats.put("pageLoadTimes", new ConcurrentHashMap<>(pageLoadTimes));
        return stats;
    }
    
    /**
     * Save statistics to file (optional implementation)
     */
    public void saveStatistics() {
        // Can be extended to save to JSON/XML file
        Map<String, Object> stats = getStatistics();
        // Implementation for saving to file can be added here
    }
    
    /**
     * Reset all statistics
     */
    public void reset() {
        loadTimes.clear();
        pageLoadTimes.clear();
        startTime = 0;
    }
}

