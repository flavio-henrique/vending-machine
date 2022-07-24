package com.xpto.vendingmachine.web.controller;

import com.xpto.vendingmachine.service.BuyerService;
import com.xpto.vendingmachine.web.dto.AmountInfo;
import com.xpto.vendingmachine.web.dto.PurchaseInfo;
import com.xpto.vendingmachine.web.dto.PurchaseResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BuyerController {

    private BuyerService buyerService;

    @PostMapping("/api/buyers/deposit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(@RequestBody AmountInfo amountInfo) {
        buyerService.deposit(amountInfo.getAmount());
    }

    @PostMapping("/api/buyers/buy")
    public PurchaseResponse buy(@RequestBody PurchaseInfo purchaseInfo) {
        return buyerService.buy(purchaseInfo);
    }

    @PostMapping("/api/buyers/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset() {
        buyerService.reset();
    }
}
