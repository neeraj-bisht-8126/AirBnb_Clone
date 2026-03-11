package com.neerajbisht.projects.AirBnB.service;

import com.neerajbisht.projects.AirBnB.dto.HotelDTO;
import com.neerajbisht.projects.AirBnB.dto.HotelInfoDTO;
import org.jspecify.annotations.Nullable;

public interface HotelService {
    HotelDTO createNewHotel(HotelDTO hotelDTO);

    HotelDTO getHotelById(Long id);

    HotelDTO updateHotelById(Long id, HotelDTO hotelDTO);

    void deleteHotelById(Long id);

    void activateHotel(Long id);

    HotelInfoDTO getHotelInfoById(Long hotelId);
}
