package org.example.springmvc.domain.cars.model.dto;

import java.math.BigDecimal;
import java.time.Year;

public record CreateCarDTO(
        String make,
        String model,
        BigDecimal hourlyPrice,
        String licencePlate,
        String vin,
        Year year
) {
}
