package com.compass.order.web.controller;

import com.compass.order.web.controller.dto.CreateOrderDTO;
import com.compass.order.web.controller.dto.OrderResponseDTO;
import com.compass.order.web.controller.dto.UpdateOrderStatusDTO;
import com.compass.order.domain.service.entities.Order;
import com.compass.order.feign.CustomerFeignClient;
import com.compass.order.domain.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Operations related to managing orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerFeignClient customerFeignClient;

    @Operation(summary = "Create a new order", description = "Create a new order for a customer with a list of product IDs and quantities")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid customer or product data"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "422", description = "Insufficient stock for one or more products")
    })
    @PostMapping("/create")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid CreateOrderDTO createOrderDTO) {
        customerFeignClient.getCustomerById(createOrderDTO.customerId());
        Order order = new Order();
        order.setCustomerId(createOrderDTO.customerId());
        order.setProductIdList(createOrderDTO.productIds());
        order.setQuantities(createOrderDTO.quantities());

        Order savedOrder = orderService.createOrder(order);

        OrderResponseDTO responseDTO = new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getProductIdList(),
                savedOrder.getQuantities(),
                savedOrder.getStatus()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Get order by ID", description = "Retrieve an order's details by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        OrderResponseDTO responseDTO = new OrderResponseDTO(
                order.getId(),
                order.getProductIdList(),
                order.getQuantities(),
                order.getStatus()
        );
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Get all orders", description = "Retrieve a list of all orders in the system")
    @ApiResponse(responseCode = "200")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @Operation(summary = "Update order status", description = "Update the status of an order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusDTO updateStatusDTO) {
        Order order = orderService.updateOrderStatus(id, updateStatusDTO.status());
        OrderResponseDTO responseDTO = new OrderResponseDTO(
                order.getId(),
                order.getProductIdList(),
                order.getQuantities(),
                order.getStatus()
        );
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Delete an order", description = "Remove an order from the system by its ID")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get orders by customer ID", description = "Retrieve a list of orders for a specific customer")
    @ApiResponse(responseCode = "200")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<Order> orders = orderService.findOrdersByCustomerId(customerId);
        List<OrderResponseDTO> responseDTOs = orders.stream()
                .map(order -> new OrderResponseDTO(order.getId(), order.getProductIdList(), order.getQuantities(), order.getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
}
