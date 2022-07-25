package com.xpto.vendingmachine.web.controller;

import org.springframework.security.core.AuthenticationException;

public class AuthRequestException extends AuthenticationException {
    public AuthRequestException(String message) {
        super(message);
    }
}
