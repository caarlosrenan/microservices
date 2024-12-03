package com.compass.stock;

import com.compass.stock.domain.entities.Product;
import com.compass.stock.domain.entities.repository.ProductRepository;
import com.compass.stock.domain.entities.service.ProductService;
import com.compass.stock.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StockServiceTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setup() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService();
        productService.productRepository = productRepository;
    }

    @Test
    void testFindById_Success() {
        Product product = new Product(1L, "TestProduct", 10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product found = productService.findById(1L);

        assertEquals(product, found);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findById(1L));
    }

    @Test
    void testUpdateQuantity_Success() {
        Product product = new Product(1L, "TestProduct", 10);
        when(productRepository.findByName("TestProduct")).thenReturn(product);

        productService.updateQuantity("TestProduct", 20);

        assertEquals(20, product.getQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdateQuantity_NotFound() {
        when(productRepository.findByName("NonexistentProduct")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> productService.updateQuantity("NonexistentProduct", 20));
    }

    @Test
    void testReduceQuantity_Success() {
        Product product = new Product(1L, "TestProduct", 10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.reduceQuantity(1L, 5);

        assertEquals(5, product.getQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testReduceQuantity_InsufficientStock() {
        Product product = new Product(1L, "TestProduct", 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class, () -> productService.reduceQuantity(1L, 10));
    }

    @Test
    void testDeleteProduct_Success() {
        Product product = new Product(1L, "TestProduct", 10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }
}
