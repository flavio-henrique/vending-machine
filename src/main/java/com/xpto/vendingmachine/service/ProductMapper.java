package com.xpto.vendingmachine.service;

import com.xpto.vendingmachine.persistence.model.Product;
import com.xpto.vendingmachine.persistence.model.UserAuth;
import com.xpto.vendingmachine.web.dto.ProductDTO;

public class ProductMapper {
    public static Product mapToProduct(ProductDTO productDTO, Long id) {
        return Product.builder()
                .id(productDTO.getId())
                .title(productDTO.getTitle())
                .description(productDTO.getDescription())
                .cost(productDTO.getCost())
                .amountAvailable(productDTO.getAmountAvailable())
                .userAuth(UserAuth.builder()
                        .id(id)
                        .build())
                .build();
    }

    public static ProductDTO mapToProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .cost(product.getCost())
                .amountAvailable(product.getAmountAvailable())
                .sellerId(product.getUserAuth().getId())
                .build();
    }
}