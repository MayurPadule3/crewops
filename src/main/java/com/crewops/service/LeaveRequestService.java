package com.crewops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.dto.LeaveRequestRequest;
import com.crewops.dto.LeaveRequestResponse;
import com.crewops.entity.LeaveRequest;
import com.crewops.exception.LeaveRequestNotFoundException;
import com.crewops.repository.LeaveRequestRepository;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    public LeaveRequestResponse createLeaveRequest(
            LeaveRequestRequest leaveRequestRequest) {
    	
    	if (leaveRequestRequest.getEndDate()
    	        .isBefore(leaveRequestRequest.getStartDate())) {

    	    throw new IllegalArgumentException(
    	            "End date must be on or after start date");
    	}

        LeaveRequest leaveRequest = new LeaveRequest();

        leaveRequest.setCrewId(leaveRequestRequest.getCrewId());
        leaveRequest.setStartDate(leaveRequestRequest.getStartDate());
        leaveRequest.setEndDate(leaveRequestRequest.getEndDate());
        leaveRequest.setReason(leaveRequestRequest.getReason());
        leaveRequest.setStatus(leaveRequestRequest.getStatus());

        LeaveRequest savedLeaveRequest =
                leaveRequestRepository.save(leaveRequest);

        return mapToResponse(savedLeaveRequest);
    }

    public List<LeaveRequestResponse> getAllLeaveRequests() {

        return leaveRequestRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public LeaveRequestResponse getLeaveRequestById(Long id) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveRequestNotFoundException(
                        "Leave Request Not Found With id: " + id));

        return mapToResponse(leaveRequest);
    }

    public LeaveRequestResponse updateLeaveRequest(
            Long id,
            LeaveRequestRequest leaveRequestRequest) {
    	
    	if (leaveRequestRequest.getEndDate()
    	        .isBefore(leaveRequestRequest.getStartDate())) {

    	    throw new IllegalArgumentException(
    	            "End date must be on or after start date");
    	}

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveRequestNotFoundException(
                        "Leave Request Not Found With id: " + id));

        leaveRequest.setCrewId(leaveRequestRequest.getCrewId());
        leaveRequest.setStartDate(leaveRequestRequest.getStartDate());
        leaveRequest.setEndDate(leaveRequestRequest.getEndDate());
        leaveRequest.setReason(leaveRequestRequest.getReason());
        leaveRequest.setStatus(leaveRequestRequest.getStatus());

        LeaveRequest updatedLeaveRequest =
                leaveRequestRepository.save(leaveRequest);

        return mapToResponse(updatedLeaveRequest);
    }

    public void deleteLeaveRequest(Long id) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveRequestNotFoundException(
                        "Leave Request Not Found With id: " + id));

        leaveRequestRepository.delete(leaveRequest);
    }

    private LeaveRequestResponse mapToResponse(
            LeaveRequest leaveRequest) {

        LeaveRequestResponse response = new LeaveRequestResponse();

        response.setId(leaveRequest.getId());
        response.setCrewId(leaveRequest.getCrewId());
        response.setStartDate(leaveRequest.getStartDate());
        response.setEndDate(leaveRequest.getEndDate());
        response.setReason(leaveRequest.getReason());
        response.setStatus(leaveRequest.getStatus());

        return response;
    }
}