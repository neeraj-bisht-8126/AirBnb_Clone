package com.neerajbisht.projects.AirBnB.service;

import com.neerajbisht.projects.AirBnB.dto.RoomDTO;
import com.neerajbisht.projects.AirBnB.entity.Hotel;
import com.neerajbisht.projects.AirBnB.entity.Room;
import com.neerajbisht.projects.AirBnB.exception.ResourceNotFoundException;
import com.neerajbisht.projects.AirBnB.repository.HotelRepository;
import com.neerajbisht.projects.AirBnB.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService{

    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    @Override
    public RoomDTO createNewRoom(Long hotelId, RoomDTO roomDTO) {
        log.info("Creating a new room in hotel with Id: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()->
                        new ResourceNotFoundException("hotel not found with Id : "+hotelId));
        Room room = modelMapper.map(roomDTO, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        if(hotel.getActive()){
            inventoryService.initializeRoomForYear(room);
        }

        return modelMapper.map(room, RoomDTO.class);
    }

    @Override
    public List<RoomDTO> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with Id: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Hotel not found with Id: "+hotelId));

       return hotel.getRooms()
                .stream()
                .map(room->
                        modelMapper.map(room, RoomDTO.class)
                        ).collect(Collectors.toList());
    }

    @Override
    public RoomDTO getRoomById(Long roomId) {
        log.info("Getting the room with id: {}",roomId);
        Room room = roomRepository
                                .findById(roomId)
                                .orElseThrow(()->
                                        new ResourceNotFoundException("Room not found with id: "+ roomId));
        return modelMapper.map(room, RoomDTO.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room with id: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Room not found with id: "+ roomId));
    inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);

    }
}
