package org.example.springmvc.cars.dto;

public record CreateCarDTO(
        String make,
        String model,
        int year
) {
}
