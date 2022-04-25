package com.mikorpar.brbljavac_api.services;

import com.mikorpar.brbljavac_api.data.models.ConfirmationToken;
import com.mikorpar.brbljavac_api.exceptions.tokens.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {

    @Value("#{${conf-token.expiration-time} * 60}")
    private int tokenExpirationTime;

    public ConfirmationToken generateConfToken() {
        Instant instant = Instant.now();
        return new ConfirmationToken(
                UUID.randomUUID().toString(),
                instant,
                instant.plusSeconds(tokenExpirationTime)
        );
    }

    public void confirmToken(ConfirmationToken confToken) throws TokenExpiredException {
        if (Instant.now().isAfter(confToken.getExpiresAt())) {
            throw new TokenExpiredException(String.format("Token '%s' expired", confToken.getToken()));
        }
        confToken.setConfirmedAt(Instant.now());
    }
}
