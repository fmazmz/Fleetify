package org.example.springmvc.bookings;

import org.example.springmvc.insurances.InsuranceType;

import java.util.UUID;

public class BookingFilter {

    private UUID carId;
    private UUID driverId;
    private InsuranceType insuranceType;

    public UUID getCarId() {return carId;}
    public void setCarId(UUID carId) {this.carId = carId;}
    public UUID getDriverId() {return driverId;}
    public void setDriverId(UUID driverId) {this.driverId = driverId;}
    public InsuranceType getInsuranceType() {return insuranceType;}
    public void setInsuranceType(InsuranceType insuranceType) {this.insuranceType = insuranceType;}
}