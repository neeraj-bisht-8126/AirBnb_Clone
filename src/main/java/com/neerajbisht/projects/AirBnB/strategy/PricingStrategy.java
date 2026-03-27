package com.neerajbisht.projects.AirBnB.strategy;

import com.neerajbisht.projects.AirBnB.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    public BigDecimal calculatePrice(Inventory inventory);











}
