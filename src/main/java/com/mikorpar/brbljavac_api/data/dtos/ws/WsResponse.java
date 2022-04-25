package com.mikorpar.brbljavac_api.data.dtos.ws;

import com.mikorpar.brbljavac_api.data.ws.MsgType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WsResponse<T> {
    private MsgType type;
    private String id;
    private T content;
}
