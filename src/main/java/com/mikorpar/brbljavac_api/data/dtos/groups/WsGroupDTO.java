package com.mikorpar.brbljavac_api.data.dtos.groups;

import com.mikorpar.brbljavac_api.data.models.Message;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class WsGroupDTO {
    private String id;
    private String admin;
    private List<String> users;
    private List<String> lastMessageSeeners;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private List<Message> messages;
}
