package com.compass.order.repositories;

import com.compass.order.domain.service.entities.Order;
import com.compass.order.domain.service.entities.enums.OrderStatus;
import com.compass.order.domain.service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveAndFindOrderById() {
        Order order = new Order();
        order.setCustomerId(123L);
        order.setProductIdList(List.of(101L, 102L));
        order.setQuantities(List.of(2, 3));
        order.setStatus(OrderStatus.CONFIRMED);

        Order savedOrder = orderRepository.save(order);

        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());
        assertTrue(foundOrder.isPresent());
        assertEquals(savedOrder.getId(), foundOrder.get().getId());
    }

    @Test
    void shouldFindOrdersByCustomerId() {
        Order order1 = new Order();
        order1.setCustomerId(123L);
        order1.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setCustomerId(123L);
        order2.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order2);

        List<Order> orders = orderRepository.findByCustomerId(123L);
        assertEquals(2, orders.size());
    }
}
