package com.mikorpar.brbljavac_api.data.dtos.users;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class UserWithoutPasswdDTO {

    @Id
    private String id;
    
    @Field("is_activated")
    private boolean isActivated;
    private String email;
    private String username;
}
