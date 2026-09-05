package com.crewops.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crewops.entity.CrewAssignment;

public interface CrewAssignmentRepository
        extends JpaRepository<CrewAssignment, Long> {
	
	List<CrewAssignment> findByCrewId(Long crewId);
	
	List<CrewAssignment> findByCrewIdOrderByAssignmentStartTimeAsc(
	        Long crewId);

}