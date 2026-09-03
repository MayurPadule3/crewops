package com.crewops.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crewops.entity.Airport;

public interface AirportRepository extends JpaRepository<Airport, Long >{

}
