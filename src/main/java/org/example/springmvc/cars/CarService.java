package org.example.springmvc.cars;

import org.example.springmvc.cars.model.dto.CarDTO;
import org.example.springmvc.cars.model.dto.CreateCarDTO;
import org.example.springmvc.cars.mappers.CarMapper;
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

    public Page<CarDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(CarMapper::toDto);
    }

    public Page<CarDTO> getByMake(String make, Pageable pageable) {
        return repository.findByMakeIgnoreCase(make, pageable)
                .map(CarMapper::toDto);
    }

    public void create(CreateCarDTO dto) {
        Car car = CarMapper.fromDto(dto);
        repository.save(car);
    }
}
