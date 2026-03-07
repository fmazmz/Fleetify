package org.example.springmvc.domain.drivers.model.dto;

public record CreateDriverDTO(
        String email,
        String fname,
        String lname,
        String ssn
) {
}
