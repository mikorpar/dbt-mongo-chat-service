package com.mikorpar.brbljavac_api.data.dtos.users;

import lombok.Data;

@Data
public class UserRegisterResDTO {
    private String id;
    private boolean isActivated;
    private String email;
    private String username;
}
