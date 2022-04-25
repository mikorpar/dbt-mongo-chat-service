package com.mikorpar.brbljavac_api.data.dtos.users;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class UserUpdateReqDTO {

    @Pattern(regexp = "^(?!.*[\\.\\-\\_]{2,})^[a-zA-Z0-9\\.\\-\\_]{3,10}$",
            message = "Min 3, max 10 alphanumeric chars, can only contain '.', '_', and '-' special chars")
    private String username;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$",
            message = "Min 8 characters, at leas 1 uppercase, 1 lowercase and 1 number" )
    private String password;
}
