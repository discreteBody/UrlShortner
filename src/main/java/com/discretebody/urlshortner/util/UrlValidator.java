package com.discretebody.urlshortner.util;


import com.discretebody.urlshortner.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class UrlValidator {

    private final PingService pingService;

    public UrlValidator(PingService pingService) {
        this.pingService = pingService;
    }


    public void validateOriginalUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new BadRequestException("Original URL cannot be empty");
        }

        try {
            URI uri = new URI(originalUrl.trim());

            // Must have scheme + host
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new BadRequestException("Invalid URL format. Example: https://example.com");
            }

            // Allow only http/https
            String scheme = uri.getScheme().toLowerCase();
            if (!scheme.equals("http") && !scheme.equals("https")) {
                throw new BadRequestException("Only http/https URLs are allowed");
            }
            else{
                pingService.pingOriginalUrl(originalUrl);
            }
        } catch (URISyntaxException e) {
            throw new BadRequestException("Invalid URL format. Example: https://example.com");
        }
    }


}
