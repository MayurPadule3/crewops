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

    private final LeaveRequestRepository leaveRequestRepository;

    private final CrewAvailabilityRepository crewAvailabilityRepository;

    private final SchedulingProperties schedulingProperties;

    private final SchedulingValidator schedulingValidator;

    public CrewAssignmentService(
            CrewAssignmentRepository crewAssignmentRepository,
            LeaveRequestRepository leaveRequestRepository,
            CrewAvailabilityRepository crewAvailabilityRepository,
            SchedulingProperties schedulingProperties,
            SchedulingValidator schedulingValidator) {

        this.crewAssignmentRepository = crewAssignmentRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.crewAvailabilityRepository = crewAvailabilityRepository;
        this.schedulingProperties = schedulingProperties;
        this.schedulingValidator = schedulingValidator;
    }

    public CrewAssignmentResponse createCrewAssignment(
            CrewAssignmentRequest crewAssignmentRequest) {

        // Rule 1: Assignment time

        schedulingValidator.validateAssignmentTime(
                crewAssignmentRequest.getAssignmentStartTime(),
                crewAssignmentRequest.getAssignmentEndTime());

        // Rule 2: Approved leave

        validateApprovedLeave(crewAssignmentRequest);

        // Rule 3: Crew availability

        validateCrewAvailability(crewAssignmentRequest);

        // Get existing assignments

        List<CrewAssignment> existingAssignments =
                crewAssignmentRepository.findByCrewId(
                        crewAssignmentRequest.getCrewId());

        // Rule 4: Overlap

        schedulingValidator.validateNoOverlap(
                existingAssignments,
                crewAssignmentRequest.getAssignmentStartTime(),
                crewAssignmentRequest.getAssignmentEndTime(),
                null);

        // Rule 5: Minimum rest

        schedulingValidator.validateMinimumRestPeriod(
                existingAssignments,
                crewAssignmentRequest.getAssignmentStartTime(),
                crewAssignmentRequest.getAssignmentEndTime(),
                null,
                schedulingProperties.getMinimumRestHours());

        // Rule 6: Maximum duty hours

        schedulingValidator.validateMaximumDutyHours(
                crewAssignmentRequest.getAssignmentStartTime(),
                crewAssignmentRequest.getAssignmentEndTime(),
                schedulingProperties.getMaximumDutyHours());

        // Rule 7: Maximum consecutive duty days

        schedulingValidator.validateMaximumConsecutiveDutyDays(
                existingAssignments,
                crewAssignmentRequest.getAssignmentStartTime(),
                null,
                schedulingProperties.getMaximumConsecutiveDutyDays());

        // Create assignment

        CrewAssignment crewAssignment =
                new CrewAssignment();

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
                crewAssignmentRepository.save(
                        crewAssignment);

        return mapToResponse(savedCrewAssignment);
    }

    public List<CrewAssignmentResponse> getAllCrewAssignments() {

        return crewAssignmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CrewAssignmentResponse getCrewAssignmentById(
            Long id) {

        CrewAssignment crewAssignment =
                crewAssignmentRepository.findById(id)
                .orElseThrow(() ->
                        new CrewAssignmentNotFoundException(
                                "Crew Assignment Not Found With id: "
                                + id));

        return mapToResponse(crewAssignment);
    }

    public CrewAssignmentResponse updateCrewAssignment(
            Long id,
            CrewAssignmentRequest crewAssignmentRequest) {

        // Find existing assignment

        CrewAssignment crewAssignment =
                crewAssignmentRepository.findById(id)
                .orElseThrow(() ->
                        new CrewAssignmentNotFoundException(
                                "Crew Assignment Not Found With id: "
                                + id));

        // Rule 1: Assignment time

        schedulingValidator.validateAssignmentTime(
                crewAssignmentRequest.getAssignmentStartTime(),
                crewAssignmentRequest.getAssignmentEndTime());

        // Rule 2: Approved leave

        validateApprovedLeave(crewAssignmentRequest);

        // Rule 3: Crew availability

        validateCrewAvailability(crewAssignmentRequest);

        // Get all assignments for this crew member

        List<CrewAssignment> existingAssignments =
                crewAssignmentRepository.findByCrewId(
                        crewAssignmentRequest.getCrewId());

        // Rule 4: Overlap
        // Exclude the assignment currently being updated

        schedulingValidator.validateNoOverlap(
                existingAssignments,
                crewAssignmentRequest.getAssignmentStartTime(),
                crewAssignmentRequest.getAssignmentEndTime(),
                id);

        // Rule 5: Minimum rest
        // Exclude the assignment currently being updated

        schedulingValidator.validateMinimumRestPeriod(
                existingAssignments,
                crewAssignmentRequest.getAssignmentStartTime(),
                crewAssignmentRequest.getAssignmentEndTime(),
                id,
                schedulingProperties.getMinimumRestHours());

        // Rule 6: Maximum duty hours

        schedulingValidator.validateMaximumDutyHours(
                crewAssignmentRequest.getAssignmentStartTime(),
                crewAssignmentRequest.getAssignmentEndTime(),
                schedulingProperties.getMaximumDutyHours());

        // Rule 7: Maximum consecutive duty days
        // Exclude the assignment currently being updated

        schedulingValidator.validateMaximumConsecutiveDutyDays(
                existingAssignments,
                crewAssignmentRequest.getAssignmentStartTime(),
                id,
                schedulingProperties.getMaximumConsecutiveDutyDays());

        // Update assignment

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
                crewAssignmentRepository.save(
                        crewAssignment);

        return mapToResponse(updatedCrewAssignment);
    }

    public void deleteCrewAssignment(Long id) {

        CrewAssignment crewAssignment =
                crewAssignmentRepository.findById(id)
                .orElseThrow(() ->
                        new CrewAssignmentNotFoundException(
                                "Crew Assignment Not Found With id: "
                                + id));

        crewAssignmentRepository.delete(crewAssignment);
    }

    private void validateApprovedLeave(
            CrewAssignmentRequest crewAssignmentRequest) {

        var leaveRequests =
                leaveRequestRepository.findByCrewId(
                        crewAssignmentRequest.getCrewId());

        for (var leaveRequest : leaveRequests) {

            if ("APPROVED".equalsIgnoreCase(
                    leaveRequest.getStatus())
                    && !crewAssignmentRequest
                            .getAssignmentStartTime()
                            .toLocalDate()
                            .isAfter(
                                    leaveRequest.getEndDate())
                    && !crewAssignmentRequest
                            .getAssignmentEndTime()
                            .toLocalDate()
                            .isBefore(
                                    leaveRequest.getStartDate())) {

                throw new IllegalArgumentException(
                        "Crew member is on approved leave during this assignment");
            }
        }
    }

    private void validateCrewAvailability(
            CrewAssignmentRequest crewAssignmentRequest) {

        var availabilityRecords =
                crewAvailabilityRepository
                        .findByCrewIdAndDate(
                                crewAssignmentRequest.getCrewId(),
                                crewAssignmentRequest
                                        .getAssignmentStartTime()
                                        .toLocalDate());

        if (availabilityRecords.isEmpty()
                || !"AVAILABLE".equalsIgnoreCase(
                        availabilityRecords
                                .get(0)
                                .getStatus())) {

            throw new IllegalArgumentException(
                    "Crew member is not available on the assignment date");
        }
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
                crewAssignment
                        .getAssignmentStartTime());

        response.setAssignmentEndTime(
                crewAssignment
                        .getAssignmentEndTime());

        response.setStatus(
                crewAssignment.getStatus());

        return response;
    }
}