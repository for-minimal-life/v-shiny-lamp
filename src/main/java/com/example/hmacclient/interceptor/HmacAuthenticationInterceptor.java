package com.example.hmacclient.interceptor;

import com.example.hmacclient.utils.HmacUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class HmacAuthenticationInterceptor implements ClientHttpRequestInterceptor {

    private final String secretKey;

    public HmacAuthenticationInterceptor(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String method = request.getMethodValue();
        String requestUri = request.getURI().getPath(); // Depending on API needs, could be .toString() or .getPath()
        String dateHeader = HmacUtils.generateImfFixdate();

        String signature = HmacUtils.generateSignature(method, requestUri, dateHeader, secretKey);

        HttpHeaders headers = request.getHeaders();
        headers.set(HttpHeaders.DATE, dateHeader);
        headers.set(HttpHeaders.AUTHORIZATION, "HMAC-Signature " + signature);

        return execution.execute(request, body);
    }
}
