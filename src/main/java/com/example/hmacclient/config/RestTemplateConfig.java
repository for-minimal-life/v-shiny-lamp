package com.example.hmacclient.config;

import com.example.hmacclient.interceptor.HmacAuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${api.secret.key:defaultSecretKey}")
    private String secretKey;

    @Bean
    public RestTemplate hmacRestTemplate(RestTemplateBuilder builder) {
        return builder
                .additionalInterceptors(new HmacAuthenticationInterceptor(secretKey))
                .build();
    }
}
