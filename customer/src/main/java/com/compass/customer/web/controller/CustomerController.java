package com.compass.customer.web.controller;

import com.compass.customer.web.controller.dto.CustomerRequestDTO;
import com.compass.customer.web.controller.dto.CustomerResponseDTO;
import com.compass.customer.web.controller.dto.OrderResponseDTO;
import com.compass.customer.feign.OrderFeignClient;
import com.compass.customer.domain.entities.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer", description = "Operations related to customer management")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderFeignClient orderFeignClient;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Create a new customer", description = "Add a new customer to the system")
    @ApiResponse(responseCode = "201", description = "Customer created successfully")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody @Valid CustomerRequestDTO request) {
        CustomerResponseDTO customerResponse = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponse);
    }

    @Operation(summary = "Get all customers", description = "Retrieve a list of all customers in the system")
    @ApiResponse(responseCode = "200")
    @GetMapping
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @Operation(summary = "Get customer by ID", description = "Retrieve a customer's details by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")})
    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @Operation(summary = "Delete a customer", description = "Remove a customer from the system by their ID")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    @Operation(summary = "Get customer orders", description = "Retrieve a list of orders placed by a customer")
    @ApiResponse(responseCode = "200")
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getCustomerOrders(@PathVariable Long id) {
        List<OrderResponseDTO> orders = orderFeignClient.getOrdersByCustomerId(id);
        return ResponseEntity.ok(orders);
    }
}
