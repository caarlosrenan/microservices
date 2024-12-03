package com.compass.order.domain.service;

import com.compass.order.web.controller.dto.StockResponseDTO;
import com.compass.order.domain.service.entities.Order;
import com.compass.order.domain.service.entities.enums.OrderStatus;
import com.compass.order.exceptions.CustomerNotFoundException;
import com.compass.order.exceptions.InsufficientStockException;
import com.compass.order.exceptions.ProductNotFoundException;
import com.compass.order.exceptions.ResourceNotFoundException;
import com.compass.order.feign.CustomerFeignClient;
import com.compass.order.feign.StockFeignClient;
import com.compass.order.domain.service.repository.OrderRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StockFeignClient stockFeignClient;

    @Autowired
    private CustomerFeignClient customerFeignClient;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public void checkStockAvailability(List<Long> productIds, List<Integer> requestedQuantities) {
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            int requestedQuantity = requestedQuantities.get(i);

            StockResponseDTO stock = stockFeignClient.getProductById(productId);
            if (stock == null) {
                throw new ProductNotFoundException(productId.toString());
            }

            if (stock.quantity() < requestedQuantity) {
                throw new InsufficientStockException(productId, stock.quantity(), requestedQuantity);
            }
        }
    }

    @Transactional
    public Order createOrder(Order order) {
        validateCustomer(order.getCustomerId());
        checkStockAvailability(order.getProductIdList(), order.getQuantities());
        reduceStock(order.getProductIdList(), order.getQuantities());
        order.setStatus(OrderStatus.CONFIRMED);
        return orderRepository.save(order);
    }

    private void validateCustomer(Long customerId) {
        try {
            log.debug("Validating customer with ID: {}", customerId);
            customerFeignClient.getCustomerById(customerId);
        } catch (FeignException.NotFound e) {
            log.error("Customer validation failed for ID: {}", customerId, e);
            throw new CustomerNotFoundException(customerId.toString());
        } catch (Exception e) {
            log.error("Unexpected error occurred while validating customer ID: {}", customerId, e);
            throw new RuntimeException("Unexpected error occurred while validating customer", e);
        }
    }

    private void reduceStock(List<Long> productIds, List<Integer> requestedQuantities) {
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            int quantity = requestedQuantities.get(i);
            stockFeignClient.reduceStock(productId, quantity);
        }
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    public List<Order> findOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}
