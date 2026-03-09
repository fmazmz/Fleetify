package org.example.springmvc.bookings;

import org.example.springmvc.bookings.model.Booking;
import org.example.springmvc.insurances.InsuranceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface BookingRepository extends ListCrudRepository<Booking, UUID> {
    Page<Booking> findAll(Pageable pageable);
    Page<Booking> findByCarId(Pageable pageable, UUID carId);
    Page<Booking> findByDriverId(Pageable pageable, UUID driverId);
    Page<Booking> findByInsuranceType(Pageable pageable, InsuranceType insuranceType);


    @Query("""
SELECT COUNT(b) > 0
FROM Booking b
WHERE b.car.id = :carId
AND b.startTime < :endTime
AND b.endTime > :startTime
""")
    boolean existsOverlappingBooking(UUID carId, Instant startTime, Instant endTime);
}