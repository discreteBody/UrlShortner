package com.discretebody.urlshortner.services;

import com.discretebody.urlshortner.dto.UrlShortResponseDto;
import com.discretebody.urlshortner.entity.ShortUrl;
import com.discretebody.urlshortner.exception.ResourceNotFoundException;
import com.discretebody.urlshortner.repository.ShortUrlRepository;
import com.discretebody.urlshortner.util.ShortCodeGenerator;
import com.discretebody.urlshortner.util.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final UrlValidator urlValidator;
    private final ModelMapper modelMapper;


    private final StringRedisTemplate redisTemplate;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlShortResponseDto createShortUrl(String originalUrl) {

        // Validate URL format (Syntax  + Ping Original URL if needed)
        urlValidator.validateOriginalUrl(originalUrl);

        String shortCode;

        do{
             shortCode = shortCodeGenerator.generateShortCode();
        }
        while(shortUrlRepository.findByShortCode(shortCode).isPresent());

        String shortUrl = baseUrl + shortCode;

        System.out.println("Short URL: " + shortUrl);

        ShortUrl shortUrlEntity = ShortUrl.builder()
                .originalUrl(originalUrl)
                .shortCode(shortCode)
                .creationDate(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusDays(30)) // Example: 30 days expiration
                .build();

        shortUrlRepository.save(shortUrlEntity);

        UrlShortResponseDto responseDto =  modelMapper.map(shortUrlEntity, UrlShortResponseDto.class);
        responseDto.setShortUrl(shortUrl);

        return responseDto;
    }

    private static final String NOT_FOUND = "__NOT_FOUND__";

    public String findByShortUrl(String shortCode) {

        String key = "url:code:" + shortCode;

        try{
            String cachedUrl = redisTemplate.opsForValue().get(key);

            // Cache Hit
            if (cachedUrl != null) {

                redisTemplate.expire(key, Duration.ofHours(1));

                if (NOT_FOUND.equals(cachedUrl)) {
                    redisTemplate.expire(key, Duration.ofMinutes(1));
                    throw new ResourceNotFoundException("Short URL not found in database.");
                }

                System.out.println("Cache Hit for shortCode: " + shortCode);
                return cachedUrl;
            }
        }
        catch (Exception e){
            System.out.println("Redis unavailable, falling back to Postgres. Reason: " + e.getMessage());
        }



        // Cache Miss -> DB
        Optional<ShortUrl> shortUrl = shortUrlRepository.findByShortCode(shortCode);

        if (shortUrl.isPresent()) {
            String originalUrl = shortUrl.get().getOriginalUrl();
            try{
                redisTemplate.opsForValue().set(key, originalUrl, Duration.ofHours(1)); // Cache for 1 hour
            }
            catch (Exception e){
                System.out.println("Redis unavailable, cannot cache result. Reason: " + e.getMessage());
            }

            return originalUrl;
        }

        // Cache negative result (protect DB)
        try {
            redisTemplate.opsForValue().set(key, NOT_FOUND, Duration.ofMinutes(1));
        }
        catch (Exception e){
            System.out.println("Redis unavailable, cannot cache negative result. Reason: " + e.getMessage());
        }


        throw new ResourceNotFoundException("Short URL not found in database.");
    }



    public List<UrlShortResponseDto> findAll() {
        List<ShortUrl> shortUrls = shortUrlRepository.findAll();
        return shortUrls.stream()
                .map(shortUrl -> {
                    UrlShortResponseDto dto = modelMapper.map(shortUrl, UrlShortResponseDto.class);
                    dto.setShortUrl(baseUrl + shortUrl.getShortCode());
                    return dto;
                })
                .toList();
    }
}
