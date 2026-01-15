package com.discretebody.urlshortner.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final int SHORT_CODE_LENGTH = 6;
    private final SecureRandom random;

    public ShortCodeGenerator(){
        random = new SecureRandom();
    }

    public String generateShortCode() {
        StringBuilder sb = new StringBuilder();

        for(int i=0;i<SHORT_CODE_LENGTH;i++){
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }


}
