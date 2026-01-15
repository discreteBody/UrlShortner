package com.discretebody.urlshortner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlShortRequestDto {

    @NotBlank(message = "originalUrl must not be blank")
    @Size(max = 2048, message = "originalUrl is too long")
    private String originalUrl;
}
