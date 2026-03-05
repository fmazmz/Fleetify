package org.example.springmvc.cars.mappers;

import org.example.springmvc.cars.Car;
import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CreateCarDTO;

public class CarMapper {

    public static CarDTO toDto(Car car) {
        return new CarDTO(
                car.getMake(),
                car.getModel(),
                car.getYear()
        );
    }

    public static Car fromDto(CreateCarDTO dto) {
        return new Car(
                dto.make(),
                dto.model(),
                dto.year()
        );
    }
}
