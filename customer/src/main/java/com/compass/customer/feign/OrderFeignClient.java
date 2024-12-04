package com.compass.customer.feign;

import com.compass.customer.web.controller.dto.OrderResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service", url = "http://localhost:8082/api/v1/orders")
public interface OrderFeignClient {

    @GetMapping("/customer/{customerId}")
    List<OrderResponseDTO> getOrdersByCustomerId(@PathVariable Long customerId);
}
