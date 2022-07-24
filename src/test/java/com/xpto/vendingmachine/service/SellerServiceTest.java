package com.xpto.vendingmachine.service;

import com.xpto.vendingmachine.persistence.model.Product;
import com.xpto.vendingmachine.persistence.model.UserAuth;
import com.xpto.vendingmachine.persistence.repository.ProductRepository;
import com.xpto.vendingmachine.persistence.repository.UserRepository;
import com.xpto.vendingmachine.web.controller.BadRequestException;
import com.xpto.vendingmachine.web.dto.ProductDTO;
import com.xpto.vendingmachine.web.dto.PurchaseInfo;
import com.xpto.vendingmachine.web.dto.PurchaseResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SellerServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private SellerService sellerService;

    @Test
    public void testSaveProduct_success () {
        // Given
        given(userRepository.findByUsername(any()))
                .willReturn(UserAuth.builder()
                        .deposit(5)
                        .build());
        given(productRepository.save(any()))
                .willReturn(Product.builder()
                        .id(1L)
                        .userAuth(UserAuth.builder().id(1L).build())
                        .build());

        // And
        mockSecurityContext();

        // When
        ProductDTO productDTO = sellerService.saveProduct(ProductDTO.builder()
                .cost(5)
                .title("test")
                .amountAvailable(1)
                .build());

        // Then
        verify(userRepository, times(1)).findByUsername("username");
        verify(productRepository, times(1))
                .save(argThat(product -> product.getCost() == 5));
        assertEquals(1L, productDTO.getId());
    }

    @Test
    public void testUpdateProduct_success () {
        // Given
        given(productRepository.findByIdAndUserAuthUsername(1L, "username"))
                .willReturn(Optional.of(Product.builder().build()));
        SellerService sellerServiceSpied = Mockito.spy(sellerService);
        Mockito.doReturn(ProductDTO.builder()
                .id(1L)
                .build()).when(sellerServiceSpied).saveProduct(any());

        // And
        mockSecurityContext();

        // When
        ProductDTO productDTO = sellerServiceSpied.updateProduct(ProductDTO.builder()
                .cost(5)
                .title("test")
                .amountAvailable(1)
                .id(1L)
                .build());

        // Then
        assertEquals(1L, productDTO.getId());
    }

    @Test
    public void testList_success () {
        // Given
        given(productRepository.findAll())
                .willReturn(Collections.singletonList(Product.builder()
                        .id(1L)
                        .userAuth(UserAuth.builder().id(1L).build())
                        .build()));

        // When
        List<ProductDTO> productDTOList = sellerService.listProduct();

        // Then
        assertEquals(1, productDTOList.size());
    }

    @Test
    public void testDelete_success () {
        // Given
        given(productRepository.findByIdAndUserAuthUsername(1L, "username"))
                .willReturn(Optional.of(Product.builder()
                        .id(1L)
                        .userAuth(UserAuth.builder().id(1L).build())
                        .build()));

        // And
        mockSecurityContext();

        // When
        ProductDTO productDTO= sellerService.delete(1L);

        // Then
        assertEquals(1L, productDTO.getId());
    }

    @Test
    public void testSaveProduct_invalidCost () {
        // Given
        ProductDTO product = ProductDTO.builder()
                .cost(6)
                .title("test")
                .amountAvailable(1)
                .build();

        // When
        BadRequestException thrown = assertThrows(BadRequestException.class,
                () -> sellerService.saveProduct(product));

        // Then
        assertEquals("Cost should be in multiples of 5.", thrown.getMessage());
    }

    private void mockSecurityContext() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn("username");
        SecurityContextHolder.setContext(securityContext);
    }

}
