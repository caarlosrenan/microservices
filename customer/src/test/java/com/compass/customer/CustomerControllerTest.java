package com.compass.customer;

import com.compass.customer.domain.entities.Customer;
import com.compass.customer.domain.entities.repository.CustomerRepository;
import com.compass.customer.web.controller.CustomerController;
import com.compass.customer.web.controller.dto.CustomerRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreateCustomer() throws Exception {
        CustomerRequestDTO request = new CustomerRequestDTO("John Marston", "john@gmail.com");
        Customer customer = new Customer("John Marston", "john10@gmail.com");
        customer.setId(1L);

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(post("/api/v1/customers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.name").value("John Marston"))
                .andExpect(jsonPath("$.email").value("john@gmail.com"));
    }

    @Test
    void testGetCustomerById() throws Exception {
        Customer customer = new Customer("Leandro Almeida", "leandroalmeida@gmail.com");
        customer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(customer));

        mockMvc.perform(get("/api/v1/customers/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.name").value("Leandro Almeida"))
                .andExpect(jsonPath("$.email").value("leandroalmeida@gmail.com"));
    }

    @Test
    void testDeleteCustomer() throws Exception {
        Customer customer = new Customer("John Doe", "john@example.com");
        customer.setId(1L);

        when(customerRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/customers/delete/1"))
                .andExpect(status().isNoContent());
    }
}
