package com.mikorpar.brbljavac_api.data.dtos.groups;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class GroupUpdateResDTO {
    private String id;
    private String admin;
    private List<String> users;
    private List<String> lastMessageSeeners;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}
