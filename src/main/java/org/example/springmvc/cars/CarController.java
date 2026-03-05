package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public String listCars(@RequestParam String make, Model model) {
        List<CarDTO> cars = make == null
                ? carService.getAll()
                : carService.getByMake(make);

        model.addAttribute("cars", cars);
        return "cars/list-cars";
    }
}
