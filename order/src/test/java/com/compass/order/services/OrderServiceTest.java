package com.compass.order.services;

import com.compass.order.web.controller.dto.StockResponseDTO;
import com.compass.order.domain.service.entities.Order;
import com.compass.order.domain.service.entities.enums.OrderStatus;
import com.compass.order.exceptions.InsufficientStockException;
import com.compass.order.feign.CustomerFeignClient;
import com.compass.order.feign.StockFeignClient;
import com.compass.order.domain.service.repository.OrderRepository;
import com.compass.order.domain.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StockFeignClient stockFeignClient;

    @Mock
    private CustomerFeignClient customerFeignClient;

    private Order mockOrder;

    @BeforeEach
    void setUp() {
        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setCustomerId(123L);
        mockOrder.setProductIdList(List.of(101L, 102L));
        mockOrder.setQuantities(List.of(2, 3));
        mockOrder.setStatus(OrderStatus.CONFIRMED);
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        doNothing().when(customerFeignClient).getCustomerById(mockOrder.getCustomerId());
        when(stockFeignClient.getProductById(101L)).thenReturn(new StockResponseDTO(101L, "Product A", 10));
        when(stockFeignClient.getProductById(102L)).thenReturn(new StockResponseDTO(102L, "Product B", 5));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        Order createdOrder = orderService.createOrder(mockOrder);

        assertNotNull(createdOrder);
        assertEquals(OrderStatus.CONFIRMED, createdOrder.getStatus());
        verify(customerFeignClient).getCustomerById(mockOrder.getCustomerId());
        verify(stockFeignClient).getProductById(101L);
        verify(stockFeignClient).getProductById(102L);
        verify(orderRepository).save(mockOrder);
    }

    @Test
    void shouldThrowInsufficientStockException() {
        when(stockFeignClient.getProductById(101L)).thenReturn(new StockResponseDTO(101L, "Product A", 10));
        when(stockFeignClient.getProductById(102L)).thenReturn(new StockResponseDTO(102L, "Product B", 1));

        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(mockOrder));
        verify(stockFeignClient).getProductById(101L);
        verify(stockFeignClient).getProductById(102L);
    }

}
