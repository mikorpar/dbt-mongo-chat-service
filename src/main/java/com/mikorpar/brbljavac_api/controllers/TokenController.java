package com.mikorpar.brbljavac_api.controllers;

import com.mikorpar.brbljavac_api.services.TokenAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final TokenAuthService tokenHandlerService;

    @GetMapping("/refresh")
    public void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        UserDetails user = tokenHandlerService.getTokenUser(req, res);
        String requestUrl = req.getRequestURL().toString();
        if (user != null) tokenHandlerService.generateTokens(user.getUsername(), requestUrl, res);
    }
}
