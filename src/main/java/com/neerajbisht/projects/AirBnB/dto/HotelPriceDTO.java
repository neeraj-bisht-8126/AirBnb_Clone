package com.neerajbisht.projects.AirBnB.dto;

import com.neerajbisht.projects.AirBnB.entity.Hotel;
import lombok.Data;

@Data
public class HotelPriceDTO {
    private Hotel hotel;
    private Double price;

}
