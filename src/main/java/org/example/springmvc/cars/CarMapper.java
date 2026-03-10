package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.UpdateCarDTO;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CreateCarDTO;

public class CarMapper {

    public static CarDTO toDto(Car car) {
        return new CarDTO(
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getHourlyPrice(),
                car.getLicencePlate(),
                car.getVin(),
                car.getYear()
        );
    }

    public static Car fromDto(CreateCarDTO dto) {
        return new Car(
                dto.make(),
                dto.model(),
                dto.hourlyPrice(),
                dto.licencePlate(),
                dto.vin(),
                dto.year()
        );
    }

    public static void updateEntity(Car car, UpdateCarDTO dto) {
        car.setMake(dto.make().trim());
        car.setModel(dto.model().trim());
        car.setHourlyPrice(dto.hourlyPrice());
        car.setLicencePlate(dto.licencePlate().trim());
        car.setVin(dto.vin().trim());
        car.setYear(dto.year());
    }
}