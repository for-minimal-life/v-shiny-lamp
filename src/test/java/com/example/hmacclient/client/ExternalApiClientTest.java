package com.example.hmacclient.client;

import com.example.hmacclient.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Application.class, properties = {
        "api.server.url=https://mock.api.example.com",
        "api.secret.key=testSecretKey"
})
public class ExternalApiClientTest {

    @Autowired
    private ExternalApiClient externalApiClient;

    @Autowired
    private RestTemplate hmacRestTemplate;

    @Test
    public void testFetchData() {
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(hmacRestTemplate);

        mockServer.expect(MockRestRequestMatchers.requestTo("https://mock.api.example.com/api/v1/data"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andExpect(request -> {
                    // Assert Date header is present and valid
                    String dateHeader = request.getHeaders().getFirst("Date");
                    assertNotNull(dateHeader);
                    assertTrue(dateHeader.endsWith("GMT"));

                    // Assert Authorization header is present and starts with HMAC-Signature
                    String authHeader = request.getHeaders().getFirst("Authorization");
                    assertNotNull(authHeader);
                    assertTrue(authHeader.startsWith("HMAC-Signature "));
                })
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\": \"success\"}"));

        String response = externalApiClient.fetchData();

        assertEquals("{\"message\": \"success\"}", response);
        mockServer.verify();
    }
}
