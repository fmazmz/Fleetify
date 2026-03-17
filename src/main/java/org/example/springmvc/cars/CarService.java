package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CarFilter;
import org.example.springmvc.cars.dto.CreateCarDTO;
import org.example.springmvc.cars.dto.UpdateCarDTO;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.exceptions.DuplicateEntityException;
import org.example.springmvc.exceptions.EntityNotFoundException;
import org.example.springmvc.exceptions.ErrorMessages;
import org.example.springmvc.utils.SearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CarService {

    private final CarRepository repository;

    public CarService(CarRepository repository) {
        this.repository = repository;
    }

    @Caching(evict = {
            @CacheEvict(value = "availableCars", allEntries = true),
            @CacheEvict(value = "cars", allEntries = true)
    })
    public CarDTO create(CreateCarDTO dto) {
        log.debug("Creating car: make={}, model={}, plate={}", dto.make(), dto.model(), dto.licencePlate());

        String plate = dto.licencePlate().trim();
        String vin = dto.vin().trim();

        if (repository.findByLicencePlateIgnoreCase(plate).isPresent()) {
            throw new DuplicateEntityException(
                    String.format(ErrorMessages.CAR_DUPLICATE_PLATE, plate)
            );
        }

        if (repository.findByVinIgnoreCase(vin).isPresent()) {
            throw new DuplicateEntityException(
                    String.format(ErrorMessages.CAR_DUPLICATE_VIN, vin)
            );
        }

        Car car = CarMapper.fromDto(dto);
        Car savedCar = repository.save(car);

        log.info("Car created successfully: id={}, plate={}", savedCar.getId(), plate);
        return CarMapper.toDto(savedCar);
    }

    @Cacheable(
            value = "cars",
            key = "{#pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString(), " +
                    "#filter.q, #filter.make, #filter.model, #filter.year, " +
                    "#filter.minPrice, #filter.maxPrice, #filter.licencePlate, #filter.vin}"
    )
    public Page<CarDTO> search(Pageable pageable, CarFilter filter) {
        log.debug("Searching cars with filter: q={}, make={}, model={}", filter.q(), filter.make(), filter.model());
        return repository.searchCars(
                SearchUtils.toWildcardPattern(filter.q()),
                SearchUtils.toWildcardPattern(filter.make()),
                SearchUtils.toWildcardPattern(filter.model()),
                filter.year(),
                filter.minPrice(),
                filter.maxPrice(),
                SearchUtils.toWildcardPattern(filter.licencePlate()),
                SearchUtils.toWildcardPattern(filter.vin()),
                pageable
        ).map(CarMapper::toDto);
    }

    @Cacheable(
            value = "availableCars",
            key = "{#startTime, #endTime}"
    )
    public List<CarDTO> findAvailable(Instant startTime, Instant endTime) {
        log.debug("Finding available cars from {} to {}", startTime, endTime);
        return repository.findAvailableCars(startTime, endTime)
                .stream()
                .map(CarMapper::toDto)
                .toList();
    }

    @Cacheable(value = "car", key = "#id")
    public CarDTO getById(UUID id) {
        log.debug("Fetching car by id={}", id);
        Car car = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.CAR_NOT_FOUND_ID, id)
                ));
        return CarMapper.toDto(car);
    }

    @Caching(evict = {
            @CacheEvict(value = "cars", allEntries = true),
            @CacheEvict(value = "availableCars", allEntries = true),
            @CacheEvict(value = "car", key = "#id")
    })
    public void update(UUID id, UpdateCarDTO dto) {
        log.debug("Updating car: id={}", id);
        Car car = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.CAR_NOT_FOUND_ID, id)
                ));

        String plate = dto.licencePlate().trim();
        String vin = dto.vin().trim();

        if (repository.existsByLicencePlateIgnoreCaseAndIdNot(plate, id)) {
            throw new DuplicateEntityException(
                    String.format(ErrorMessages.CAR_DUPLICATE_PLATE, plate)
            );
        }

        if (repository.existsByVinIgnoreCaseAndIdNot(vin, id)) {
            throw new DuplicateEntityException(
                    String.format(ErrorMessages.CAR_DUPLICATE_VIN, vin)
            );
        }

        CarMapper.updateEntity(car, dto);
        repository.save(car);
        log.info("Car updated successfully: id={}, plate={}", id, plate);
    }

    @Caching(evict = {
            @CacheEvict(value = "cars", allEntries = true),
            @CacheEvict(value = "availableCars", allEntries = true),
            @CacheEvict(value = "car", key = "#id")
    })
    public void delete(UUID id) {
        log.debug("Deleting car: id={}", id);
        Car car = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.CAR_NOT_FOUND_ID, id)
                ));
        repository.delete(car);
        log.info("Car deleted successfully: id={}", id);
    }
}

