package com.xpto.vendingmachine.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpto.vendingmachine.persistence.model.Role;
import com.xpto.vendingmachine.persistence.model.UserAuth;
import com.xpto.vendingmachine.web.dto.UserDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.xpto.vendingmachine.security.SecurityConstants.EXPIRATION_TIME;
import static com.xpto.vendingmachine.security.SecurityConstants.SECRET;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

        setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            UserDTO cred = new ObjectMapper()
                    .readValue(req.getInputStream(), UserDTO.class);

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

        String token = JWT.create()
                .withSubject(userAuth.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withClaim("role", roles)
                .sign(Algorithm.HMAC512(SECRET.getBytes()));

        String body = "{ \"token\": \"" + token + "\", "
                + "\"role\": \"" + roles + "\" }";

        res.getWriter().write(body);
        res.setHeader("Content-Type", "application/json");
        res.getWriter().flush();
    }
}
