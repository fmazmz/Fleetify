package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CreateCarDTO;
import org.example.springmvc.cars.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class CarService {
    private final CarRepository repository;

    public CarService(CarRepository repository) {
        this.repository = repository;
    }

    public Page<CarDTO> search(Pageable pageable, CarFilter filter) {
        return repository.searchCars(
                wildcard(filter.q()),
                wildcard(filter.make()),
                wildcard(filter.model()),
                filter.year(),
                wildcard(filter.licencePlate()),
                wildcard(filter.vin()),
                pageable
        ).map(CarMapper::toDto);
    }

    private String wildcard(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return "%" + value.trim().toLowerCase() + "%";
    }

    public void create(CreateCarDTO dto) {

        if (repository.findByLicencePlateIgnoreCase(dto.licencePlate().trim()).isPresent()) {
            throw new IllegalArgumentException(
                    "A car with license plate '" + dto.licencePlate() + "' already exists."
            );
        }

        if (repository.findByVinIgnoreCase(dto.vin().trim()).isPresent()) {
            throw new IllegalArgumentException(
                    "A car with VIN '" + dto.vin() + "' already exists."
            );
        }

        Car car = CarMapper.fromDto(dto);
        repository.save(car);
    }
}