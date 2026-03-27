package com.neerajbisht.projects.AirBnB.service;

import com.neerajbisht.projects.AirBnB.dto.HotelDTO;
import com.neerajbisht.projects.AirBnB.dto.HotelPriceDTO;
import com.neerajbisht.projects.AirBnB.dto.HotelSearchRequest;
import com.neerajbisht.projects.AirBnB.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceDTO> searchHotels(HotelSearchRequest hotelSearchRequest);
}
