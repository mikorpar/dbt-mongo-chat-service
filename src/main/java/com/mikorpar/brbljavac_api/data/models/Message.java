package com.mikorpar.brbljavac_api.data.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@NoArgsConstructor
public class Message {

    @Id
    private String id;

    @Field(name = "user_id")
    private String userId;
    private String text;

    @Field("created_at")
    private Instant createdAt;

    @Field(name = "replied_on")
    private String repliedOn;

    @Field("file_id")
    private String fileId;
    private User user;

    public Message(String userId, String text, Instant createdAt, String repliedOn, String fileId) {
        this.userId = userId;
        this.text = text;
        this.createdAt = createdAt;
        this.repliedOn = repliedOn;
        this.fileId = fileId;
    }
}
