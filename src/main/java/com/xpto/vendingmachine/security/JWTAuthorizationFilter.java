package com.xpto.vendingmachine.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xpto.vendingmachine.persistence.model.Role;
import com.xpto.vendingmachine.persistence.repository.TokenRevokeRepository;
import com.xpto.vendingmachine.web.controller.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.xpto.vendingmachine.security.SecurityConstants.HEADER_STRING;
import static com.xpto.vendingmachine.security.SecurityConstants.SECRET;
import static com.xpto.vendingmachine.security.SecurityConstants.TOKEN_PREFIX;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private TokenRevokeRepository tokenRevokeRepository;

    public JWTAuthorizationFilter(AuthenticationManager authManager, TokenRevokeRepository tokenRevokeRepository) {
        super(authManager);
        this.tokenRevokeRepository = tokenRevokeRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    // Reads the JWT from the Authorization header, and then uses JWT to validate the token
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            // parse the token.
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""));
            String user = jwt.getSubject();
            if(tokenRevokeRepository.findById(jwt.getId()).isPresent()) {
                throw new BadRequestException("You are logged out.");
            }
            List<Role> roles = jwt.getClaim("role").asList(Role.class);

            if (user != null) {
                // new arraylist means authorities
                return new UsernamePasswordAuthenticationToken(user, null, roles);
            }

            return null;
        }

        return null;
    }
}
