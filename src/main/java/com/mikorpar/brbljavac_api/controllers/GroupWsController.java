package com.mikorpar.brbljavac_api.controllers;

import com.mikorpar.brbljavac_api.data.dtos.exceptions.ExceptionMsg;
import com.mikorpar.brbljavac_api.data.ws.MsgType;
import com.mikorpar.brbljavac_api.data.dtos.groups.*;
import com.mikorpar.brbljavac_api.data.models.Group;
import com.mikorpar.brbljavac_api.exceptions.groups.GroupNotFoundException;
import com.mikorpar.brbljavac_api.exceptions.groups.UserNotGroupAdminException;
import com.mikorpar.brbljavac_api.exceptions.users.UserNotGroupMemberException;
import com.mikorpar.brbljavac_api.services.GroupService;
import com.mikorpar.brbljavac_api.utils.DataMapper;
import com.mikorpar.brbljavac_api.utils.GroupWsNotifier;
import com.mikorpar.brbljavac_api.utils.LoggedUserFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
public class GroupWsController {

    private final DataMapper dataMapper;
    private final GroupService groupService;
    private final GroupWsNotifier groupWsNotifier;
    private final LoggedUserFetcher userFetcher;

    @MessageMapping("/groups/add")
    public void createGroup(@Valid @Payload GroupCreateReqDTO group, Authentication auth)
            throws GroupNotFoundException {
        GroupCreateResDTO dto = dataMapper.map(
                groupService.createGroup(group, userFetcher.getPrincipal(auth).getId()),
                GroupCreateResDTO.class
        );
        List<String> members = groupService.getGroupMemberUsernames(dto.getId());
        groupWsNotifier.sendNotification(dto.getId(), MsgType.ADD, dto, members);
    }

    @MessageMapping("/groups/delete/{id}")
    public void deleteGroup(
            @Pattern(regexp = "^[a-f\\d]{24}$", message = "Must consist of 24 hex chars")
            @DestinationVariable("id") String id, Authentication auth
    ) throws GroupNotFoundException, UserNotGroupAdminException {
        List<String> members = groupService.getGroupMemberUsernames(id);
        groupService.deleteGroup(id, userFetcher.getPrincipal(auth).getId());
        groupWsNotifier.sendNotification(id, MsgType.DELETE, members);
    }

    @MessageMapping("/groups/update/{id}")
    public void updateGroup(
            @Pattern(regexp = "^[a-f\\d]{24}$", message = "Must consist of 24 hex chars")
            @DestinationVariable("id") String id,
            @Valid @Payload GroupUpdateReqDTO group, Authentication auth
    ) throws GroupNotFoundException, UserNotGroupAdminException {
        GroupUpdateResDTO dto = dataMapper.map(
                groupService.updateGroup(id, group.getName(), group.getUsers(), userFetcher.getPrincipal(auth).getId()),
                GroupUpdateResDTO.class
        );
        List<String> members = groupService.getGroupMemberUsernames(dto.getId());
        groupWsNotifier.sendNotification(id, MsgType.UPDATE, dto, members);
    }

    @MessageMapping("/groups/{id}/remove-me")
    public void removeCurrentUser(
            @Pattern(regexp = "^[a-f\\d]{24}$", message = "Must consist of 24 hex chars")
            @DestinationVariable("id") String id, Authentication auth
    ) throws GroupNotFoundException, UserNotGroupMemberException {
        List<String> members = groupService.getGroupMemberUsernames(id);
        Group group = groupService.removeCurrUser(id, userFetcher.getPrincipal(auth).getId());
        if (group == null) {
            groupWsNotifier.sendNotification(id, MsgType.DELETE, members);
        } else {
            groupWsNotifier.sendNotification(id, MsgType.UPDATE, dataMapper.map(group, GroupUpdateResDTO.class), members);
        }
    }

    @SubscribeMapping("/groups/all")
    public List<GroupGetResDTO> getUserGroups(Authentication auth) {
        return dataMapper.mapList(
                groupService.getAllUserGroups(userFetcher.getPrincipal(auth).getId()),
                GroupGetResDTO.class
        );
    }

    @MessageExceptionHandler
    @SendToUser("/groups/exceptions")
    public ExceptionMsg handleExceptions(Exception e) {
        return new ExceptionMsg(e.getMessage());
    }
}
