package com.mikorpar.brbljavac_api.data.dtos.messages;

import lombok.Data;

import java.time.Instant;

@Data
public class MsgUpdateReqDTO {
    private String id;
    private String userId;
    private String text;
    private Instant createdAt;
    private String repliedOn;
    private String fileId;
}
