package com.mikorpar.brbljavac_api.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class PasswordGenerator {

    private final String lowerChars = "abcdefghijklmnopqrstuvwxyz";
    private final String upperChars = lowerChars.toUpperCase();
    private final String digits = "0123456789";
    private final String specialChars = "<>,.?/{[(}])+_-*&^%$#@!=";
    private final String chars = lowerChars + upperChars + digits + specialChars;
    private final int length = 12;
    private final SecureRandom random = new SecureRandom();

    public String generatePasswd() {
        StringBuilder passwdBuilder = new StringBuilder(length);

        for (int i = 0; i < length - 3; i++) {
            passwdBuilder.append(chars.charAt(random.nextInt(chars.length())));
        }

        passwdBuilder.append(lowerChars.charAt(random.nextInt(lowerChars.length())));
        passwdBuilder.append(upperChars.charAt(random.nextInt(upperChars.length())));
        passwdBuilder.append(digits.charAt(random.nextInt(digits.length())));

        List<String> passwdChars = Arrays.asList(passwdBuilder.toString().split(""));
        Collections.shuffle(passwdChars);

        return String.join("", passwdChars);
    }
}
