package com.automation.autoheal.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration manager for AutoHeal framework
 */
public class AutoHealConfig {
    
    private static AutoHealConfig instance;
    private Properties properties;
    
    // Default values
    private static final int DEFAULT_MAX_RETRY_ATTEMPTS = 3;
    private static final long DEFAULT_PAGE_LOAD_TIMEOUT = 30000; // 30 seconds
    private static final boolean DEFAULT_AUTO_HEAL_ENABLED = true;
    private static final boolean DEFAULT_PAGE_LOAD_TRACKING_ENABLED = true;
    
    private AutoHealConfig() {
        properties = new Properties();
        loadProperties();
    }
    
    public static synchronized AutoHealConfig getInstance() {
        if (instance == null) {
            instance = new AutoHealConfig();
        }
        return instance;
    }
    
    /**
     * Load properties from config file or use defaults
     */
    private void loadProperties() {
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/autoheal.properties");
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            // Use default values if config file doesn't exist
            setDefaultProperties();
        }
    }
    
    /**
     * Set default properties
     */
    private void setDefaultProperties() {
        properties.setProperty("autoheal.enabled", String.valueOf(DEFAULT_AUTO_HEAL_ENABLED));
        properties.setProperty("autoheal.maxRetryAttempts", String.valueOf(DEFAULT_MAX_RETRY_ATTEMPTS));
        properties.setProperty("pageLoad.tracking.enabled", String.valueOf(DEFAULT_PAGE_LOAD_TRACKING_ENABLED));
        properties.setProperty("pageLoad.defaultTimeout", String.valueOf(DEFAULT_PAGE_LOAD_TIMEOUT));
    }
    
    public boolean isAutoHealEnabled() {
        return Boolean.parseBoolean(properties.getProperty("autoheal.enabled", 
            String.valueOf(DEFAULT_AUTO_HEAL_ENABLED)));
    }
    
    public int getMaxRetryAttempts() {
        return Integer.parseInt(properties.getProperty("autoheal.maxRetryAttempts", 
            String.valueOf(DEFAULT_MAX_RETRY_ATTEMPTS)));
    }
    
    public boolean isPageLoadTrackingEnabled() {
        return Boolean.parseBoolean(properties.getProperty("pageLoad.tracking.enabled", 
            String.valueOf(DEFAULT_PAGE_LOAD_TRACKING_ENABLED)));
    }
    
    public long getDefaultPageLoadTimeout() {
        return Long.parseLong(properties.getProperty("pageLoad.defaultTimeout", 
            String.valueOf(DEFAULT_PAGE_LOAD_TIMEOUT)));
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}

