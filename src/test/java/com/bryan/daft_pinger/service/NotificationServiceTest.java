package com.bryan.daft_pinger.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Test
    public void testSendHelloWorldMessage() {
        System.out.println("=== Testing NotificationService with Hello World ===");
        
        String helloWorldMessage = "Hello World! This is a test notification from the integration test.";
        
        System.out.println("Sending message: " + helloWorldMessage);
        
        // Send the hello world message
        notificationService.send(helloWorldMessage);
        
        System.out.println("Message sent successfully!");
        System.out.println("Check your ntfy.sh topic 'yupD7Z6Hxo1V1Th2' to see if the message arrived.");
        
        // Add a small delay to ensure the message is sent
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("=== NotificationService Test Completed ===");
    }

    @Test
    public void testSendMultipleMessages() {
        System.out.println("=== Testing Multiple Notifications ===");
        
        String[] testMessages = {
            "Hello World - Message 1",
            "Hello World - Message 2", 
            "Hello World - Message 3"
        };
        
        for (int i = 0; i < testMessages.length; i++) {
            System.out.println("Sending message " + (i + 1) + ": " + testMessages[i]);
            //notificationService.send(testMessages[i]);
            
            // Small delay between messages
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("All messages sent! Check your ntfy.sh notifications.");
        System.out.println("=== Multiple Notifications Test Completed ===");
    }
}