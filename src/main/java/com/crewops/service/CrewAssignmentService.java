package com.crewops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.dto.CrewAssignmentRequest;
import com.crewops.dto.CrewAssignmentResponse;
import com.crewops.entity.CrewAssignment;
import com.crewops.exception.CrewAssignmentNotFoundException;
import com.crewops.repository.CrewAssignmentRepository;

@Service
public class CrewAssignmentService {

    private final CrewAssignmentRepository crewAssignmentRepository;

    public CrewAssignmentService(
            CrewAssignmentRepository crewAssignmentRepository) {

        this.crewAssignmentRepository = crewAssignmentRepository;
    }

    public CrewAssignmentResponse createCrewAssignment(
            CrewAssignmentRequest crewAssignmentRequest) {

        CrewAssignment crewAssignment = new CrewAssignment();

        crewAssignment.setCrewId(crewAssignmentRequest.getCrewId());
        crewAssignment.setFlightId(crewAssignmentRequest.getFlightId());
        crewAssignment.setAssignmentRole(
                crewAssignmentRequest.getAssignmentRole());
        crewAssignment.setAssignmentTime(
                crewAssignmentRequest.getAssignmentTime());
        crewAssignment.setStatus(crewAssignmentRequest.getStatus());

        CrewAssignment savedCrewAssignment =
                crewAssignmentRepository.save(crewAssignment);

        return mapToResponse(savedCrewAssignment);
    }

    public List<CrewAssignmentResponse> getAllCrewAssignments() {

        return crewAssignmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CrewAssignmentResponse getCrewAssignmentById(Long id) {

        CrewAssignment crewAssignment =
                crewAssignmentRepository.findById(id)
                .orElseThrow(() -> new CrewAssignmentNotFoundException(
                        "Crew Assignment Not Found With id: " + id));

        return mapToResponse(crewAssignment);
    }

    public CrewAssignmentResponse updateCrewAssignment(
            Long id,
            CrewAssignmentRequest crewAssignmentRequest) {

        CrewAssignment crewAssignment =
                crewAssignmentRepository.findById(id)
                .orElseThrow(() -> new CrewAssignmentNotFoundException(
                        "Crew Assignment Not Found With id: " + id));

        crewAssignment.setCrewId(crewAssignmentRequest.getCrewId());
        crewAssignment.setFlightId(crewAssignmentRequest.getFlightId());
        crewAssignment.setAssignmentRole(
                crewAssignmentRequest.getAssignmentRole());
        crewAssignment.setAssignmentTime(
                crewAssignmentRequest.getAssignmentTime());
        crewAssignment.setStatus(crewAssignmentRequest.getStatus());

        CrewAssignment updatedCrewAssignment =
                crewAssignmentRepository.save(crewAssignment);

        return mapToResponse(updatedCrewAssignment);
    }

    public void deleteCrewAssignment(Long id) {

        CrewAssignment crewAssignment =
                crewAssignmentRepository.findById(id)
                .orElseThrow(() -> new CrewAssignmentNotFoundException(
                        "Crew Assignment Not Found With id: " + id));

        crewAssignmentRepository.delete(crewAssignment);
    }

    private CrewAssignmentResponse mapToResponse(
            CrewAssignment crewAssignment) {

        CrewAssignmentResponse response =
                new CrewAssignmentResponse();

        response.setId(crewAssignment.getId());
        response.setCrewId(crewAssignment.getCrewId());
        response.setFlightId(crewAssignment.getFlightId());
        response.setAssignmentRole(
                crewAssignment.getAssignmentRole());
        response.setAssignmentTime(
                crewAssignment.getAssignmentTime());
        response.setStatus(crewAssignment.getStatus());

        return response;
    }
}