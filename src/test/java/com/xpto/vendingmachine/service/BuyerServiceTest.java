package com.xpto.vendingmachine.service;

import com.xpto.vendingmachine.persistence.model.Product;
import com.xpto.vendingmachine.persistence.model.UserAuth;
import com.xpto.vendingmachine.persistence.repository.ProductRepository;
import com.xpto.vendingmachine.persistence.repository.UserRepository;
import com.xpto.vendingmachine.web.controller.BadRequestException;
import com.xpto.vendingmachine.web.dto.PurchaseInfo;
import com.xpto.vendingmachine.web.dto.PurchaseResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BuyerServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private BuyerService buyerService;

    @Test
    public void testDeposit_success () {
        // Given
        given(userRepository.findByUsername(any()))
                .willReturn(UserAuth.builder()
                        .deposit(5)
                        .build());

        // And
        mockSecurityContext();

        // When
        buyerService.deposit(10);

        // Then
        verify(userRepository, times(1)).findByUsername("username");
        verify(userRepository, times(1))
                .save(argThat(user -> user.getDeposit() == 15));


    }

    private void mockSecurityContext() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("username");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testBuy_success () {
        // Given
        given(userRepository.findByUsername(any()))
                .willReturn(UserAuth.builder()
                        .deposit(10)
                        .build());
        given(productRepository.findById(1L))
                .willReturn(Optional.of(Product.builder()
                                .cost(5)
                                .title("test")
                                .amountAvailable(2)
                                .id(1L)
                        .build()));

        // And
        mockSecurityContext();

        // When
        PurchaseResponse response = buyerService.buy(PurchaseInfo.builder().amount(2).productId(1).build());

        // Then
        assertEquals(0, response.getChange());
        assertEquals("test", response.getProductName());
        assertEquals(10, response.getTotalSpent());

    }

    @Test
    public void testBuy_notEnoughFunds () {
        // Given
        given(userRepository.findByUsername(any()))
                .willReturn(UserAuth.builder()
                        .deposit(10)
                        .build());
        given(productRepository.findById(1L))
                .willReturn(Optional.of(Product.builder()
                        .cost(5)
                        .title("test")
                        .amountAvailable(2)
                        .id(1L)
                        .build()));

        // And
        mockSecurityContext();

        // When
        BadRequestException thrown = assertThrows(BadRequestException.class,
                () ->  buyerService.buy(PurchaseInfo.builder().amount(5).productId(1).build()));

        // Then
        assertEquals("Not enough funds available.", thrown.getMessage());

    }

    @Test
    public void testBuy_notEnoughProducts () {
        // Given
        given(userRepository.findByUsername(any()))
                .willReturn(UserAuth.builder()
                        .deposit(10)
                        .build());
        given(productRepository.findById(1L))
                .willReturn(Optional.of(Product.builder()
                        .cost(5)
                        .title("test")
                        .amountAvailable(0)
                        .id(1L)
                        .build()));

        // And
        mockSecurityContext();

        // When
        BadRequestException thrown = assertThrows(BadRequestException.class,
                () ->  buyerService.buy(PurchaseInfo.builder().amount(1).productId(1).build()));

        // Then
        assertEquals("Not enough products available.", thrown.getMessage());

    }

    @Test
    public void testDeposit_withAmountNotAllowed () {

        // When
        BadRequestException thrown = assertThrows(BadRequestException.class,
                () -> buyerService.deposit(2));

        // Then
        assertEquals(thrown.getMessage(), "Amount allowed [5, 10, 20, 50, 100].");

    }
}
