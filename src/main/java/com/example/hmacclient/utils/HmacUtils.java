package com.example.hmacclient.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;

public class HmacUtils {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    // IMF-fixdate format (e.g. Fri, 22 May 2026 13:20:53 GMT)
    private static final DateTimeFormatter IMF_FIXDATE_FORMATTER = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US)
            .withZone(ZoneId.of("GMT"));

    /**
     * Generates current date in IMF-fixdate format.
     */
    public static String generateImfFixdate() {
        return ZonedDateTime.now(ZoneId.of("GMT")).format(IMF_FIXDATE_FORMATTER);
    }

    /**
     * Generates HMAC-SHA256 signature encoded in Base64.
     *
     * @param method     HTTP Method
     * @param requestUri Request URI
     * @param dateHeader Date header value
     * @param secretKey  Secret Key for HMAC
     * @return Base64 encoded HMAC-SHA256 signature
     */
    public static String generateSignature(String method, String requestUri, String dateHeader, String secretKey) {
        try {
            String signingString = method + "\n" + requestUri + "\n" + dateHeader;

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKeySpec);

            byte[] hmacBytes = mac.doFinal(signingString.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC-SHA256 signature", e);
        }
    }
}
