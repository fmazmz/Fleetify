package org.example.springmvc.cars.model.dto;

import java.math.BigDecimal;
import java.time.Year;

public record CreateCarDTO(
        String make,
        String model,
        int mileage,
        BigDecimal hourlyPrice,
        String licencePlate,
        String vin,
        Year year
) {
}
