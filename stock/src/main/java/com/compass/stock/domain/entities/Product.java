package com.compass.stock.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotNull(message = "Product quantity cannot be null")
    @Min(value = 0, message = "Quantity must be at least 0")
    private Integer quantity;
}
