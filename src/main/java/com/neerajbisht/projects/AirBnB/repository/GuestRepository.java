package com.neerajbisht.projects.AirBnB.repository;

import com.neerajbisht.projects.AirBnB.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}