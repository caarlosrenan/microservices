package com.compass.order.web.controller.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderDTO(  @NotNull(message = "Customer ID is required") Long customerId,
                               @NotNull(message = "Product IDs are required") List<Long> productIds,
                               @NotNull(message = "Quantities are required") List<Integer> quantities ) {
}
