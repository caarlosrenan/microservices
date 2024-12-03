package com.compass.customer.web.controller.dto;

import java.util.List;

public record OrderResponseDTO(Long id, List<Long> productIdList, List<Integer> quantities) {
}
