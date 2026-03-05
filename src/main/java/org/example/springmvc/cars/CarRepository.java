package org.example.springmvc.cars;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface CarRepository extends ListCrudRepository<Car, UUID> {
    List<Car> findByMakeIgnoreCase(String make);
}
