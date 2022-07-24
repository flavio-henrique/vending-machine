package com.xpto.vendingmachine.web.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductDTO {
    private Long id;
    private String title;
    private String description;
    private int amountAvailable;
    private int cost;
    private long sellerId;
}
