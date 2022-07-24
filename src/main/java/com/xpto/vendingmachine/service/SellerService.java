package com.xpto.vendingmachine.service;

import com.xpto.vendingmachine.persistence.model.Product;
import com.xpto.vendingmachine.persistence.model.UserAuth;
import com.xpto.vendingmachine.persistence.repository.ProductRepository;
import com.xpto.vendingmachine.persistence.repository.UserRepository;
import com.xpto.vendingmachine.web.controller.BadRequestException;
import com.xpto.vendingmachine.web.dto.ProductDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SellerService {
    private ProductRepository productRepository;
    private UserRepository userRepository;

    public ProductDTO updateProduct(ProductDTO productDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        productRepository.findByIdAndUserAuthUsername(productDTO.getId(), username)
                .orElseThrow(() -> new BadRequestException("Product not Found"));
        return saveProduct(productDTO);
    }

    public ProductDTO saveProduct(ProductDTO productDTO){
        if(productDTO.getCost() % 5 != 0) {
            throw new BadRequestException("Cost should be in multiples of 5.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        UserAuth userAuth = userRepository.findByUsername(username);

        Product product = ProductMapper.mapToProduct(productDTO, userAuth.getId());
        Product saved = productRepository.save(product);
        return ProductMapper.mapToProductDTO(saved);
    }

    public List<ProductDTO> listProduct() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(ProductMapper::mapToProductDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO delete(Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        Product product = productRepository.findByIdAndUserAuthUsername(id, username)
                .orElseThrow(() -> new BadRequestException("Product not Found"));
        ProductDTO productDTO = ProductMapper.mapToProductDTO(product);
        productRepository.delete(Product.builder().id(id).build());
        return productDTO;
    }

}
