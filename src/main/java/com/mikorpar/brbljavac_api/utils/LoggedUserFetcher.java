package com.mikorpar.brbljavac_api.utils;

import com.mikorpar.brbljavac_api.security.MyUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LoggedUserFetcher {
    public MyUserPrincipal getPrincipal() {
        return (MyUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public MyUserPrincipal getPrincipal(Authentication authentication) {
        return (MyUserPrincipal) authentication.getPrincipal();
    }
}
