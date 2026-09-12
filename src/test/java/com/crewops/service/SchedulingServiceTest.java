package com.crewops.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.crewops.config.SchedulingProperties;
import com.crewops.entity.CrewAssignment;
import com.crewops.entity.CrewAvailability;
import com.crewops.entity.Flight;
import com.crewops.entity.LeaveRequest;
import com.crewops.repository.CrewAssignmentRepository;
import com.crewops.repository.CrewAvailabilityRepository;
import com.crewops.repository.FlightRepository;
import com.crewops.repository.LeaveRequestRepository;

class SchedulingServiceTest {

    @Mock
    private CrewAssignmentRepository crewAssignmentRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private CrewAvailabilityRepository crewAvailabilityRepository;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private SchedulingProperties schedulingProperties;

    private SchedulingValidator schedulingValidator;

    private SchedulingService schedulingService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        schedulingValidator = new SchedulingValidator();

        schedulingService = new SchedulingService(
                crewAssignmentRepository,
                flightRepository,
                crewAvailabilityRepository,
                leaveRequestRepository,
                schedulingProperties,
                schedulingValidator);
    }

    // =========================================================
    // TEST 1
    // =========================================================

    @Test
    void shouldDetectSchedulingConflict() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setId(1L);
        flight.setFlightNumber("AI101");
        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 10, 0));
        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 14, 0));

        CrewAssignment assignment = new CrewAssignment();

        assignment.setId(1L);
        assignment.setCrewId(crewId);
        assignment.setFlightId(2L);
        assignment.setAssignmentStartTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));
        assignment.setAssignmentEndTime(
                LocalDateTime.of(2026, 9, 26, 16, 0));

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of(assignment));

        boolean result =
                schedulingService.hasSchedulingConflict(
                        crewId,
                        flight);

        assertTrue(result);
    }

    // =========================================================
    // TEST 2
    // =========================================================

    @Test
    void shouldNotDetectSchedulingConflictWhenFlightDoesNotOverlap() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setId(1L);
        flight.setFlightNumber("AI101");
        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 8, 0));
        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));

        CrewAssignment assignment = new CrewAssignment();

        assignment.setId(1L);
        assignment.setCrewId(crewId);
        assignment.setFlightId(2L);
        assignment.setAssignmentStartTime(
                LocalDateTime.of(2026, 9, 26, 14, 0));
        assignment.setAssignmentEndTime(
                LocalDateTime.of(2026, 9, 26, 18, 0));

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of(assignment));

        boolean result =
                schedulingService.hasSchedulingConflict(
                        crewId,
                        flight);

        assertFalse(result);
    }

    // =========================================================
    // TEST 3
    // =========================================================

    @Test
    void shouldReturnTrueWhenCrewIsAvailable() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 8, 0));
        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));

        CrewAvailability availability =
                new CrewAvailability();

        availability.setCrewId(crewId);
        availability.setDate(
                LocalDate.of(2026, 9, 26));
        availability.setStatus("AVAILABLE");

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 26)))
                .thenReturn(List.of(availability));

        boolean result =
                schedulingService.isCrewAvailable(
                        crewId,
                        flight);

        assertTrue(result);
    }

    // =========================================================
    // TEST 4
    // =========================================================

    @Test
    void shouldReturnFalseWhenCrewIsUnavailable() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 8, 0));
        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));

        CrewAvailability availability =
                new CrewAvailability();

        availability.setCrewId(crewId);
        availability.setDate(
                LocalDate.of(2026, 9, 26));
        availability.setStatus("UNAVAILABLE");

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 26)))
                .thenReturn(List.of(availability));

        boolean result =
                schedulingService.isCrewAvailable(
                        crewId,
                        flight);

        assertFalse(result);
    }

    // =========================================================
    // TEST 5
    // =========================================================

    @Test
    void shouldReturnFalseWhenAvailabilityRecordDoesNotExist() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 8, 0));
        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 26)))
                .thenReturn(List.of());

        boolean result =
                schedulingService.isCrewAvailable(
                        crewId,
                        flight);

        assertFalse(result);
    }

    // =========================================================
    // TEST 6
    // =========================================================

    @Test
    void shouldReturnTrueWhenCrewIsOnApprovedLeave() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 8, 0));
        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));

        LeaveRequest leaveRequest =
                new LeaveRequest();

        leaveRequest.setCrewId(crewId);
        leaveRequest.setStartDate(
                LocalDate.of(2026, 9, 25));
        leaveRequest.setEndDate(
                LocalDate.of(2026, 9, 27));
        leaveRequest.setStatus("APPROVED");

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of(leaveRequest));

        boolean result =
                schedulingService.isCrewOnApprovedLeave(
                        crewId,
                        flight);

        assertTrue(result);
    }

    // =========================================================
    // TEST 7
    // =========================================================

    @Test
    void shouldReturnFalseWhenCrewIsNotOnApprovedLeave() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 8, 0));
        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));

        LeaveRequest leaveRequest =
                new LeaveRequest();

        leaveRequest.setCrewId(crewId);
        leaveRequest.setStartDate(
                LocalDate.of(2026, 9, 20));
        leaveRequest.setEndDate(
                LocalDate.of(2026, 9, 22));
        leaveRequest.setStatus("APPROVED");

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of(leaveRequest));

        boolean result =
                schedulingService.isCrewOnApprovedLeave(
                        crewId,
                        flight);

        assertFalse(result);
    }

    // =========================================================
    // TEST 8
    // =========================================================

    @Test
    void shouldReturnTrueWhenCrewIsEligible() {

        Long crewId = 1L;
        Long flightId = 1L;

        Flight flight = new Flight();

        flight.setId(flightId);
        flight.setFlightNumber("AI101");

        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 8, 0));

        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));

        when(flightRepository
                .findById(flightId))
                .thenReturn(Optional.of(flight));

        CrewAvailability availability =
                new CrewAvailability();

        availability.setCrewId(crewId);
        availability.setDate(
                LocalDate.of(2026, 9, 26));
        availability.setStatus("AVAILABLE");

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 26)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of());

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of());

        when(schedulingProperties
                .getMinimumRestHours())
                .thenReturn(10);

        when(schedulingProperties
                .getMaximumDutyHours())
                .thenReturn(12);

        when(schedulingProperties
                .getMaximumConsecutiveDutyDays())
                .thenReturn(6);

        boolean result =
                schedulingService.isCrewEligible(
                        crewId,
                        flightId);

        assertTrue(result);
    }

    // =========================================================
    // TEST 9
    // =========================================================

    @Test
    void shouldReturnFalseWhenCrewIsUnavailableForEligibility() {

        Long crewId = 1L;
        Long flightId = 1L;

        Flight flight = new Flight();

        flight.setId(flightId);

        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 8, 0));

        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));

        when(flightRepository
                .findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 26)))
                .thenReturn(List.of());

        boolean result =
                schedulingService.isCrewEligible(
                        crewId,
                        flightId);

        assertFalse(result);
    }

    // =========================================================
    // TEST 10
    // =========================================================

    @Test
    void shouldReturnFalseWhenCrewIsOnApprovedLeaveForEligibility() {

        Long crewId = 1L;
        Long flightId = 1L;

        Flight flight = new Flight();

        flight.setId(flightId);

        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 8, 0));

        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));

        when(flightRepository
                .findById(flightId))
                .thenReturn(Optional.of(flight));

        CrewAvailability availability =
                new CrewAvailability();

        availability.setCrewId(crewId);
        availability.setDate(
                LocalDate.of(2026, 9, 26));
        availability.setStatus("AVAILABLE");

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 26)))
                .thenReturn(List.of(availability));

        LeaveRequest leaveRequest =
                new LeaveRequest();

        leaveRequest.setCrewId(crewId);
        leaveRequest.setStartDate(
                LocalDate.of(2026, 9, 25));
        leaveRequest.setEndDate(
                LocalDate.of(2026, 9, 27));
        leaveRequest.setStatus("APPROVED");

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of(leaveRequest));

        boolean result =
                schedulingService.isCrewEligible(
                        crewId,
                        flightId);

        assertFalse(result);
    }

    // =========================================================
    // TEST 11
    // =========================================================

    @Test
    void shouldReturnFalseWhenSchedulingConflictExistsForEligibility() {

        Long crewId = 1L;
        Long flightId = 1L;

        Flight flight = new Flight();

        flight.setId(flightId);

        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 10, 0));

        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 14, 0));

        when(flightRepository
                .findById(flightId))
                .thenReturn(Optional.of(flight));

        CrewAvailability availability =
                new CrewAvailability();

        availability.setCrewId(crewId);
        availability.setDate(
                LocalDate.of(2026, 9, 26));
        availability.setStatus("AVAILABLE");

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 26)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of());

        CrewAssignment assignment =
                new CrewAssignment();

        assignment.setCrewId(crewId);

        assignment.setAssignmentStartTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));

        assignment.setAssignmentEndTime(
                LocalDateTime.of(2026, 9, 26, 16, 0));

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of(assignment));

        boolean result =
                schedulingService.isCrewEligible(
                        crewId,
                        flightId);

        assertFalse(result);
    }

    // =========================================================
    // TEST 12
    // MINIMUM REST PERIOD
    // =========================================================

    @Test
    void shouldThrowExceptionWhenMinimumRestPeriodIsViolated() {

        Long crewId = 1L;
        Long flightId = 1L;

        Flight flight = new Flight();

        flight.setId(flightId);

        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 8, 0));

        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));

        when(flightRepository
                .findById(flightId))
                .thenReturn(Optional.of(flight));

        CrewAvailability availability =
                new CrewAvailability();

        availability.setCrewId(crewId);
        availability.setDate(
                LocalDate.of(2026, 9, 26));
        availability.setStatus("AVAILABLE");

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 26)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of());

        /*
         * Existing assignment:
         *
         * 25 Sep 18:00 -> 26 Sep 02:00
         *
         * New flight:
         *
         * 26 Sep 08:00 -> 12:00
         *
         * Rest = 6 hours
         *
         * Required = 10 hours
         */

        CrewAssignment assignment =
                new CrewAssignment();

        assignment.setId(1L);
        assignment.setCrewId(crewId);

        assignment.setAssignmentStartTime(
                LocalDateTime.of(2026, 9, 25, 18, 0));

        assignment.setAssignmentEndTime(
                LocalDateTime.of(2026, 9, 26, 2, 0));

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of(assignment));

        when(schedulingProperties
                .getMinimumRestHours())
                .thenReturn(10);

        when(schedulingProperties
                .getMaximumDutyHours())
                .thenReturn(12);

        when(schedulingProperties
                .getMaximumConsecutiveDutyDays())
                .thenReturn(6);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> schedulingService
                                .isCrewEligible(
                                        crewId,
                                        flightId));

        assertEquals(
                "Crew member does not have the required minimum rest period of 10 hours",
                exception.getMessage());
    }

    // =========================================================
    // TEST 13
    // MAXIMUM DUTY HOURS
    // =========================================================

    @Test
    void shouldThrowExceptionWhenMaximumDutyHoursAreExceeded() {

        Long crewId = 1L;
        Long flightId = 1L;

        /*
         * Flight:
         *
         * 08:00 -> 21:00
         *
         * Total = 13 hours
         *
         * Maximum allowed = 12 hours
         */

        Flight flight = new Flight();

        flight.setId(flightId);

        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 8, 0));

        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 21, 0));

        when(flightRepository
                .findById(flightId))
                .thenReturn(Optional.of(flight));

        CrewAvailability availability =
                new CrewAvailability();

        availability.setCrewId(crewId);
        availability.setDate(
                LocalDate.of(2026, 9, 26));
        availability.setStatus("AVAILABLE");

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 26)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of());

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of());

        when(schedulingProperties
                .getMinimumRestHours())
                .thenReturn(10);

        when(schedulingProperties
                .getMaximumDutyHours())
                .thenReturn(12);

        when(schedulingProperties
                .getMaximumConsecutiveDutyDays())
                .thenReturn(6);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> schedulingService
                                .isCrewEligible(
                                        crewId,
                                        flightId));

        assertEquals(
                "Crew assignment exceeds the maximum duty period of 12 hours",
                exception.getMessage());
    }

    // =========================================================
    // TEST 14
    // MAXIMUM CONSECUTIVE DUTY DAYS
    // =========================================================

    @Test
    void shouldThrowExceptionWhenMaximumConsecutiveDutyDaysAreExceeded() {

        Long crewId = 1L;
        Long flightId = 1L;

        /*
         * Existing assignments:
         *
         * 20 Sep
         * 21 Sep
         * 22 Sep
         * 23 Sep
         * 24 Sep
         * 25 Sep
         *
         * New flight:
         *
         * 26 Sep
         *
         * Total = 7 consecutive duty days
         *
         * Maximum allowed = 6 days
         */

        Flight flight = new Flight();

        flight.setId(flightId);

        flight.setDepartureTime(
                LocalDateTime.of(2026, 9, 26, 8, 0));

        flight.setArrivalTime(
                LocalDateTime.of(2026, 9, 26, 12, 0));

        when(flightRepository
                .findById(flightId))
                .thenReturn(Optional.of(flight));

        CrewAvailability availability =
                new CrewAvailability();

        availability.setCrewId(crewId);
        availability.setDate(
                LocalDate.of(2026, 9, 26));
        availability.setStatus("AVAILABLE");

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 26)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of());

        CrewAssignment assignment1 =
                createAssignment(
                        1L,
                        crewId,
                        LocalDateTime.of(
                                2026, 9, 20, 8, 0),
                        LocalDateTime.of(
                                2026, 9, 20, 16, 0));

        CrewAssignment assignment2 =
                createAssignment(
                        2L,
                        crewId,
                        LocalDateTime.of(
                                2026, 9, 21, 8, 0),
                        LocalDateTime.of(
                                2026, 9, 21, 16, 0));

        CrewAssignment assignment3 =
                createAssignment(
                        3L,
                        crewId,
                        LocalDateTime.of(
                                2026, 9, 22, 8, 0),
                        LocalDateTime.of(
                                2026, 9, 22, 16, 0));

        CrewAssignment assignment4 =
                createAssignment(
                        4L,
                        crewId,
                        LocalDateTime.of(
                                2026, 9, 23, 8, 0),
                        LocalDateTime.of(
                                2026, 9, 23, 16, 0));

        CrewAssignment assignment5 =
                createAssignment(
                        5L,
                        crewId,
                        LocalDateTime.of(
                                2026, 9, 24, 8, 0),
                        LocalDateTime.of(
                                2026, 9, 24, 16, 0));

        CrewAssignment assignment6 =
                createAssignment(
                        6L,
                        crewId,
                        LocalDateTime.of(
                                2026, 9, 25, 8, 0),
                        LocalDateTime.of(
                                2026, 9, 25, 16, 0));

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(
                        List.of(
                                assignment1,
                                assignment2,
                                assignment3,
                                assignment4,
                                assignment5,
                                assignment6));

        when(schedulingProperties
                .getMinimumRestHours())
                .thenReturn(10);

        when(schedulingProperties
                .getMaximumDutyHours())
                .thenReturn(12);

        when(schedulingProperties
                .getMaximumConsecutiveDutyDays())
                .thenReturn(6);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> schedulingService
                                .isCrewEligible(
                                        crewId,
                                        flightId));

        assertEquals(
                "Crew member cannot be assigned for more than 6 consecutive duty days",
                exception.getMessage());
    }

    // =========================================================
    // HELPER METHOD
    // =========================================================

    private CrewAssignment createAssignment(
            Long id,
            Long crewId,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        CrewAssignment assignment =
                new CrewAssignment();

        assignment.setId(id);
        assignment.setCrewId(crewId);
        assignment.setAssignmentStartTime(startTime);
        assignment.setAssignmentEndTime(endTime);

        return assignment;
    }
}