package com.neerajbisht.projects.AirBnB.service;

import com.neerajbisht.projects.AirBnB.dto.BookingDTO;
import com.neerajbisht.projects.AirBnB.dto.BookingRequest;
import com.neerajbisht.projects.AirBnB.dto.GuestDTO;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface BookingService {
    BookingDTO initializeBooking(BookingRequest bookingRequest);

    BookingDTO addGuests(Long bookingId, List<GuestDTO> guestDTOList);
}
