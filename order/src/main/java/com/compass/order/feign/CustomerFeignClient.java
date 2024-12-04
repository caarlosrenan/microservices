package com.compass.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service", url = "http://customer-service:8081/api/v1/customers")
public interface CustomerFeignClient {

    @GetMapping("/{id}")
    void getCustomerById(@PathVariable Long id);
}
