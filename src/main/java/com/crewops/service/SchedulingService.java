package com.crewops.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.config.SchedulingProperties;
import com.crewops.dto.AutoSchedulingRequest;
import com.crewops.dto.EligibleCrewResponse;
import com.crewops.dto.SchedulingRequest;
import com.crewops.dto.SchedulingResponse;
import com.crewops.entity.Crew;
import com.crewops.entity.CrewAssignment;
import com.crewops.entity.Flight;
import com.crewops.repository.CrewAssignmentRepository;
import com.crewops.repository.CrewAvailabilityRepository;
import com.crewops.repository.CrewRepository;
import com.crewops.repository.FlightRepository;
import com.crewops.repository.LeaveRequestRepository;

@Service
public class SchedulingService {

    private final CrewAssignmentRepository crewAssignmentRepository;
    private final FlightRepository flightRepository;
    private final CrewAvailabilityRepository crewAvailabilityRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final CrewRepository crewRepository;
    private final SchedulingProperties schedulingProperties;
    private final SchedulingValidator schedulingValidator;

    public SchedulingService(
            CrewAssignmentRepository crewAssignmentRepository,
            FlightRepository flightRepository,
            CrewAvailabilityRepository crewAvailabilityRepository,
            LeaveRequestRepository leaveRequestRepository,
            CrewRepository crewRepository,
            SchedulingProperties schedulingProperties,
            SchedulingValidator schedulingValidator) {

        this.crewAssignmentRepository = crewAssignmentRepository;
        this.flightRepository = flightRepository;
        this.crewAvailabilityRepository = crewAvailabilityRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.crewRepository = crewRepository;
        this.schedulingProperties = schedulingProperties;
        this.schedulingValidator = schedulingValidator;
    }

    public List<CrewAssignment> getCrewAssignments(Long crewId) {

        return crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId);
    }

    public Flight getFlight(Long flightId) {

        return flightRepository.findById(flightId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Flight Not Found With id: "
                                        + flightId));
    }

    public boolean hasSchedulingConflict(
            Long crewId,
            Flight flight) {

        List<CrewAssignment> assignments =
                getCrewAssignments(crewId);

        for (CrewAssignment assignment : assignments) {

            boolean overlap =
                    flight.getDepartureTime()
                            .isBefore(
                                    assignment.getAssignmentEndTime())
                    &&
                    flight.getArrivalTime()
                            .isAfter(
                                    assignment.getAssignmentStartTime());

            if (overlap) {
                return true;
            }
        }

        return false;
    }

    public boolean isCrewAvailable(
            Long crewId,
            Flight flight) {

        var availabilityRecords =
                crewAvailabilityRepository
                        .findByCrewIdAndDate(
                                crewId,
                                flight.getDepartureTime()
                                        .toLocalDate());

        if (availabilityRecords.isEmpty()) {
            return false;
        }

        return availabilityRecords.stream()
                .anyMatch(availability ->
                        "AVAILABLE".equalsIgnoreCase(
                                availability.getStatus()));
    }

    public boolean isCrewOnApprovedLeave(
            Long crewId,
            Flight flight) {

        var leaveRequests =
                leaveRequestRepository
                        .findByCrewId(crewId);

        var flightDate =
                flight.getDepartureTime()
                        .toLocalDate();

        return leaveRequests.stream()
                .anyMatch(leaveRequest ->
                        "APPROVED".equalsIgnoreCase(
                                leaveRequest.getStatus())
                        &&
                        !flightDate.isBefore(
                                leaveRequest.getStartDate())
                        &&
                        !flightDate.isAfter(
                                leaveRequest.getEndDate()));
    }

    public boolean isCrewEligible(
            Long crewId,
            Long flightId) {

        Flight flight =
                getFlight(flightId);

        if (!isCrewAvailable(
                crewId,
                flight)) {

            return false;
        }

        if (isCrewOnApprovedLeave(
                crewId,
                flight)) {

            return false;
        }

        if (hasSchedulingConflict(
                crewId,
                flight)) {

            return false;
        }

        List<CrewAssignment> assignments =
                getCrewAssignments(crewId);

        schedulingValidator.validateAssignmentTime(
                flight.getDepartureTime(),
                flight.getArrivalTime());

        schedulingValidator.validateMinimumRestPeriod(
                assignments,
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                null,
                schedulingProperties
                        .getMinimumRestHours());

        schedulingValidator.validateMaximumDutyHours(
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                schedulingProperties
                        .getMaximumDutyHours());

        schedulingValidator.validateMaximumConsecutiveDutyDays(
                assignments,
                flight.getDepartureTime(),
                null,
                schedulingProperties
                        .getMaximumConsecutiveDutyDays());

        return true;
    }

    public SchedulingResponse createAssignment(
            SchedulingRequest schedulingRequest) {

        Long crewId =
                schedulingRequest.getCrewId();

        Long flightId =
                schedulingRequest.getFlightId();

        String assignmentRole =
                schedulingRequest.getAssignmentRole();

        Flight flight =
                getFlight(flightId);

        boolean eligible =
                isCrewEligible(
                        crewId,
                        flightId);

        if (!eligible) {

            throw new IllegalArgumentException(
                    "Crew member is not eligible for this flight");
        }

        CrewAssignment crewAssignment =
                new CrewAssignment();

        crewAssignment.setCrewId(crewId);
        crewAssignment.setFlightId(flightId);
        crewAssignment.setAssignmentRole(assignmentRole);
        crewAssignment.setAssignmentStartTime(
                flight.getDepartureTime());
        crewAssignment.setAssignmentEndTime(
                flight.getArrivalTime());
        crewAssignment.setStatus("ASSIGNED");

        CrewAssignment savedAssignment =
                crewAssignmentRepository.save(
                        crewAssignment);

        SchedulingResponse response =
                new SchedulingResponse();

        response.setAssignmentId(
                savedAssignment.getId());

        response.setCrewId(
                savedAssignment.getCrewId());

        response.setFlightId(
                savedAssignment.getFlightId());

        response.setAssignmentRole(
                savedAssignment.getAssignmentRole());

        response.setStatus(
                savedAssignment.getStatus());

        response.setMessage(
                "Crew member successfully assigned to flight");

        return response;
    }

    public List<EligibleCrewResponse> findEligibleCrew(
            Long flightId,
            String role) {

        getFlight(flightId);

        List<Crew> allCrew =
                crewRepository.findAll();

        List<EligibleCrewResponse> eligibleCrew =
                new ArrayList<>();

        for (Crew crew : allCrew) {

            if (!crew.getRole().equalsIgnoreCase(role)) {
                continue;
            }

            try {

                boolean eligible =
                        isCrewEligible(
                                crew.getId(),
                                flightId);

                if (eligible) {

                    EligibleCrewResponse response =
                            new EligibleCrewResponse();

                    response.setCrewId(
                            crew.getId());

                    response.setEmployeeCode(
                            crew.getEmployeeCode());

                    response.setName(
                            crew.getName());

                    response.setRole(
                            crew.getRole());

                    response.setBaseAirport(
                            crew.getBaseAirport());

                    response.setStatus(
                            crew.getStatus());

                    eligibleCrew.add(response);
                }

            } catch (IllegalArgumentException exception) {

                // Ineligible crew is skipped.
            }
        }

        return eligibleCrew;
    }

    public SchedulingResponse autoAssignCrew(
            AutoSchedulingRequest request) {

        Long flightId =
                request.getFlightId();

        String role =
                request.getAssignmentRole();

        List<EligibleCrewResponse> eligibleCrew =
                findEligibleCrew(
                        flightId,
                        role);

        if (eligibleCrew.isEmpty()) {

            throw new IllegalArgumentException(
                    "No eligible crew available for this flight and role");
        }

        Long selectedCrewId =
                eligibleCrew.get(0).getCrewId();

        SchedulingRequest schedulingRequest =
                new SchedulingRequest();

        schedulingRequest.setCrewId(
                selectedCrewId);

        schedulingRequest.setFlightId(
                flightId);

        schedulingRequest.setAssignmentRole(
                role);

        return createAssignment(
                schedulingRequest);
    }
}