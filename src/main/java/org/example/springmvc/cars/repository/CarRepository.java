package org.example.springmvc.cars.repository;

import org.example.springmvc.cars.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarRepository extends ListCrudRepository<Car, UUID> {
    Page<Car> findAll(Pageable pageable);
    Optional<Car> findByVinIgnoreCase(String vin);
    Optional<Car> findByLicencePlateIgnoreCase(String licencePlate);

    @Query("""
            SELECT c FROM Car c WHERE (
      :q IS NULL OR
      LOWER(c.make) LIKE :q OR
      LOWER(c.model) LIKE :q OR
      LOWER(c.licencePlate) LIKE :q OR
      LOWER(c.vin) LIKE :q
)
AND (:make IS NULL OR LOWER(c.make) LIKE :make)
AND (:model IS NULL OR LOWER(c.model) LIKE :model)
AND (:year IS NULL OR c.year = :year)
AND (:licencePlate IS NULL OR LOWER(c.licencePlate) LIKE :licencePlate)
AND (:vin IS NULL OR LOWER(c.vin) LIKE :vin)
""")
    Page<Car> searchCars(String q, String make, String model, Year year, String licencePlate, String vin, Pageable pageable);}