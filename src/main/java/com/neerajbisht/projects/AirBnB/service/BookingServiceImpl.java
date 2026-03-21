package com.neerajbisht.projects.AirBnB.service;

import com.neerajbisht.projects.AirBnB.dto.BookingDTO;
import com.neerajbisht.projects.AirBnB.dto.BookingRequest;
import com.neerajbisht.projects.AirBnB.dto.GuestDTO;
import com.neerajbisht.projects.AirBnB.entity.*;
import com.neerajbisht.projects.AirBnB.entity.enums.BookingStatus;
import com.neerajbisht.projects.AirBnB.exception.ResourceNotFoundException;
import com.neerajbisht.projects.AirBnB.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final GuestRepository guestRepository;

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BookingDTO initializeBooking(BookingRequest bookingRequest) {

        log.info("Initializing booking for hotel: {}, room: {}, date {}-{}",bookingRequest.getHotelId(),bookingRequest.getRoomId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate());

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found with id "+bookingRequest.getHotelId()));

        Room room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(()->
                new ResourceNotFoundException("Room not found with id "+bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId()
        ,bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate())+1;

        if(inventoryList.size() != daysCount){
            throw new IllegalStateException("Room is not available anymore.");
        }

        // Reserve the room update the booked count of inventories.

        for(Inventory inventory: inventoryList){
            inventory.setReservedCount(inventory.getReservedCount()+ bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);

        //create the booking.

        //TODO : calculate dynamic amount

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(BigDecimal.TEN)
                .build();

        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);

    }

    @Override
    @Transactional
    public BookingDTO addGuests(Long bookingId, List<GuestDTO> guestDTOList) {
        log.info("Adding guests for booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with id: "+bookingId));

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired.");
        }
        if(booking.getBookingStatus()!= BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests.");
        }

        for(GuestDTO guestDTO : guestDTOList){
            Guest guest = modelMapper.map(guestDTO, Guest.class);
            guest.setUser(getCurrentUser());
            guest = guestRepository.save(guest);
            booking.getGuest().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);
    }


    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser(){
        User user = new User();
        user.setId(1L); // TODO: REMOVE DUMMY USER
        return user;
    }





}
