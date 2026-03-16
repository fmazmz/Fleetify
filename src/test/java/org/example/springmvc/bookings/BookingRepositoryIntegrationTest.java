package org.example.springmvc.bookings;

import org.example.springmvc.bookings.model.Booking;
import org.example.springmvc.cars.CarRepository;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.drivers.DriverRepository;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.insurances.CarInsurance;
import org.example.springmvc.insurances.InsuranceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryIntegrationTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private DriverRepository driverRepository;

    private final CarInsurance carInsurance = new CarInsurance();

    private Car testCar;
    private Car testCar2;
    private Driver testDriver;
    private Driver testDriver2;
    private Booking testBooking;

    private static final Instant BOOKING_START = Instant.parse("2026-03-20T10:00:00Z");
    private static final Instant BOOKING_END = Instant.parse("2026-03-20T13:00:00Z");

    @BeforeEach
    void setUp() {
        testCar = carRepository.save(new Car(
                "Volvo", "XC90", new BigDecimal("100.00"),
                "ABC123", "VIN123456", Year.of(2022)
        ));

        testCar2 = carRepository.save(new Car(
                "BMW", "X5", new BigDecimal("200.00"),
                "DEF456", "VIN789012", Year.of(2023)
        ));

        testDriver = driverRepository.save(new Driver(
                "Oscar", "Danielsson", "199001011207"
        ));

        testDriver2 = driverRepository.save(new Driver(
                "Vincent", "Ashcroft", "199402053466"
        ));

        testBooking = bookingRepository.save(new Booking(
                testCar,
                testDriver,
                BOOKING_START,
                BOOKING_END,
                InsuranceType.BASIC,
                totalPrice(testCar, 3, InsuranceType.BASIC)
        ));
    }

    private BigDecimal totalPrice(Car car, long hours, InsuranceType insuranceType) {
        return car.getHourlyPrice()
                .multiply(BigDecimal.valueOf(hours))
                .add(carInsurance.getPrice(insuranceType));
    }

    @Test
    void existsOverlappingBooking_shouldReturnTrue_whenOverlapExistsInsideExistingBooking() {
        boolean exists = bookingRepository.existsOverlappingBooking(
                testCar.getId(),
                Instant.parse("2026-03-20T11:00:00Z"),
                Instant.parse("2026-03-20T12:00:00Z")
        );

        assertThat(exists).isTrue();
    }

    @Test
    void existsOverlappingBooking_shouldReturnTrue_whenNewBookingEnclosesExistingBooking() {
        boolean exists = bookingRepository.existsOverlappingBooking(
                testCar.getId(),
                Instant.parse("2026-03-20T09:00:00Z"),
                Instant.parse("2026-03-20T14:00:00Z")
        );

        assertThat(exists).isTrue();
    }

    @Test
    void existsOverlappingBooking_shouldReturnFalse_whenNoOverlapAfterExistingBooking() {
        boolean exists = bookingRepository.existsOverlappingBooking(
                testCar.getId(),
                Instant.parse("2026-03-20T14:00:00Z"),
                Instant.parse("2026-03-20T16:00:00Z")
        );

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlappingBooking_shouldReturnFalse_whenNoOverlapBeforeExistingBooking() {
        boolean exists = bookingRepository.existsOverlappingBooking(
                testCar.getId(),
                Instant.parse("2026-03-20T07:00:00Z"),
                Instant.parse("2026-03-20T10:00:00Z")
        );

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlappingBooking_shouldReturnFalse_whenBookingStartsExactlyAtExistingEndTime() {
        boolean exists = bookingRepository.existsOverlappingBooking(
                testCar.getId(),
                BOOKING_END,
                Instant.parse("2026-03-20T15:00:00Z")
        );

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlappingBooking_shouldReturnFalse_whenBookingEndsExactlyAtExistingStartTime() {
        boolean exists = bookingRepository.existsOverlappingBooking(
                testCar.getId(),
                Instant.parse("2026-03-20T08:00:00Z"),
                BOOKING_START
        );

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlappingBooking_shouldReturnFalse_forDifferentCarEvenIfTimeOverlaps() {
        boolean exists = bookingRepository.existsOverlappingBooking(
                testCar2.getId(),
                Instant.parse("2026-03-20T11:00:00Z"),
                Instant.parse("2026-03-20T12:00:00Z")
        );

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlappingBookingExcludingId_shouldReturnFalse_whenOnlySameBookingMatches() {
        boolean exists = bookingRepository.existsOverlappingBookingExcludingId(
                testCar.getId(),
                BOOKING_START,
                BOOKING_END,
                testBooking.getId()
        );

        assertThat(exists).isFalse();
    }

    @Test
    void existsOverlappingBookingExcludingId_shouldReturnTrue_whenAnotherBookingOverlaps() {
        bookingRepository.save(new Booking(
                testCar,
                testDriver2,
                Instant.parse("2026-03-20T11:00:00Z"),
                Instant.parse("2026-03-20T12:00:00Z"),
                InsuranceType.PREMIUM,
                totalPrice(testCar, 1, InsuranceType.PREMIUM)
        ));

        boolean exists = bookingRepository.existsOverlappingBookingExcludingId(
                testCar.getId(),
                BOOKING_START,
                BOOKING_END,
                testBooking.getId()
        );

        assertThat(exists).isTrue();
    }

    @Test
    void searchBookings_shouldFilterByCarId() {
        var result = bookingRepository.searchBookings(
                null,
                testCar.getId(),
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(booking -> booking.getCar().getId())
                .containsExactly(testCar.getId());
    }

    @Test
    void searchBookings_shouldFilterByDriverId() {
        var result = bookingRepository.searchBookings(
                null,
                null,
                testDriver.getId(),
                null,
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(booking -> booking.getDriver().getId())
                .containsExactly(testDriver.getId());
    }

    @Test
    void searchBookings_shouldFilterByInsuranceType() {
        var result = bookingRepository.searchBookings(
                null,
                null,
                null,
                InsuranceType.BASIC,
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(Booking::getInsuranceType)
                .containsExactly(InsuranceType.BASIC);
    }

    @Test
    void searchBookings_shouldFilterBySearchQuery_make() {
        var result = bookingRepository.searchBookings(
                "%volvo%",
                null,
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(booking -> booking.getCar().getMake())
                .containsExactly("Volvo");
    }

    @Test
    void searchBookings_shouldFilterBySearchQuery_model() {
        var result = bookingRepository.searchBookings(
                "%xc90%",
                null,
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(booking -> booking.getCar().getModel())
                .containsExactly("XC90");
    }

    @Test
    void searchBookings_shouldReturnEmptyPage_whenSearchQueryDoesNotMatch() {
        var result = bookingRepository.searchBookings(
                "%audi%",
                null,
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void searchBookings_shouldReturnAll_whenNoFilters() {
        var result = bookingRepository.searchBookings(
                null,
                null,
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchBookings_shouldReturnMultipleBookings_whenMultipleExist() {
        bookingRepository.save(new Booking(
                testCar2,
                testDriver2,
                Instant.parse("2026-03-21T09:00:00Z"),
                Instant.parse("2026-03-21T12:00:00Z"),
                InsuranceType.PREMIUM,
                totalPrice(testCar2, 3, InsuranceType.PREMIUM)
        ));

        var result = bookingRepository.searchBookings(
                null,
                null,
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void searchBookings_shouldCombineMultipleFilters() {
        bookingRepository.save(new Booking(
                testCar2,
                testDriver2,
                Instant.parse("2026-03-21T09:00:00Z"),
                Instant.parse("2026-03-21T12:00:00Z"),
                InsuranceType.PREMIUM,
                totalPrice(testCar2, 3, InsuranceType.PREMIUM)
        ));

        var result = bookingRepository.searchBookings(
                "%volvo%",
                testCar.getId(),
                testDriver.getId(),
                InsuranceType.BASIC,
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
        Booking found = result.getContent().getFirst();

        assertThat(found.getCar().getId()).isEqualTo(testCar.getId());
        assertThat(found.getDriver().getId()).isEqualTo(testDriver.getId());
        assertThat(found.getInsuranceType()).isEqualTo(InsuranceType.BASIC);
    }

    @Test
    void searchBookings_shouldReturnEmpty_whenCombinedFiltersDoNotMatch() {
        bookingRepository.save(new Booking(
                testCar2,
                testDriver2,
                Instant.parse("2026-03-21T09:00:00Z"),
                Instant.parse("2026-03-21T12:00:00Z"),
                InsuranceType.PREMIUM,
                totalPrice(testCar2, 3, InsuranceType.PREMIUM)
        ));

        var result = bookingRepository.searchBookings(
                "%volvo%",
                testCar.getId(),
                testDriver.getId(),
                InsuranceType.PREMIUM,
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByDriverId_shouldReturnBookingsForSpecificDriver() {
        bookingRepository.save(new Booking(
                testCar2,
                testDriver2,
                Instant.parse("2026-03-21T09:00:00Z"),
                Instant.parse("2026-03-21T12:00:00Z"),
                InsuranceType.PREMIUM,
                totalPrice(testCar2, 3, InsuranceType.PREMIUM)
        ));

        var result = bookingRepository.findByDriverId(
                testDriver.getId(),
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent())
                .extracting(booking -> booking.getDriver().getId())
                .containsExactly(testDriver.getId());
    }

    @Test
    void searchBookings_shouldSupportPagination() {
        bookingRepository.save(new Booking(
                testCar2,
                testDriver2,
                Instant.parse("2026-03-21T09:00:00Z"),
                Instant.parse("2026-03-21T12:00:00Z"),
                InsuranceType.PREMIUM,
                totalPrice(testCar2, 3, InsuranceType.PREMIUM)
        ));

        var result = bookingRepository.searchBookings(
                null,
                null,
                null,
                null,
                PageRequest.of(0, 1)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void searchBookings_shouldSortByStartTimeAscending() {
        bookingRepository.save(new Booking(
                testCar2,
                testDriver2,
                Instant.parse("2026-03-22T09:00:00Z"),
                Instant.parse("2026-03-22T12:00:00Z"),
                InsuranceType.PREMIUM,
                totalPrice(testCar2, 3, InsuranceType.PREMIUM)
        ));

        var result = bookingRepository.searchBookings(
                null,
                null,
                null,
                null,
                PageRequest.of(0, 10, Sort.by("startTime").ascending())
        );

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getStartTime())
                .isBefore(result.getContent().get(1).getStartTime());
    }

    @Test
    void searchBookings_shouldSortByStartTimeDescending() {
        bookingRepository.save(new Booking(
                testCar2,
                testDriver2,
                Instant.parse("2026-03-22T09:00:00Z"),
                Instant.parse("2026-03-22T12:00:00Z"),
                InsuranceType.PREMIUM,
                totalPrice(testCar2, 3, InsuranceType.PREMIUM)
        ));

        var result = bookingRepository.searchBookings(
                null,
                null,
                null,
                null,
                PageRequest.of(0, 10, Sort.by("startTime").descending())
        );

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getStartTime())
                .isAfter(result.getContent().get(1).getStartTime());
    }

    @Test
    void searchBookings_shouldSortByTotalPriceAscending() {
        bookingRepository.save(new Booking(
                testCar2,
                testDriver2,
                Instant.parse("2026-03-22T09:00:00Z"),
                Instant.parse("2026-03-22T12:00:00Z"),
                InsuranceType.PREMIUM,
                totalPrice(testCar2, 3, InsuranceType.PREMIUM)
        ));

        var result = bookingRepository.searchBookings(
                null,
                null,
                null,
                null,
                PageRequest.of(0, 10, Sort.by("totalPrice").ascending())
        );

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getTotalPrice())
                .isLessThan(result.getContent().get(1).getTotalPrice());
    }

    @Test
    void searchBookings_shouldSortByTotalPriceDescending() {
        bookingRepository.save(new Booking(
                testCar2,
                testDriver2,
                Instant.parse("2026-03-22T09:00:00Z"),
                Instant.parse("2026-03-22T12:00:00Z"),
                InsuranceType.PREMIUM,
                totalPrice(testCar2, 3, InsuranceType.PREMIUM)
        ));

        var result = bookingRepository.searchBookings(
                null,
                null,
                null,
                null,
                PageRequest.of(0, 10, Sort.by("totalPrice").descending())
        );

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getTotalPrice())
                .isGreaterThan(result.getContent().get(1).getTotalPrice());
    }
}