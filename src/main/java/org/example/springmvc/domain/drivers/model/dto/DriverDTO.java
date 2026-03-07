package org.example.springmvc.domain.drivers.model.dto;

import org.example.springmvc.domain.cars.model.dto.CarDTO;

import java.util.List;
import java.util.UUID;

public record DriverDTO(
        UUID id,
        String email,
        String fname,
        String lname,
        String ssn,
        List<CarDTO> cars
) {
}
