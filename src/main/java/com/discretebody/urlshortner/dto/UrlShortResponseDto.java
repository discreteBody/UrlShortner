package com.discretebody.urlshortner.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlShortResponseDto {

    public String shortUrl;
    public String originalUrl;
    public LocalDateTime expirationDate;
    public LocalDateTime creationDate;

}
