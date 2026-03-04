package org.example.springmvc.cars;

import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface CarRepository extends ListCrudRepository<Car, UUID> {
}
