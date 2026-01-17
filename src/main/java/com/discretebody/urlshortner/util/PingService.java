package com.discretebody.urlshortner.util;

import com.discretebody.urlshortner.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class PingService {

    private final WebClient webClient;

    public PingService(WebClient webClient) {
        this.webClient = webClient;
    }

    public void pingOriginalUrl(String url) {
        try {
            HttpStatusCode status = webClient
                    .method(HttpMethod.HEAD)
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity()
                    .map(res -> res.getStatusCode())
                    .timeout(Duration.ofSeconds(2))
                    .onErrorResume(ex -> Mono.empty()) // HEAD failed (some sites block it)
                    .block();

            // If HEAD failed, optionally try GET
            if (status == null) {
                status = webClient
                        .get()
                        .uri(url)
                        .retrieve()
                        .toBodilessEntity()
                        .map(res -> res.getStatusCode())
                        .timeout(Duration.ofSeconds(2))
                        .block();
            }

            if (status.is4xxClientError() || status.is5xxServerError()) {
                throw new BadRequestException("URL is not reachable. Status: " + status.value());
            }

        } catch (Exception e) {
            throw new BadRequestException("URL is not reachable or timed out");
        }
    }
}
