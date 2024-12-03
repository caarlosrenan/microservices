package com.compass.stock.web.dto.controller;

import com.compass.stock.web.dto.CreateProductDTO;
import com.compass.stock.web.dto.ProductResponseDTO;
import com.compass.stock.web.dto.UpdateProductDTO;
import com.compass.stock.domain.entities.Product;
import com.compass.stock.domain.entities.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/stock")
@Tag(name = "Stock", description = "Operations related to stock management")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get product by ID", description = "Retrieve a product's details by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")})
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        Product product = productService.findById(id);
        ProductResponseDTO responseDTO = new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getQuantity()
        );
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Update product quantity", description = "Update the quantity of a product in stock")
    @ApiResponse(responseCode = "200", description = "Product quantity updated successfully")
    @PutMapping("/update")
    public ResponseEntity<ProductResponseDTO> updateProductQuantity(@RequestBody @Valid UpdateProductDTO updateProductDTO) {
        Product product = productService.updateQuantity(updateProductDTO.name(), updateProductDTO.quantity());

        ProductResponseDTO responseDTO = new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getQuantity()
        );
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Create a new product", description = "Add a new product to the stock")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @PostMapping("/create")
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody @Valid CreateProductDTO createProductDTO) {
        Product product = new Product();
        product.setName(createProductDTO.name());
        product.setQuantity(createProductDTO.quantity());
        Product savedProduct = productService.createProduct(product);

        ProductResponseDTO responseDTO = new ProductResponseDTO(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getQuantity()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Reduce product quantity", description = "Reduce the quantity of a product in stock")
    @ApiResponse(responseCode = "204", description = "Product quantity reduced successfully")
    @PutMapping("/{id}/reduce")
    public ResponseEntity<Void> reduceProductQuantity(@PathVariable Long id, @RequestBody Integer quantity) {
        productService.reduceQuantity(id, quantity);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a product", description = "Remove a product from the stock by its ID")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all products", description = "Retrieve a list of all products in stock")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts().stream()
                .map(product -> new ProductResponseDTO(
                        product.getId(),
                        product.getName(),
                        product.getQuantity()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }
}
