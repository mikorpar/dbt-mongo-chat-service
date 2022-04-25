package com.mikorpar.brbljavac_api.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@Document("users")
public class User {

    @Id
    private String id;

    @Field("is_activated")
    private boolean isActivated;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String username;
    private String password;

    @Nullable
    @Field("conf_tokens")
    private List<ConfirmationToken> confTokens;

    @PersistenceConstructor
    public User(String id, boolean isActivated, String email, String username, String password) {
        this.id = id;
        this.isActivated = isActivated;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(
            boolean isActivated,
            String email, String username,
            String password,
            List<ConfirmationToken> confTokens
    ) {
        this.isActivated = isActivated;
        this.email = email;
        this.username = username;
        this.password = password;
        this.confTokens = confTokens;
    }
}
