package com.compass.stock.domain.entities.repository;

import com.compass.stock.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByName(String name);
}
