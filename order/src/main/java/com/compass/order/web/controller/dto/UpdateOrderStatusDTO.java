package com.compass.order.web.controller.dto;

import com.compass.order.domain.service.entities.enums.OrderStatus;

public record UpdateOrderStatusDTO (OrderStatus status) {
}
