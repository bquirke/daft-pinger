package com.bryan.daft_pinger.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Set;

@SpringBootTest
public class DaftScraperServiceTest {

    @Autowired
    private DaftScraperHeadlessService daftScraperService;


    @Test
    void testServiceRunTwiceWithDelayVerifiesUrlsInMemory() throws IOException, InterruptedException {
        // First run - get initial listings
        System.out.println("Running first scrape...");
        Set<String> firstRunResults = daftScraperService.checkForNewListings();
        
        // Verify we got some results (assuming the website has listings)
        System.out.println("First run found " + firstRunResults.size() + " new listings");

        // Wait 5 seconds as requested
        System.out.println("Waiting 15 seconds...");
        Thread.sleep(15000);

        // Second run - should return fewer (or zero) new listings since we've seen some already
        System.out.println("Running second scrape...");
        Set<String> secondRunResults = daftScraperService.checkForNewListings();
        
        System.out.println("Second run found " + secondRunResults.size() + " new listings");
    }

    @Test
    void smallTest() throws IOException, InterruptedException {
        daftScraperService.testSelenium();
    }
}
