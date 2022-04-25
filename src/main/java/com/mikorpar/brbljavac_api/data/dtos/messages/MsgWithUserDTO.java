package com.mikorpar.brbljavac_api.data.dtos.messages;

import com.mikorpar.brbljavac_api.data.dtos.users.UserWithoutPasswdDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Date;

@Data
public class MsgWithUserDTO {

    @Id
    private String id;
    private String text;

    @Field("created_at")
    private Instant createdAt;

    @Field("replied_on")
    private String repliedOn;

    @Field("file_id")
    private String fileId;
    private UserWithoutPasswdDTO user;
}
