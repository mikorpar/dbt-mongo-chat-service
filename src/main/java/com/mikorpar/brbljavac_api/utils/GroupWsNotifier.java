package com.mikorpar.brbljavac_api.utils;

import com.mikorpar.brbljavac_api.data.dtos.ws.WsResponse;
import com.mikorpar.brbljavac_api.data.ws.MsgType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupWsNotifier {

    private final SimpMessagingTemplate wsTemplate;
    private final String GROUPS_WS_TOPIC = "/topic/groups";

    public <T> void sendNotification(String id, MsgType type, T content, List<String> usernames) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(usernames.get(0));
        headerAccessor.setLeaveMutable(true);

        WsResponse<T> response = WsResponse.<T>builder()
                .type(type)
                .id(id)
                .content(content)
                .build();

        usernames.forEach(username ->
                wsTemplate.convertAndSendToUser(username, GROUPS_WS_TOPIC, response, headerAccessor.getMessageHeaders())
        );
    }

    public void sendNotification(String id, MsgType type, List<String> usernames) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(usernames.get(0));
        headerAccessor.setLeaveMutable(true);

        WsResponse<Object> response = WsResponse.builder()
                .type(type)
                .id(id)
                .build();

        usernames.forEach(username ->
                wsTemplate.convertAndSendToUser(username, GROUPS_WS_TOPIC, response, headerAccessor.getMessageHeaders())
        );
    }
}
