package com.mikorpar.brbljavac_api.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@Configuration
@RequiredArgsConstructor
public class TokenAuthService {

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long jwtRefreshExpirationMs;

    @Value("${jwt.secret}")
    private String jwtSecret;
    private final String TOKEN_PREFIX = "Bearer ";
    private final UserDetailsService userDetailsService;

    private String generateToken(String username, String requestURL, long jwtExpirationMs) {
        return JWT
                .create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .withIssuer(requestURL)
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    private String generateAccessToken(String username, String requestURL) {
        return generateToken(username, requestURL, jwtExpirationMs);
    }

    private String generateRefreshToken(String username, String requestURL) {
        return generateToken(username, requestURL, jwtRefreshExpirationMs);
    }

    public void generateTokens(String username, String reqURL, HttpServletResponse res) {
        String jwtAccessToken = generateAccessToken(username, reqURL);
        String jwtRefreshToken = generateRefreshToken(username, reqURL);

        res.setHeader("access-token", jwtAccessToken);
        res.setHeader("refresh-token", jwtRefreshToken);
        res.setHeader("token-type", TOKEN_PREFIX.trim());
    }

    public UserDetails getTokenUser(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String authHeaderParam = req.getHeader(AUTHORIZATION);
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtSecret)).build();

        try {
            DecodedJWT decodedJWT = verifier.verify(authHeaderParam.replace(TOKEN_PREFIX, ""));
            return userDetailsService.loadUserByUsername(decodedJWT.getSubject());
        } catch (JWTVerificationException | UsernameNotFoundException ex) {
            addErrorMsgToResponse(res, ex.getMessage());
            return null;
        }
    }

    public Authentication getAuthenticationToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        UserDetails principal = getTokenUser(req, res);
        return principal == null ? null : new UsernamePasswordAuthenticationToken(principal, null, emptyList());
    }

    private void addErrorMsgToResponse(HttpServletResponse res, String errorMessage) throws IOException {
        log.error("Error when verifying JWT token: {}", errorMessage);

        res.setHeader("error", errorMessage);
        res.setStatus(FORBIDDEN.value());
        res.setContentType(APPLICATION_JSON_VALUE);

        Map<String, String> error = new HashMap<>();
        error.put("error_message", errorMessage);

        new ObjectMapper().writeValue(res.getOutputStream(), error);
    }
}
