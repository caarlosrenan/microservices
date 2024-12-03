package com.compass.stock;

import com.compass.stock.domain.entities.Product;
import com.compass.stock.domain.entities.service.ProductService;
import com.compass.stock.exceptions.ResourceNotFoundException;
import com.compass.stock.web.dto.CreateProductDTO;
import com.compass.stock.web.dto.controller.ProductController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StockControllerTest {

    private MockMvc mockMvc;
    private ProductService productService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        productService = mock(ProductService.class);
        ProductController productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetProductById_Success() throws Exception {
        Product product = new Product(1L, "TestProduct", 10);
        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/v1/stock/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestProduct"))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        when(productService.findById(1L)).thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(get("/api/v1/stock/product/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        CreateProductDTO dto = new CreateProductDTO("TestProduct", 20);
        Product savedProduct = new Product(1L, "TestProduct", 20);

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/v1/stock/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("TestProduct"))
                .andExpect(jsonPath("$.quantity").value(20));
    }

    @Test
    void testCreateProduct_InvalidData() throws Exception {
        CreateProductDTO invalidDto = new CreateProductDTO("", 20);

        mockMvc.perform(post("/api/v1/stock/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/stock/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }


}
