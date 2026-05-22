package com.example.hmacclient.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalApiClient {

    private final RestTemplate restTemplate;

    @Autowired
    public ExternalApiClient(@Qualifier("hmacRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchData() {
        String url = "https://api.example.com/api/v1/data";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            return response.getBody();
        } catch (Exception e) {
            // Handle error appropriately in real application
            throw new RuntimeException("Failed to fetch data from external API", e);
        }
    }
}
