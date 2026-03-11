package com.neerajbisht.projects.AirBnB.service;

import com.neerajbisht.projects.AirBnB.dto.HotelDTO;
import com.neerajbisht.projects.AirBnB.dto.HotelInfoDTO;
import com.neerajbisht.projects.AirBnB.dto.RoomDTO;
import com.neerajbisht.projects.AirBnB.entity.Hotel;
import com.neerajbisht.projects.AirBnB.entity.Room;
import com.neerajbisht.projects.AirBnB.exception.ResourceNotFoundException;
import com.neerajbisht.projects.AirBnB.repository.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomService roomService;

    @Override
    public HotelDTO createNewHotel(HotelDTO hotelDTO) {
        log.info("Creating a new hotel with name: {}", hotelDTO.getName());
        Hotel hotel= modelMapper.map(hotelDTO,Hotel.class);
        hotel.setActive(false);
        hotel = hotelRepository.save(hotel);
        log.info("Created a new hotel with ID: {}", hotelDTO.getId());
        return modelMapper.map(hotel, HotelDTO.class);
    }

    @Override
    public HotelDTO getHotelById(Long id) {
        log.info("Getting the hotel with Id: {}", id);
        Hotel hotel =hotelRepository
                .findById(id)
                .orElseThrow(()->
                new ResourceNotFoundException("Hotel not found with Id: "+ id));
        return modelMapper.map(hotel, HotelDTO.class);
    }

    @Override
    public HotelDTO updateHotelById(Long id, HotelDTO hotelDTO) {
    log.info("Uddating the hotel with Id: {}",id);
    Hotel hotel = hotelRepository
            .findById(id)
            .orElseThrow(()->
                    new ResourceNotFoundException("Hotel not found with Id: "+id));
    modelMapper.map(hotelDTO,hotel);
    hotel.setId(id);
    hotel = hotelRepository.save(hotel);
    return modelMapper.map(hotel, HotelDTO.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found with id: "+id));

        for(Room room: hotel.getRooms()){

// This will handle by deleteRoomById method of RoomServiceImpl. we already initialized inventory delete by room inside Room Deletion.
//            inventoryService.deleteAllInventories(room);

            roomService.deleteRoomById(room.getId());
        }

        hotelRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("Activating the hotel with Id: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Hotel not found with Id: "+hotelId)
                );
        hotel.setActive(true);

        for (Room room: hotel.getRooms()){
            inventoryService.initializeRoomForYear(room);
        }
    }

    @Override
    public HotelInfoDTO getHotelInfoById(Long hotelId) {
       Hotel hotel = hotelRepository
               .findById(hotelId)
               .orElseThrow(()->
                       new ResourceNotFoundException("Hotel not found with Id:"+ hotelId));
        List<RoomDTO> rooms = hotel.getRooms()
                .stream()
                .map(room->
                        modelMapper.map(room, RoomDTO.class))
                .toList();

        return new HotelInfoDTO(modelMapper.map(hotel,HotelDTO.class),rooms);
    }
}
