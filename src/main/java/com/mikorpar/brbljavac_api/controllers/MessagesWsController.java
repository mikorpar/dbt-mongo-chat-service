package com.mikorpar.brbljavac_api.controllers;

import com.mikorpar.brbljavac_api.data.dtos.exceptions.ExceptionMsg;
import com.mikorpar.brbljavac_api.data.dtos.groups.GroupUpdateResDTO;
import com.mikorpar.brbljavac_api.data.dtos.messages.MsgCreateReqDTO;
import com.mikorpar.brbljavac_api.data.dtos.messages.MsgWithUserDTO;
import com.mikorpar.brbljavac_api.data.dtos.messages.WsGroupMessageDTO;
import com.mikorpar.brbljavac_api.data.dtos.ws.WsResponse;
import com.mikorpar.brbljavac_api.data.models.Group;
import com.mikorpar.brbljavac_api.data.models.Message;
import com.mikorpar.brbljavac_api.data.ws.MsgType;
import com.mikorpar.brbljavac_api.exceptions.groups.GroupNotFoundException;
import com.mikorpar.brbljavac_api.exceptions.users.UserNotGroupMemberException;
import com.mikorpar.brbljavac_api.services.GroupService;
import com.mikorpar.brbljavac_api.services.MessageService;
import com.mikorpar.brbljavac_api.utils.DataMapper;
import com.mikorpar.brbljavac_api.utils.GroupWsNotifier;
import com.mikorpar.brbljavac_api.utils.LoggedUserFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@Controller
@Validated
@RequiredArgsConstructor
public class MessagesWsController {

    private final DataMapper dataMapper;
    private final MessageService messageService;
    private final GroupService groupService;
    private final GroupWsNotifier groupWsNotifier;
    private final LoggedUserFetcher userFetcher;

    @MessageMapping("/messages/add/{gid}")
    @SendTo("/topic/messages/{gid}")
    public WsResponse<WsGroupMessageDTO> createMsg(
            @Pattern(regexp = "^[a-f\\d]{24}$", message = "Must consist of 24 hex chars")
            @DestinationVariable String gid,
            @Valid @Payload MsgCreateReqDTO msg, Authentication auth
    ) throws GroupNotFoundException, UserNotGroupMemberException {
        Group group = messageService.addMessageToGroup(msg, gid, userFetcher.getPrincipal(auth).getId());
        Message message = group.getMessages().get(group.getMessages().size() - 1);
        message.setUser(userFetcher.getPrincipal(auth).getUser());

        GroupUpdateResDTO dto = dataMapper.map(group, GroupUpdateResDTO.class);
        List<String> members = groupService.getGroupMemberUsernames(dto.getId());
        groupWsNotifier.sendNotification(dto.getId(), MsgType.UPDATE, dto, members);

        return WsResponse.<WsGroupMessageDTO>builder()
                .type(MsgType.ADD)
                .id(message.getId())
                .content(dataMapper.map(message, WsGroupMessageDTO.class))
                .build();
    }

    @MessageMapping("/messages/delete/{gid}/{id}")
    @SendTo("/topic/messages/{gid}")
    public WsResponse<Object> deleteMsg(
            @Pattern(regexp = "^[a-f\\d]{24}$", message = "Must consist of 24 hex chars")
            @DestinationVariable String gid,
            @DestinationVariable String id, Authentication auth
    ) throws GroupNotFoundException, UserNotGroupMemberException {
        messageService.deleteMessage(id, gid, userFetcher.getPrincipal(auth).getId());
        return WsResponse.builder()
                .type(MsgType.DELETE)
                .id(id)
                .build();
    }

    @SubscribeMapping("/messages/all/{gid}")
    public List<MsgWithUserDTO> sendAllMessages(
            @Pattern(regexp = "^[a-f\\d]{24}$", message = "Must consist of 24 hex chars")
            @DestinationVariable String gid, Authentication auth
    ) throws UserNotGroupMemberException, GroupNotFoundException {
        return dataMapper.mapList(
                messageService.getGroupMessages(gid, userFetcher.getPrincipal(auth).getId()),
                MsgWithUserDTO.class
        );
    }

    @MessageExceptionHandler
    @SendToUser("/messages/exceptions")
    public ExceptionMsg handleExceptions(Exception e) {
        return new ExceptionMsg(e.getMessage());
    }
}
