package com.compass.stock.domain.entities.service;

import com.compass.stock.domain.entities.Product;
import com.compass.stock.exceptions.InvalidProductNameException;
import com.compass.stock.exceptions.ResourceNotFoundException;
import com.compass.stock.domain.entities.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    public ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);


    public Product findById(Long id) {
        logger.info("Searching for product with id {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product updateQuantity(String name, Integer quantity) {
        Product product = productRepository.findByName(name);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found with name: " + name);
        }

        product.setQuantity(quantity);
        logger.info("Updating quantity of product: {}, new quantity: {}", name, quantity);

        return productRepository.save(product);
    }

    public Product createProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new InvalidProductNameException("Product name cannot be empty or null.");
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        logger.info("Attempting to delete product with id {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productRepository.delete(product);
        logger.info("Product with id {} deleted successfully", id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void reduceQuantity(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product ID: " + id);
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }
}
