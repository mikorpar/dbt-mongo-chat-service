package com.mikorpar.brbljavac_api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserWsController {

    private final SimpUserRegistry userWsRegistry;

    @SubscribeMapping("/users/online")
    public List<String> getConnectedUsers() {
        Set<SimpUser> users = Objects.requireNonNull(userWsRegistry).getUsers();
        return users.stream().map(SimpUser::getName).collect(Collectors.toList());
    }
}
