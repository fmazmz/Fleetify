package org.example.springmvc.cars.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Year;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String make;
    @NotBlank
    private String model;

    @NotBlank
    @Min(value = 0)
    private int mileage;

    @NotBlank
    @Min(value = 0)
    private BigDecimal hourlyPrice;

    @NotBlank
    private String licencePlate;
    @NotBlank
    private String vin;

    @Min(value = 1800)
    @PastOrPresent
    private Year year;

    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    public Car(String make, String model, int mileage, BigDecimal hourlyPrice, String licencePlate, String vin, Year year) {
        this.make = make;
        this.model = model;
        this.mileage = mileage;
        this.hourlyPrice = hourlyPrice;
        this.licencePlate = licencePlate;
        this.vin = vin;
        this.year = year;
    }
}
