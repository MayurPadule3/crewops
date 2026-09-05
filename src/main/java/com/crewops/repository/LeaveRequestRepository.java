package com.crewops.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crewops.entity.LeaveRequest;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long>{
	
	List<LeaveRequest> findByCrewId(Long crewId);

}
