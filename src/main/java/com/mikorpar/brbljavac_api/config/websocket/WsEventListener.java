package com.mikorpar.brbljavac_api.config.websocket;

import com.mikorpar.brbljavac_api.data.dtos.ws.WsSessEvntResponse;
import com.mikorpar.brbljavac_api.data.models.User;
import com.mikorpar.brbljavac_api.data.ws.WsSessEventType;
import com.mikorpar.brbljavac_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WsEventListener {

    private final SimpMessagingTemplate wsTemplate;
    private final UserService userService;
    private final String USERS_TOPIC = "/topic/users";

    @EventListener
    public void handleSessionConnectEvent(SessionConnectedEvent event) {
        User user = userService.getUserByUsername(Objects.requireNonNull(event.getUser()).getName());
        WsSessEvntResponse msg = new WsSessEvntResponse(WsSessEventType.CONNECTED, user.getId(), user.getUsername());
        wsTemplate.convertAndSend(USERS_TOPIC, msg);
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event){
        User user = userService.getUserByUsername(Objects.requireNonNull(event.getUser()).getName());
        WsSessEvntResponse msg = new WsSessEvntResponse(WsSessEventType.DISCONNECTED, user.getId(), user.getUsername());
        wsTemplate.convertAndSend(USERS_TOPIC, msg);
    }
}
