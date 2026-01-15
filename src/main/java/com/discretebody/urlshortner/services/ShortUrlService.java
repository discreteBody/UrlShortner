package com.discretebody.urlshortner.services;

import com.discretebody.urlshortner.dto.UrlShortResponseDto;
import com.discretebody.urlshortner.entity.ShortUrl;
import com.discretebody.urlshortner.repository.ShortUrlRepository;
import com.discretebody.urlshortner.util.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final ModelMapper modelMapper;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlShortResponseDto createShortUrl(String originalUrl) {

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

    public Optional<ShortUrl> findByShortUrl(String shortUrl) {
        return shortUrlRepository.findByShortCode(shortUrl);
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
