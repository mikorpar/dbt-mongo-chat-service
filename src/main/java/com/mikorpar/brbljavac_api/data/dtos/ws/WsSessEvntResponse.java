package com.mikorpar.brbljavac_api.data.dtos.ws;

import com.mikorpar.brbljavac_api.data.ws.WsSessEventType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WsSessEvntResponse {
    private WsSessEventType type;
    private String userId;
    private String username;
}
