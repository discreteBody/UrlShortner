package com.discretebody.urlshortner.controller;

import com.discretebody.urlshortner.dto.UrlShortRequestDto;
import com.discretebody.urlshortner.dto.UrlShortResponseDto;
import com.discretebody.urlshortner.services.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
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
        String originalUrl = shortUrlService.findByShortUrl(shortCode);

        // Redirect to original URL
        URI location = URI.create(originalUrl);
        return ResponseEntity.status(HttpStatus.FOUND).location(location).build();

    }



}
