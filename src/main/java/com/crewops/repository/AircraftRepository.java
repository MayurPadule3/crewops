package com.crewops.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crewops.entity.Aircraft;

public interface AircraftRepository extends JpaRepository<Aircraft, Long>{

}
