package com.bryan.daft_pinger.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class DaftScraperService {

    private static final String URL = "https://www.daft.ie/property-for-rent/ireland?location=dublin-8-dublin&location=dublin-7-dublin&adState=published&numBeds_from=3&numBeds_to=3&sort=publishDateDesc";
    private final Set<String> seen = new HashSet<>();

    public Set<String> checkForNewListings() throws IOException {
        log.info("Gradding urls from {}", URL);
        
        // Configure jsoup to mimic a real browser
        Document doc = Jsoup.connect(URL)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .timeout(10000) // 10 second timeout
                .get();
        
        // Find the results ul element and then get all li items within it
        Elements listItems = doc.select("ul[data-testid='results'] li");

        Set<String> newListings = new HashSet<>();
        for (var listItem : listItems) {
            // Find the anchor tag within each li
            var link = listItem.selectFirst("a");
            if (link != null) {
                String href = link.attr("href");
                if (href != null && !href.isEmpty() && !seen.contains(href)) {
                    newListings.add(href);
                    seen.add(href);
                }
            }
        }

        if (!newListings.isEmpty()) {
            log.info("Found {} new listings!", newListings.size());
        }

        return newListings;
    }
}

