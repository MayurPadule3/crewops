package com.crewops.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crewops.entity.CrewAssignment;

public interface CrewAssignmentRepository
        extends JpaRepository<CrewAssignment, Long> {

}