package com.xpto.vendingmachine.web.controller;

import com.xpto.vendingmachine.web.dto.ErrorEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = { BadRequestException.class, RuntimeException.class})
    protected ResponseEntity<Object> handleBadRequest(
            RuntimeException ex, WebRequest request) {
        ErrorEntity errorEntity = ErrorEntity.builder()
                .errorMessage(ex.getMessage())
                .build();
        return new ResponseEntity(errorEntity, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
