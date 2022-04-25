package com.mikorpar.brbljavac_api.data.dtos.users;

import com.mikorpar.brbljavac_api.data.models.User;
import lombok.Data;
import org.bson.types.ObjectId;

import java.util.List;

@Data
public class UsersDTO {
    private List<User> users;
}
