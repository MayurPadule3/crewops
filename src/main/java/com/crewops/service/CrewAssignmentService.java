package com.crewops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.config.SchedulingProperties;
import com.crewops.dto.CrewAssignmentRequest;
import com.crewops.dto.CrewAssignmentResponse;
import com.crewops.entity.CrewAssignment;
import com.crewops.exception.CrewAssignmentNotFoundException;
import com.crewops.repository.CrewAssignmentRepository;
import com.crewops.repository.CrewAvailabilityRepository;
import com.crewops.repository.LeaveRequestRepository;

@Service
public class CrewAssignmentService {

    private final CrewAssignmentRepository crewAssignmentRepository;

    private final SchedulingService schedulingService;

    private final LeaveRequestRepository leaveRequestRepository;

    private final CrewAvailabilityRepository crewAvailabilityRepository;

    private final SchedulingProperties schedulingProperties;

    private final SchedulingValidator schedulingValidator;

    public CrewAssignmentService(
            CrewAssignmentRepository crewAssignmentRepository,
            LeaveRequestRepository leaveRequestRepository,
            CrewAvailabilityRepository crewAvailabilityRepository,
            SchedulingService schedulingService,
            SchedulingProperties schedulingProperties,
            SchedulingValidator schedulingValidator) {

        this.crewAssignmentRepository = crewAssignmentRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.crewAvailabilityRepository = crewAvailabilityRepository;
        this.schedulingService = schedulingService;
        this.schedulingProperties = schedulingProperties;
        this.schedulingValidator = schedulingValidator;
    }

    public CrewAssignmentResponse createCrewAssignment(
            CrewAssignmentRequest crewAssignmentRequest) {

        // Rule 1: Assignment end time must be after start time

        if (crewAssignmentRequest.getAssignmentEndTime()
                .isBefore(crewAssignmentRequest.getAssignmentStartTime())) {

            throw new IllegalArgumentException(
                    "Assignment end time must be after start time");
        }

        // Rule 2: Check whether crew member is on approved leave

        var leaveRequests = leaveRequestRepository.findByCrewId(
                crewAssignmentRequest.getCrewId());

        for (var leaveRequest : leaveRequests) {

            if ("APPROVED".equalsIgnoreCase(leaveRequest.getStatus())
                    && !crewAssignmentRequest.getAssignmentStartTime()
                            .toLocalDate()
                            .isAfter(leaveRequest.getEndDate())
                    && !crewAssignmentRequest.getAssignmentEndTime()
                            .toLocalDate()
                            .isBefore(leaveRequest.getStartDate())) {

                throw new IllegalArgumentException(
                        "Crew member is on approved leave during this assignment");
            }
        }

        // Rule 3: Check crew availability

        var availabilityRecords =
                crewAvailabilityRepository.findByCrewIdAndDate(
                        crewAssignmentRequest.getCrewId(),
                        crewAssignmentRequest.getAssignmentStartTime()
                                .toLocalDate());

        if (availabilityRecords.isEmpty()
                || !"AVAILABLE".equalsIgnoreCase(
                        availabilityRecords.get(0).getStatus())) {

            throw new IllegalArgumentException(
                    "Crew member is not available on the assignment date");
        }

        // Rule 4: Check whether crew already has an overlapping assignment

        boolean crewAvailable = schedulingService.isCrewAvailable(
                crewAssignmentRequest.getCrewId(),
                crewAssignmentRequest.getAssignmentStartTime(),
                crewAssignmentRequest.getAssignmentEndTime());

        if (!crewAvailable) {

            throw new IllegalArgumentException(
                    "Crew member already has an overlapping assignment");
        }

        // Rule 5: Check minimum rest period

        List<CrewAssignment> existingAssignments =
                crewAssignmentRepository.findByCrewId(
                        crewAssignmentRequest.getCrewId());

        for (CrewAssignment assignment : existingAssignments) {

            schedulingValidator.validateMinimumRestPeriod(
                    crewAssignmentRequest.getAssignmentStartTime(),
                    crewAssignmentRequest.getAssignmentEndTime(),
                    assignment.getAssignmentStartTime(),
                    assignment.getAssignmentEndTime(),
                    schedulingProperties.getMinimumRestHours());
        }

        // Rule 6: Check maximum duty hours

        schedulingValidator.validateMaximumDutyHours(
                crewAssignmentRequest.getAssignmentStartTime(),
                crewAssignmentRequest.getAssignmentEndTime(),
                schedulingProperties.getMaximumDutyHours());

        // Rule 7: Check maximum consecutive duty days

        schedulingValidator.validateMaximumConsecutiveDutyDays(
                existingAssignments,
                crewAssignmentRequest.getAssignmentStartTime(),
                schedulingProperties.getMaximumConsecutiveDutyDays());

        // Create assignment

        CrewAssignment crewAssignment = new CrewAssignment();

        crewAssignment.setCrewId(
                crewAssignmentRequest.getCrewId());

        crewAssignment.setFlightId(
                crewAssignmentRequest.getFlightId());

        crewAssignment.setAssignmentRole(
                crewAssignmentRequest.getAssignmentRole());

        crewAssignment.setAssignmentStartTime(
                crewAssignmentRequest.getAssignmentStartTime());

        crewAssignment.setAssignmentEndTime(
                crewAssignmentRequest.getAssignmentEndTime());

        crewAssignment.setStatus(
                crewAssignmentRequest.getStatus());

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
                .orElseThrow(() ->
                        new CrewAssignmentNotFoundException(
                                "Crew Assignment Not Found With id: " + id));

        return mapToResponse(crewAssignment);
    }

    public CrewAssignmentResponse updateCrewAssignment(
            Long id,
            CrewAssignmentRequest crewAssignmentRequest) {

        // Rule: Assignment end time must be after start time

        if (crewAssignmentRequest.getAssignmentEndTime()
                .isBefore(crewAssignmentRequest.getAssignmentStartTime())) {

            throw new IllegalArgumentException(
                    "Assignment end time must be after start time");
        }

        CrewAssignment crewAssignment =
                crewAssignmentRepository.findById(id)
                .orElseThrow(() ->
                        new CrewAssignmentNotFoundException(
                                "Crew Assignment Not Found With id: " + id));

        crewAssignment.setCrewId(
                crewAssignmentRequest.getCrewId());

        crewAssignment.setFlightId(
                crewAssignmentRequest.getFlightId());

        crewAssignment.setAssignmentRole(
                crewAssignmentRequest.getAssignmentRole());

        crewAssignment.setAssignmentStartTime(
                crewAssignmentRequest.getAssignmentStartTime());

        crewAssignment.setAssignmentEndTime(
                crewAssignmentRequest.getAssignmentEndTime());

        crewAssignment.setStatus(
                crewAssignmentRequest.getStatus());

        CrewAssignment updatedCrewAssignment =
                crewAssignmentRepository.save(crewAssignment);

        return mapToResponse(updatedCrewAssignment);
    }

    public void deleteCrewAssignment(Long id) {

        CrewAssignment crewAssignment =
                crewAssignmentRepository.findById(id)
                .orElseThrow(() ->
                        new CrewAssignmentNotFoundException(
                                "Crew Assignment Not Found With id: " + id));

        crewAssignmentRepository.delete(crewAssignment);
    }

    private CrewAssignmentResponse mapToResponse(
            CrewAssignment crewAssignment) {

        CrewAssignmentResponse response =
                new CrewAssignmentResponse();

        response.setId(
                crewAssignment.getId());

        response.setCrewId(
                crewAssignment.getCrewId());

        response.setFlightId(
                crewAssignment.getFlightId());

        response.setAssignmentRole(
                crewAssignment.getAssignmentRole());

        response.setAssignmentStartTime(
                crewAssignment.getAssignmentStartTime());

        response.setAssignmentEndTime(
                crewAssignment.getAssignmentEndTime());

        response.setStatus(
                crewAssignment.getStatus());

        return response;
    }
}