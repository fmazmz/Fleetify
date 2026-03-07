package org.example.springmvc.domain.booking.dto;

import org.example.springmvc.domain.insurances.InsuranceType;

import java.time.Instant;
import java.util.UUID;

public record CreateBookingDTO(
        UUID carId,
        UUID driverId,
        Instant startTime,
        Instant endTime,
        InsuranceType insuranceType
) {}