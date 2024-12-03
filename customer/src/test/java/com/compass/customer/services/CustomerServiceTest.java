package com.compass.customer.services;

import com.compass.customer.domain.entities.Customer;
import com.compass.customer.domain.entities.repository.CustomerRepository;
import com.compass.customer.domain.entities.service.CustomerService;
import com.compass.customer.exceptions.ResourceNotFoundException;
import com.compass.customer.feign.OrderFeignClient;
import com.compass.customer.web.controller.dto.CustomerRequestDTO;
import com.compass.customer.web.controller.dto.CustomerResponseDTO;
import com.compass.customer.web.controller.dto.OrderResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderFeignClient orderFeignClient;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCustomer() {
        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO("John Doe", "john@example.com");
        CustomerResponseDTO customerResponseDTO = customerService.createCustomer(customerRequestDTO);
        assertNotNull(customerResponseDTO);
        assertEquals("John Doe", customerResponseDTO.name());
        assertEquals("john@example.com", customerResponseDTO.email());
        verify(customerRepository, times(1)).save(any());
    }

    @Test
    void testGetAllCustomers() {

        List<CustomerResponseDTO> customerList = Arrays.asList(
                new CustomerResponseDTO(1L, "John Doe", "john@example.com"),
                new CustomerResponseDTO(2L, "Jane Doe", "jane@example.com")
        );

        when(customerRepository.findAll()).thenReturn(Arrays.asList(
                new Customer(1L, "John Doe", "john@example.com"),
                new Customer(2L, "Jane Doe", "jane@example.com")
        ));

        List<CustomerResponseDTO> customers = customerService.getAllCustomers();

        assertEquals(2, customers.size());
        assertEquals("John Doe", customers.get(0).name());
        assertEquals("jane@example.com", customers.get(1).email());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetCustomerById() {
        Customer customer = new Customer(1L, "John Doe", "john@example.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponseDTO customerResponseDTO = customerService.getCustomerById(1L);

        assertNotNull(customerResponseDTO);
        assertEquals("John Doe", customerResponseDTO.name());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            customerService.getCustomerById(1L);
        });

        assertEquals("Customer not found with id: 1", exception.getMessage());
    }

    @Test
    void testDeleteCustomer() {

        Customer customer = new Customer(1L, "John Doe", "john@example.com");
        when(customerRepository.existsById(1L)).thenReturn(true);

        customerService.deleteCustomer(1L);

        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCustomer_NotFound() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            customerService.deleteCustomer(1L);
        });

        assertEquals("Customer not found with id: 1", exception.getMessage());
    }

    @Test
    void testGetCustomerOrders() {
        List<OrderResponseDTO> orders = Arrays.asList(
                new OrderResponseDTO(1L, Arrays.asList(1L, 2L), Arrays.asList(1, 2)),
                new OrderResponseDTO(2L, Arrays.asList(3L, 4L), Arrays.asList(3, 4))
        );

        when(orderFeignClient.getOrdersByCustomerId(1L)).thenReturn(orders);

        List<OrderResponseDTO> response = customerService.getCustomerOrders(1L);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).id());
        assertEquals(1, response.get(0).quantities().get(0));
        verify(orderFeignClient, times(1)).getOrdersByCustomerId(1L);
    }
}
