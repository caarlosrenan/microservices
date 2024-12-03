package com.compass.customer.domain.entities.service;

import com.compass.customer.web.controller.dto.CustomerRequestDTO;
import com.compass.customer.web.controller.dto.CustomerResponseDTO;
import com.compass.customer.web.controller.dto.OrderResponseDTO;
import com.compass.customer.domain.entities.Customer;
import com.compass.customer.exceptions.ResourceNotFoundException;
import com.compass.customer.feign.OrderFeignClient;
import com.compass.customer.domain.entities.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderFeignClient orderFeignClient;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;

    }
    public CustomerResponseDTO createCustomer(CustomerRequestDTO request) {
        Customer customer = new Customer(request.name(), request.email());
        customerRepository.save(customer);
        return new CustomerResponseDTO(customer.getId(), customer.getName(), customer.getEmail());
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(c -> new CustomerResponseDTO(c.getId(), c.getName(), c.getEmail()))
                .collect(Collectors.toList());
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(c -> new CustomerResponseDTO(c.getId(), c.getName(), c.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " +id));
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " +id);
        }
        customerRepository.deleteById(id);
    }

    public List<OrderResponseDTO> getCustomerOrders(Long customerId) {
        return orderFeignClient.getOrdersByCustomerId(customerId);
    }

}
