package com.mikorpar.brbljavac_api.data.dtos.groups;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Data
public class GroupWithoutMessagesDTO {

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
}
