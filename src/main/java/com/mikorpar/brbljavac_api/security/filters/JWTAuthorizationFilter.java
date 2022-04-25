package com.mikorpar.brbljavac_api.security.filters;

import com.mikorpar.brbljavac_api.services.TokenAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final TokenAuthService tokenAuthService;
    private final List<String> allowedFullPaths = Arrays.asList("/login", "/users/token/refresh");
    private final List<String> allowedPartialPaths = Arrays.asList("/users/reg-ver","/users/passwd-reset");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        if(isAuthenticationNeeded(request)){
            Authentication authenticationToken = tokenAuthService.getAuthenticationToken(request, response);
            if (authenticationToken != null) {
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAuthenticationNeeded(HttpServletRequest request) {
        String path = request.getServletPath();
        return !(allowedFullPaths.contains(path) ||
                allowedPartialPaths.stream().anyMatch(path::startsWith) ||
                (request.getMethod().equals("POST") && path.equals("/users")));
    }
}
