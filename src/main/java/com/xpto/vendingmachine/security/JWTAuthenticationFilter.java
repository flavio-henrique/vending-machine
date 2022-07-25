package com.xpto.vendingmachine.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpto.vendingmachine.persistence.model.Role;
import com.xpto.vendingmachine.persistence.model.Session;
import com.xpto.vendingmachine.persistence.model.UserAuth;
import com.xpto.vendingmachine.persistence.repository.SessionRepository;
import com.xpto.vendingmachine.web.controller.AuthRequestException;
import com.xpto.vendingmachine.web.controller.BadRequestException;
import com.xpto.vendingmachine.web.dto.UserDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.xpto.vendingmachine.security.SecurityConstants.EXPIRATION_TIME;
import static com.xpto.vendingmachine.security.SecurityConstants.SECRET;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    private SessionRepository sessionRepository;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, SessionRepository sessionRepository) {
        this.authenticationManager = authenticationManager;
        this.sessionRepository = sessionRepository;

        setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            UserDTO cred = new ObjectMapper()
                    .readValue(req.getInputStream(), UserDTO.class);
            if(sessionRepository.findById(cred.getUsername()).isPresent()) {
                throw new AuthRequestException("There is already an active session using your account");
            }

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            cred.getUsername(),
                            cred.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {
        UserAuth userAuth = (UserAuth) auth.getPrincipal();

        List<String> roles = userAuth.getAuthorities()
                .stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList());

        Date expirationTime = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        String jti = String.valueOf(UUID.randomUUID());
        sessionRepository.save(Session.builder()
                .username(userAuth.getUsername())
                .expiration(expirationTime.getTime())
                .jti(jti)
                .build());

        String token = JWT.create()
                .withJWTId(jti)
                .withSubject(userAuth.getUsername())
                .withExpiresAt(expirationTime)
                .withClaim("role", roles)
                .sign(Algorithm.HMAC512(SECRET.getBytes()));

        String body = "{ \"token\": \"" + token + "\", "
                + "\"role\": \"" + roles + "\" }";

        res.getWriter().write(body);
        res.setHeader("Content-Type", "application/json");
        res.getWriter().flush();
    }
}
