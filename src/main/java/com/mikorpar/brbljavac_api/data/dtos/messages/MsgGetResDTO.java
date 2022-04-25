package com.mikorpar.brbljavac_api.data.dtos.messages;

import com.mikorpar.brbljavac_api.data.dtos.users.UserWithoutPasswdDTO;
import lombok.Data;

import java.time.Instant;

@Data
public class MsgGetResDTO {
    private String id;
    private String userId;
    private String text;
    private Instant createdAt;
    private String repliedOn;
    private String fileId;
    private UserWithoutPasswdDTO user;
}
