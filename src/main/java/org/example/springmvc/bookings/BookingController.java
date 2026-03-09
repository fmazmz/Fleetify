package org.example.springmvc.bookings;

import jakarta.validation.Valid;
import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.bookings.model.BookingFilter;
import org.example.springmvc.cars.CarService;
import org.example.springmvc.drivers.DriverService;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.insurances.InsuranceType;
import org.example.springmvc.users.UserService;
import org.example.springmvc.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final CarService carService;
    private final DriverService driverService;
    private final UserService userService;

    public BookingController(BookingService bookingService, CarService carService, DriverService driverService, UserService userService) {
        this.bookingService = bookingService;
        this.carService = carService;
        this.driverService = driverService;
        this.userService = userService;
    }

    @GetMapping
    public String listBookings(@PageableDefault(value = 5) Pageable pageable, @ModelAttribute BookingFilter filter, Model model) {
        Page<BookingDTO> bookings = bookingService.search(pageable, filter);
        model.addAttribute("bookings", bookings);
        model.addAttribute("filter", filter);
        return "bookings/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        User user = userService.getCurrentUser();
        Driver driver = user.getDriver();

        CreateBookingDTO bookingDTO = new CreateBookingDTO(
                null,
                driver != null ? driver.getId() : null,
                null,
                null,
                null
        );

        model.addAttribute("booking", bookingDTO);
        model.addAttribute("cars", carService.getAll(Pageable.unpaged()).getContent());

        Map<InsuranceType, String> insuranceDisplayNames = Map.of(
                InsuranceType.BASIC, "Basic",
                InsuranceType.PREMIUM, "Premium",
                InsuranceType.FULL_COVERAGE, "Full Coverage"
        );
        model.addAttribute("insuranceTypes", insuranceDisplayNames);

        if (driver == null) {
            model.addAttribute("error", "You must become a driver first.");
        }

        return "bookings/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("booking") CreateBookingDTO booking,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("cars",
                    carService.getAll(Pageable.unpaged()).getContent());

            model.addAttribute("insuranceTypes", Map.of(
                    InsuranceType.BASIC, "Basic",
                    InsuranceType.PREMIUM, "Premium",
                    InsuranceType.FULL_COVERAGE, "Full Coverage"
            ));

            return "bookings/create";
        }

        bookingService.create(booking);
        redirectAttributes.addFlashAttribute("success", "Booking created successfully!");
        return "redirect:/";
    }
}