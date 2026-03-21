package com.neerajbisht.projects.AirBnB.repository;

import com.neerajbisht.projects.AirBnB.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
