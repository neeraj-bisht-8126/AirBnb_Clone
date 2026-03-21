package com.neerajbisht.projects.AirBnB.controller;

import com.neerajbisht.projects.AirBnB.dto.BookingDTO;
import com.neerajbisht.projects.AirBnB.dto.BookingRequest;
import com.neerajbisht.projects.AirBnB.dto.GuestDTO;
import com.neerajbisht.projects.AirBnB.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDTO> initializeBooking(@RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDTO> addGuests(@PathVariable Long bookingId,
                                                @RequestBody List<GuestDTO> guestDTOList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDTOList));
    }

}
