package com.compass.order.web.controller.dto;

import com.compass.order.domain.service.entities.enums.OrderStatus;
import java.util.List;

public record OrderResponseDTO(Long id, List<Long> productIds, List<Integer> quantities, OrderStatus status) {
}
