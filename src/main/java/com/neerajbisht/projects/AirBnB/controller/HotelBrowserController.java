package com.neerajbisht.projects.AirBnB.controller;


import com.neerajbisht.projects.AirBnB.dto.HotelDTO;
import com.neerajbisht.projects.AirBnB.dto.HotelInfoDTO;
import com.neerajbisht.projects.AirBnB.dto.HotelSearchRequest;
import com.neerajbisht.projects.AirBnB.service.HotelService;
import com.neerajbisht.projects.AirBnB.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowserController {

    private final InventoryService inventoryService;
private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDTO>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){
        Page<HotelDTO> page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelInfoDTO> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
