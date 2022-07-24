package com.xpto.vendingmachine.service;

import com.xpto.vendingmachine.persistence.model.Product;
import com.xpto.vendingmachine.persistence.model.UserAuth;
import com.xpto.vendingmachine.persistence.repository.ProductRepository;
import com.xpto.vendingmachine.persistence.repository.UserRepository;
import com.xpto.vendingmachine.web.controller.BadRequestException;
import com.xpto.vendingmachine.web.dto.PurchaseInfo;
import com.xpto.vendingmachine.web.dto.PurchaseResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@AllArgsConstructor
public class BuyerService {
    private UserRepository userRepository;
    private ProductRepository productRepository;

    public void deposit(int amount) {

        if(!Arrays.asList(5, 10, 20, 50, 100).contains(amount)) {
            throw new BadRequestException("Amount allowed [5, 10, 20, 50, 100].");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        UserAuth userAuth = userRepository.findByUsername(username);
        int currentAmount = userAuth.getDeposit();
        userAuth.setDeposit(currentAmount + amount);
        userRepository.save(userAuth);
    }

    public void reset() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        UserAuth userAuth = userRepository.findByUsername(username);
        userAuth.setDeposit(0);
        userRepository.save(userAuth);
    }

    public PurchaseResponse buy(PurchaseInfo purchaseInfo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        UserAuth userAuth = userRepository.findByUsername(username);
        Product product = productRepository.findById(purchaseInfo.getProductId())
                .orElseThrow(() -> new BadRequestException("Product not found"));
        int currentAmount = userAuth.getDeposit();

        int totalSpent = product.getCost() * purchaseInfo.getAmount();

        if(currentAmount < totalSpent) {
            throw new BadRequestException("Not enough funds available.");
        }

        if(product.getAmountAvailable() < purchaseInfo.getAmount()) {
            throw new BadRequestException("Not enough products available.");
        }

        userAuth.setDeposit(currentAmount - totalSpent);
        product.setAmountAvailable(product.getAmountAvailable() - purchaseInfo.getAmount());

        productRepository.save(product);
        userRepository.save(userAuth);

        return PurchaseResponse.builder()
                .totalSpent(totalSpent)
                .productName(product.getTitle())
                .change(currentAmount - totalSpent)
                .build();
    }
}
