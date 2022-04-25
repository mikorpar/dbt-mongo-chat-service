package com.mikorpar.brbljavac_api.data.dtos.groups;

import com.mikorpar.brbljavac_api.data.dtos.messages.MsgWithUserDTO;
import lombok.Data;

import java.util.List;

@Data
public class MessagesAndUsersDTO {
    private List<String> users;
    private List<MsgWithUserDTO> messages;
}
