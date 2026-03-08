package org.example.springmvc.bookings;

import lombok.Getter;
import lombok.Setter;
import org.example.springmvc.insurances.InsuranceType;

import java.util.UUID;

@Setter
@Getter
public class BookingFilter {
    private UUID carId;
    private UUID driverId;
    private InsuranceType insuranceType;
}