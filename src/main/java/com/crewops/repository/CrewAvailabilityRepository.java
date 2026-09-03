package com.crewops.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crewops.entity.CrewAvailability;

public interface CrewAvailabilityRepository
        extends JpaRepository<CrewAvailability, Long> {
}