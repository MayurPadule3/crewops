package com.crewops.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crewops.entity.LeaveRequest;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long>{

}
