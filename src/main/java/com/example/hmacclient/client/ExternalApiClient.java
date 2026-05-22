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

    private final String serverUrl;

    @Autowired
    public ExternalApiClient(@Qualifier("hmacRestTemplate") RestTemplate restTemplate,
                             @org.springframework.beans.factory.annotation.Value("${api.server.url}") String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    public String fetchData() {
        String url = serverUrl + "/api/v1/data";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            return response.getBody();
        } catch (Exception e) {
            // Handle error appropriately in real application
            throw new RuntimeException("Failed to fetch data from external API", e);
        }
    }
}
