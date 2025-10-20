package com.bryan.daft_pinger.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {

    @Value("${daft.pinger.notification.topic}")
    private String notificationTopic;

    private final RestTemplate restTemplate = new RestTemplate();

    public void send(String message) {
        try {
            restTemplate.postForEntity("https://ntfy.sh/" + notificationTopic, message, String.class);
            log.info("Sent notification: {}", message);
        } catch (Exception e) {
            log.error("Failed to send notification", e);
        }
    }
}
