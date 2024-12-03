package com.compass.customer.web.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequestDTO(
        @NotBlank(message = "Name cannot be empty")
        String name,
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email cannot be empty")
        String email) {
}
