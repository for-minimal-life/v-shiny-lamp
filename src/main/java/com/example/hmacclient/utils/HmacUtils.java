package com.example.hmacclient.utils;

import org.apache.commons.codec.digest.HmacAlgorithms;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;

public class HmacUtils {

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

            org.apache.commons.codec.digest.HmacUtils hm256 = new org.apache.commons.codec.digest.HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey);
            byte[] hmacBytes = hm256.hmac(signingString.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC-SHA256 signature", e);
        }
    }
}
