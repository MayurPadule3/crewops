package com.crewops.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crewops.entity.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {
	
	

}
