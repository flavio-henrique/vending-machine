package com.xpto.vendingmachine.web.controller;

import com.xpto.vendingmachine.service.SellerService;
import com.xpto.vendingmachine.web.dto.ProductDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class SellerController {

    private SellerService sellerService;

    @PostMapping("/api/sellers/products")
    public ProductDTO addProduct(@RequestBody ProductDTO productDTO) {
        return sellerService.saveProduct(productDTO);
    }

    @GetMapping("/api/public/products")
    public List<ProductDTO> ListProduct() {
        return sellerService.listProduct();
    }

    @DeleteMapping("/api/sellers/products/{id}")
    public ProductDTO deleteProduct(@PathVariable Long id) {
        return sellerService.delete(id);
    }

    @PutMapping("/api/sellers/products/{id}")
    public ProductDTO updateProduct(@RequestBody ProductDTO productDTO,
                                    @PathVariable Long id) {

        if (!id.equals(productDTO.getId())) {
            throw new BadRequestException("Id in the path does not match with the request body");
        }
        return sellerService.updateProduct(productDTO);

    }
}

