package com.mikorpar.brbljavac_api.data.dtos.users;

import lombok.Data;

@Data
public class UserGetResDTO {
    private String id;
    private boolean isActivated;
    private String email;
    private String username;
}