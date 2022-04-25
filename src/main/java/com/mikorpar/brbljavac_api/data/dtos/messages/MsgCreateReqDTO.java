package com.mikorpar.brbljavac_api.data.dtos.messages;

import lombok.Data;

@Data
public class MsgCreateReqDTO {
   String text;
   String repliedOn;
   String fileId;
}
