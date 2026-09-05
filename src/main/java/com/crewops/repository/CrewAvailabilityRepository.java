package com.crewops.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crewops.entity.CrewAvailability;

public interface CrewAvailabilityRepository
        extends JpaRepository<CrewAvailability, Long> {
	
	List<CrewAvailability> findByCrewIdAndDate(
            Long crewId,
            LocalDate date);
}