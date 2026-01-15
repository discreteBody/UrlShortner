package com.discretebody.urlshortner.controller;

import com.discretebody.urlshortner.dto.UrlShortRequestDto;
import com.discretebody.urlshortner.dto.UrlShortResponseDto;
import com.discretebody.urlshortner.entity.ShortUrl;
import com.discretebody.urlshortner.exception.ResourceNotFoundException;
import com.discretebody.urlshortner.services.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController()
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;


    @GetMapping("/")
    public ResponseEntity<List<UrlShortResponseDto>> findAllByShortCode() {
        List<UrlShortResponseDto> responseDtos = shortUrlService.findAll();
        return ResponseEntity.ok(responseDtos);
    }


    @PostMapping("/create")
    public ResponseEntity<UrlShortResponseDto> createShortUrl(@Valid @RequestBody UrlShortRequestDto request) {
        UrlShortResponseDto responseDto = shortUrlService.createShortUrl(request.getOriginalUrl());
        return  ResponseEntity.ok(responseDto);

    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        ShortUrl shortUrl = shortUrlService.findByShortUrl(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found: " + shortCode));

        // Check expiration
        if (shortUrl.getExpirationDate() != null && shortUrl.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Short URL has expired: " + shortCode);
        }

        // Redirect to original URL
        URI location = URI.create(shortUrl.getOriginalUrl());
        return ResponseEntity.status(HttpStatus.FOUND).location(location).build();

    }



}
