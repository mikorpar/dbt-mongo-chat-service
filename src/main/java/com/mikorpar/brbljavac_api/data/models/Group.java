package com.mikorpar.brbljavac_api.data.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Document("groups")
public class Group {

    @Id
    private String id;
    private String admin;
    private List<String> users;

    @Field(name = "last_message_seeners")
    private List<String> lastMessageSeeners;
    private String name;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;
    private List<Message> messages;

    public Group(
         String admin,
         List<String> users,
         List<String> lastMessageSeeners,
         String name,
         Instant createdAt,
         Instant updatedAt,
         List<Message> messages
    ) {
        this.admin = admin;
        this.users = users;
        this.lastMessageSeeners = lastMessageSeeners;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.messages = messages;
    }
}
