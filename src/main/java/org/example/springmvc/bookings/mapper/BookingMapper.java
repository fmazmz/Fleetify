package org.example.springmvc.bookings.mapper;

import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.bookings.model.Booking;


public class BookingMapper {

    public static BookingDTO toDto(Booking booking) {
        return new BookingDTO(
                booking.getDriver().getId(),
                booking.getCar().getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getInsuranceType(),
                booking.getTotalPrice());
    }
}