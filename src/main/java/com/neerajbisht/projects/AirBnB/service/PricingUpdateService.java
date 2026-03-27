package com.neerajbisht.projects.AirBnB.service;

import com.neerajbisht.projects.AirBnB.entity.Hotel;
import com.neerajbisht.projects.AirBnB.entity.HotelMinPrice;
import com.neerajbisht.projects.AirBnB.entity.Inventory;
import com.neerajbisht.projects.AirBnB.repository.HotelMinPriceRepository;
import com.neerajbisht.projects.AirBnB.repository.HotelRepository;
import com.neerajbisht.projects.AirBnB.repository.InventoryRepository;
import com.neerajbisht.projects.AirBnB.strategy.PricingService;
import com.neerajbisht.projects.AirBnB.strategy.PricingStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService {

    //Schedular to update the inventory and HotelMinPrice tables every hour.

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    // Thread Scheduled cron expression search website for more info currently this is update
    // Every 1 hours 0->sec 0->min *->hour *->day *->week *->month.
    // if want to set every 5 sec then cron= "*/5 * * * * *". for 5 min then cron= "0 */5 * * * *".
    @Scheduled(cron = "0 0 * * * *")
    public void updatePrices(){
        int page = 0;
        int batchSize = 100;

        while(true){
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));
        if(hotelPage.isEmpty()){
            break;
        }

        hotelPage.getContent().forEach(this::updateHotelPrices);

        page++;

        }
    }

    private void updateHotelPrices(Hotel hotel){
        log.info("Updating hotel prices for hotel Id: {}",hotel.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel,startDate,endDate);

        updateInventoryPrices(inventoryList);

        updateHotelMinPrice(hotel, inventoryList, startDate, endDate);
    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));

        //Prepare HotelPrice entities in bulk
        List<HotelMinPrice> hotelMinPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price)->{
            HotelMinPrice hotelMinPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, date));
            hotelMinPrice.setPrice(price);
            hotelMinPrices.add(hotelMinPrice);
        });

        //Save all HotelPrice entities in bulk
        hotelMinPriceRepository.saveAll(hotelMinPrices);
    }

    private void  updateInventoryPrices(List<Inventory> inventoryList){
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);
    }


}
