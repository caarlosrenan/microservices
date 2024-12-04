package com.compass.order.feign;

import com.compass.order.web.controller.dto.StockResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "stock-service", url = "http://stock-service:8083/api/v1/stock")
public interface StockFeignClient {

    @GetMapping("/product/{id}")
    StockResponseDTO getProductById(@PathVariable Long id);

    @GetMapping
    List<StockResponseDTO> getAllStock();

    @PutMapping("/{id}/reduce")
    void reduceStock(@PathVariable Long id, @RequestBody Integer quantity);
}
