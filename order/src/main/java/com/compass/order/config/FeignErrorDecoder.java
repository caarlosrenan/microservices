package com.compass.order.config;

import com.compass.order.exceptions.CustomerNotFoundException;
import com.compass.order.exceptions.ProductNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            if (methodKey.contains("getProductById")) {
                return new ProductNotFoundException("Product not found for the given ID");
            } else if (methodKey.contains("getCustomerById")) {
                return new CustomerNotFoundException("Customer not found for the given ID");
            }
        }
        return new RuntimeException("An unexpected error occurred: " + response.reason());
    }
}
