package org.example.springmvc.cars.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Year;

public record CreateCarDTO(
        @NotBlank(message = "Make is required")
        String make,

        @NotBlank(message = "Model is required")
        String model,

        @NotBlank(message = "Year is required")
        @PastOrPresent
        Year year
) {
}
