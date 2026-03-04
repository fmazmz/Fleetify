package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CreateCarDTO;
import org.example.springmvc.cars.mappers.CarMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {
    private final CarRepository repository;

    public CarService(CarRepository repository) {
        this.repository = repository;
    }

    public List<CarDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(CarMapper::toDto)
                .toList();
    }

    public void createCar(CreateCarDTO dto) {
        Car car = CarMapper.fromDto(dto);
        repository.save(car);
    }
}
