package com.bryan.daft_pinger.service;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class DaftScraperHeadlessService {

    @Value("${daft.pinger.search}")
    private String daftSearchUrl;
    
    private static final String SEEN_URLS_FILE = "data/seen-urls.txt";

    private Set<String> loadSeenUrlsFromFile() {
        Set<String> seenUrls = new HashSet<>();
        try {
            Path filePath = Paths.get(SEEN_URLS_FILE);
            if (Files.exists(filePath)) {
                log.info("Loading previously seen URLs from {}", SEEN_URLS_FILE);
                
                try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                    String line;
                    int count = 0;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty() && !line.startsWith("#")) { // Skip empty lines and comments (for backward compatibility)
                            seenUrls.add(line);
                            count++;
                        }
                    }
                    log.info("Loaded {} previously seen URLs from file", count);
                }
            } else {
                log.info("No {} file found. Starting with empty seen URLs set.", SEEN_URLS_FILE);
                // Create the data directory if it doesn't exist
                Files.createDirectories(filePath.getParent());
            }
        } catch (IOException e) {
            log.error("Error reading {} file: {}", SEEN_URLS_FILE, e.getMessage());
            log.error("Failed to load seen URLs from file", e);
        }
        return seenUrls;
    }

    private void saveSeenUrlsToFile(Set<String> seenUrls) {
        try {
            Path filePath = Paths.get(SEEN_URLS_FILE);
            
            // Create directories if they don't exist
            Files.createDirectories(filePath.getParent());
            
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                for (String url : seenUrls) {
                    writer.write(url + "\n");
                }
            }
            log.info("Saved {} URLs to {}", seenUrls.size(), SEEN_URLS_FILE);
        } catch (IOException e) {
            log.error("Error writing to {} file: {}", SEEN_URLS_FILE, e.getMessage());
            log.error("Failed to save seen URLs to file", e);
        }
    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        
        // Essential WSL2 Chrome arguments
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-debugging-port=0");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--window-size=1920,1080");
        
        // Anti-detection
        options.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        
        return options;
    }

    public Set<String> checkForNewListings() {
        log.info("Grabbing urls from {}", daftSearchUrl);
        
        // Load seen URLs from file on each call
        Set<String> seen = loadSeenUrlsFromFile();
        
        ChromeOptions options = getChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        Set<String> newListings = new HashSet<>();

        try {
            log.info("Visiting main page first...");
            driver.get("https://www.daft.ie");
            Thread.sleep(3000);
            
            log.info("Navigating to search results...");
            driver.get(daftSearchUrl);
            Thread.sleep(5000);
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            
            try {
                log.info("Waiting for results to load...");
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ul[data-testid='results']")));
                
                List<WebElement> listItems = driver.findElements(By.cssSelector("ul[data-testid='results'] li"));
                log.info("Found {} list items on page", listItems.size());
                
                for (WebElement listItem : listItems) {
                    try {
                        WebElement link = listItem.findElement(By.tagName("a"));
                        String href = link.getDomAttribute("href");
                        
                        if (href != null && !href.isEmpty() && !seen.contains(href)) {
                            newListings.add(href);
                            seen.add(href);
                            log.info("Added new listing: {}", href);
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            } catch (Exception e) {
                log.warn("Could not find results ul, page might be blocked or structure changed");
                String pageSource = driver.getPageSource();
                if (pageSource.contains("403") || pageSource.contains("Forbidden")) {
                    log.warn("403 Forbidden - page is blocked");
                } else {
                    log.warn("Page loaded but structure might have changed");
                    log.warn("Page title: {}", driver.getTitle());
                    log.warn("Page source preview: {}", pageSource.substring(0, Math.min(500, pageSource.length())));
                }
            }

            if (!newListings.isEmpty()) {
                log.info("Found {} new listings!", newListings.size());
            } else {
                log.info("No new listings found.");
            }

        } catch (Exception e) {
            log.error("Error scraping daft.ie: {}", e.getMessage());
            log.error("Exception details", e);
        } finally {
            driver.quit();
        }

        // Save the updated seen URLs back to the file if we found new listings
        if (!newListings.isEmpty()) {
            saveSeenUrlsToFile(seen);
        }

        return newListings;
    }

    public void testSelenium() {
        log.info("=== Starting Chrome Test ===");
        
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--remote-debugging-port=0");
            
            log.info("Creating Chrome driver...");
            WebDriver driver = new ChromeDriver(options);
            
            log.info("Navigating to example.com...");
            driver.get("https://example.com");
            String title = driver.getTitle();
            log.info("Successfully loaded page: {}", title);
            
            driver.quit();
        } catch (Exception e) {
            log.error("Error in test: {}", e.getMessage());
            log.error("Exception details", e);
        }
        
        log.info("=== Chrome Test Completed ===");
    }
}
