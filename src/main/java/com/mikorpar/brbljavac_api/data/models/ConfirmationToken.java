package com.mikorpar.brbljavac_api.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
public class ConfirmationToken {

    @Id
    private String id;

    @Indexed
    private String token;

    @Field("created_at")
    private Instant createdAt;

    @Field("expires_at")
    private Instant expiresAt;

    @Field("confirmed_at")
    private Instant confirmedAt;

    public ConfirmationToken(String token, Instant createdAt, Instant expiresAt) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
}
