package com.xpto.vendingmachine.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorEntity {
    private String errorMessage;
}
