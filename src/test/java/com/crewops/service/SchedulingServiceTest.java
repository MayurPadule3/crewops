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
import com.crewops.dto.AutoSchedulingRequest;
import com.crewops.dto.EligibleCrewResponse;
import com.crewops.dto.SchedulingRequest;
import com.crewops.dto.SchedulingResponse;
import com.crewops.entity.Crew;
import com.crewops.entity.CrewAssignment;
import com.crewops.entity.CrewAvailability;
import com.crewops.entity.Flight;
import com.crewops.entity.LeaveRequest;
import com.crewops.repository.CrewAssignmentRepository;
import com.crewops.repository.CrewAvailabilityRepository;
import com.crewops.repository.CrewRepository;
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
    private CrewRepository crewRepository;

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
                crewRepository,
                schedulingProperties,
                schedulingValidator);

        when(schedulingProperties.getMinimumRestHours())
                .thenReturn(10);

        when(schedulingProperties.getMaximumDutyHours())
                .thenReturn(12);

        when(schedulingProperties.getMaximumConsecutiveDutyDays())
                .thenReturn(6);
    }

    // ---------------------------------------------------------
    // 1. Scheduling conflict
    // ---------------------------------------------------------

    @Test
    void shouldDetectSchedulingConflict() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setDepartureTime(
                LocalDateTime.of(
                        2026, 9, 5, 11, 0));

        flight.setArrivalTime(
                LocalDateTime.of(
                        2026, 9, 5, 13, 30));

        CrewAssignment assignment = new CrewAssignment();

        assignment.setCrewId(crewId);

        assignment.setAssignmentStartTime(
                LocalDateTime.of(
                        2026, 9, 5, 10, 0));

        assignment.setAssignmentEndTime(
                LocalDateTime.of(
                        2026, 9, 5, 12, 0));

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of(assignment));

        boolean result =
                schedulingService.hasSchedulingConflict(
                        crewId,
                        flight);

        assertTrue(result);
    }

    // ---------------------------------------------------------
    // 2. No scheduling conflict
    // ---------------------------------------------------------

    @Test
    void shouldReturnFalseWhenNoSchedulingConflict() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setDepartureTime(
                LocalDateTime.of(
                        2026, 9, 5, 14, 0));

        flight.setArrivalTime(
                LocalDateTime.of(
                        2026, 9, 5, 16, 0));

        CrewAssignment assignment = new CrewAssignment();

        assignment.setCrewId(crewId);

        assignment.setAssignmentStartTime(
                LocalDateTime.of(
                        2026, 9, 5, 8, 0));

        assignment.setAssignmentEndTime(
                LocalDateTime.of(
                        2026, 9, 5, 10, 0));

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of(assignment));

        boolean result =
                schedulingService.hasSchedulingConflict(
                        crewId,
                        flight);

        assertFalse(result);
    }

    // ---------------------------------------------------------
    // 3. Crew available
    // ---------------------------------------------------------

    @Test
    void shouldReturnTrueWhenCrewIsAvailable() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setDepartureTime(
                LocalDateTime.of(
                        2026, 9, 5, 11, 0));

        CrewAvailability availability =
                new CrewAvailability();

        availability.setCrewId(crewId);

        availability.setDate(
                LocalDate.of(
                        2026, 9, 5));

        availability.setStatus("AVAILABLE");

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of(availability));

        boolean result =
                schedulingService.isCrewAvailable(
                        crewId,
                        flight);

        assertTrue(result);
    }

    // ---------------------------------------------------------
    // 4. Crew unavailable
    // ---------------------------------------------------------

    @Test
    void shouldReturnFalseWhenCrewIsUnavailable() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setDepartureTime(
                LocalDateTime.of(
                        2026, 9, 5, 11, 0));

        CrewAvailability availability =
                new CrewAvailability();

        availability.setCrewId(crewId);

        availability.setDate(
                LocalDate.of(
                        2026, 9, 5));

        availability.setStatus("UNAVAILABLE");

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of(availability));

        boolean result =
                schedulingService.isCrewAvailable(
                        crewId,
                        flight);

        assertFalse(result);
    }

    // ---------------------------------------------------------
    // 5. No availability record
    // ---------------------------------------------------------

    @Test
    void shouldReturnFalseWhenNoAvailabilityRecordExists() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setDepartureTime(
                LocalDateTime.of(
                        2026, 9, 5, 11, 0));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of());

        boolean result =
                schedulingService.isCrewAvailable(
                        crewId,
                        flight);

        assertFalse(result);
    }

    // ---------------------------------------------------------
    // 6. Crew on approved leave
    // ---------------------------------------------------------

    @Test
    void shouldReturnTrueWhenCrewIsOnApprovedLeave() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setDepartureTime(
                LocalDateTime.of(
                        2026, 9, 5, 11, 0));

        LeaveRequest leaveRequest =
                new LeaveRequest();

        leaveRequest.setCrewId(crewId);

        leaveRequest.setStartDate(
                LocalDate.of(
                        2026, 9, 4));

        leaveRequest.setEndDate(
                LocalDate.of(
                        2026, 9, 6));

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

    // ---------------------------------------------------------
    // 7. Crew not on approved leave
    // ---------------------------------------------------------

    @Test
    void shouldReturnFalseWhenCrewIsNotOnApprovedLeave() {

        Long crewId = 1L;

        Flight flight = new Flight();

        flight.setDepartureTime(
                LocalDateTime.of(
                        2026, 9, 5, 11, 0));

        LeaveRequest leaveRequest =
                new LeaveRequest();

        leaveRequest.setCrewId(crewId);

        leaveRequest.setStartDate(
                LocalDate.of(
                        2026, 9, 7));

        leaveRequest.setEndDate(
                LocalDate.of(
                        2026, 9, 8));

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

    // ---------------------------------------------------------
    // 8. Crew eligible
    // ---------------------------------------------------------

    @Test
    void shouldReturnTrueWhenCrewIsEligible() {

        Long crewId = 1L;
        Long flightId = 2L;

        Flight flight = createFlight(flightId);

        CrewAvailability availability =
                createAvailableCrewAvailability(crewId);

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of());

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of());

        boolean result =
                schedulingService.isCrewEligible(
                        crewId,
                        flightId);

        assertTrue(result);
    }

    // ---------------------------------------------------------
    // 9. Crew unavailable for eligibility
    // ---------------------------------------------------------

    @Test
    void shouldReturnFalseWhenCrewIsUnavailableForEligibility() {

        Long crewId = 1L;
        Long flightId = 2L;

        Flight flight = createFlight(flightId);

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of());

        boolean result =
                schedulingService.isCrewEligible(
                        crewId,
                        flightId);

        assertFalse(result);
    }

    // ---------------------------------------------------------
    // 10. Crew on leave for eligibility
    // ---------------------------------------------------------

    @Test
    void shouldReturnFalseWhenCrewIsOnLeaveForEligibility() {

        Long crewId = 1L;
        Long flightId = 2L;

        Flight flight = createFlight(flightId);

        CrewAvailability availability =
                createAvailableCrewAvailability(crewId);

        LeaveRequest leaveRequest =
                new LeaveRequest();

        leaveRequest.setCrewId(crewId);

        leaveRequest.setStartDate(
                LocalDate.of(
                        2026, 9, 5));

        leaveRequest.setEndDate(
                LocalDate.of(
                        2026, 9, 6));

        leaveRequest.setStatus("APPROVED");

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of(leaveRequest));

        boolean result =
                schedulingService.isCrewEligible(
                        crewId,
                        flightId);

        assertFalse(result);
    }

    // ---------------------------------------------------------
    // 11. Crew has scheduling conflict
    // ---------------------------------------------------------

    @Test
    void shouldReturnFalseWhenCrewHasSchedulingConflict() {

        Long crewId = 1L;
        Long flightId = 2L;

        Flight flight = createFlight(flightId);

        CrewAvailability availability =
                createAvailableCrewAvailability(crewId);

        CrewAssignment assignment =
                new CrewAssignment();

        assignment.setCrewId(crewId);

        assignment.setAssignmentStartTime(
                LocalDateTime.of(
                        2026, 9, 5, 10, 0));

        assignment.setAssignmentEndTime(
                LocalDateTime.of(
                        2026, 9, 5, 12, 0));

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of());

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of(assignment));

        boolean result =
                schedulingService.isCrewEligible(
                        crewId,
                        flightId);

        assertFalse(result);
    }

    // ---------------------------------------------------------
    // 12. Minimum rest violation
    // ---------------------------------------------------------

    @Test
    void shouldThrowExceptionWhenMinimumRestIsViolated() {

        Long crewId = 1L;
        Long flightId = 2L;

        Flight flight = createFlight(flightId);

        CrewAvailability availability =
                createAvailableCrewAvailability(crewId);

        CrewAssignment assignment =
                new CrewAssignment();

        assignment.setCrewId(crewId);

        assignment.setAssignmentStartTime(
                LocalDateTime.of(
                        2026, 9, 4, 20, 0));

        assignment.setAssignmentEndTime(
                LocalDateTime.of(
                        2026, 9, 5, 5, 0));

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of());

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of(assignment));

        assertThrows(
                IllegalArgumentException.class,
                () -> schedulingService.isCrewEligible(
                        crewId,
                        flightId));
    }

    // ---------------------------------------------------------
    // 13. Maximum duty hours violation
    // ---------------------------------------------------------

    @Test
    void shouldThrowExceptionWhenMaximumDutyHoursExceeded() {

        Long crewId = 1L;
        Long flightId = 2L;

        Flight flight = new Flight();

        flight.setId(flightId);

        flight.setDepartureTime(
                LocalDateTime.of(
                        2026, 9, 5, 0, 0));

        flight.setArrivalTime(
                LocalDateTime.of(
                        2026, 9, 5, 13, 0));

        CrewAvailability availability =
                createAvailableCrewAvailability(crewId);

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of());

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> schedulingService.isCrewEligible(
                        crewId,
                        flightId));
    }

    // ---------------------------------------------------------
    // 14. Maximum consecutive duty days violation
    // ---------------------------------------------------------

    @Test
    void shouldThrowExceptionWhenMaximumConsecutiveDutyDaysExceeded() {

        Long crewId = 1L;
        Long flightId = 2L;

        Flight flight = createFlight(flightId);

        CrewAvailability availability =
                createAvailableCrewAvailability(crewId);

        CrewAssignment assignment1 =
                createAssignment(
                        crewId,
                        LocalDateTime.of(
                                2026, 8, 31, 8, 0),
                        LocalDateTime.of(
                                2026, 8, 31, 10, 0));

        CrewAssignment assignment2 =
                createAssignment(
                        crewId,
                        LocalDateTime.of(
                                2026, 9, 1, 8, 0),
                        LocalDateTime.of(
                                2026, 9, 1, 10, 0));

        CrewAssignment assignment3 =
                createAssignment(
                        crewId,
                        LocalDateTime.of(
                                2026, 9, 2, 8, 0),
                        LocalDateTime.of(
                                2026, 9, 2, 10, 0));

        CrewAssignment assignment4 =
                createAssignment(
                        crewId,
                        LocalDateTime.of(
                                2026, 9, 3, 8, 0),
                        LocalDateTime.of(
                                2026, 9, 3, 10, 0));

        CrewAssignment assignment5 =
                createAssignment(
                        crewId,
                        LocalDateTime.of(
                                2026, 9, 4, 8, 0),
                        LocalDateTime.of(
                                2026, 9, 4, 10, 0));

        CrewAssignment assignment6 =
                createAssignment(
                        crewId,
                        LocalDateTime.of(
                                2026, 9, 5, 6, 0),
                        LocalDateTime.of(
                                2026, 9, 5, 8, 0));

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of());

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of(
                        assignment1,
                        assignment2,
                        assignment3,
                        assignment4,
                        assignment5,
                        assignment6));

        assertThrows(
                IllegalArgumentException.class,
                () -> schedulingService.isCrewEligible(
                        crewId,
                        flightId));
    }

    // ---------------------------------------------------------
    // 15. Create assignment successfully
    // ---------------------------------------------------------

    @Test
    void shouldCreateAssignmentSuccessfully() {

        Long crewId = 8L;
        Long flightId = 2L;

        Flight flight = createFlight(flightId);

        CrewAvailability availability =
                createAvailableCrewAvailability(crewId);

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of());

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of());

        CrewAssignment savedAssignment =
                new CrewAssignment();

        savedAssignment.setId(20L);
        savedAssignment.setCrewId(crewId);
        savedAssignment.setFlightId(flightId);
        savedAssignment.setAssignmentRole("PILOT");
        savedAssignment.setAssignmentStartTime(
                flight.getDepartureTime());
        savedAssignment.setAssignmentEndTime(
                flight.getArrivalTime());
        savedAssignment.setStatus("ASSIGNED");

        when(crewAssignmentRepository.save(
                org.mockito.ArgumentMatchers.any(CrewAssignment.class)))
                .thenReturn(savedAssignment);

        SchedulingRequest request =
                createSchedulingRequest(
                        crewId,
                        flightId,
                        "PILOT");

        SchedulingResponse response =
                schedulingService.createAssignment(request);

        assertEquals(20L, response.getAssignmentId());
        assertEquals(crewId, response.getCrewId());
        assertEquals(flightId, response.getFlightId());
        assertEquals("PILOT", response.getAssignmentRole());
        assertEquals("ASSIGNED", response.getStatus());
        assertEquals(
                "Crew member successfully assigned to flight",
                response.getMessage());
    }

    // ---------------------------------------------------------
    // 16. Assignment rejected when crew is not eligible
    // ---------------------------------------------------------

    @Test
    void shouldRejectAssignmentWhenCrewIsNotEligible() {

        Long crewId = 8L;
        Long flightId = 2L;

        Flight flight = createFlight(flightId);

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of());

        SchedulingRequest request =
                createSchedulingRequest(
                        crewId,
                        flightId,
                        "PILOT");

        assertThrows(
                IllegalArgumentException.class,
                () -> schedulingService.createAssignment(request));
    }

    // ---------------------------------------------------------
    // 17. Find eligible crew for requested role
    // ---------------------------------------------------------

    @Test
    void shouldFindEligibleCrewForRequestedRole() {

        Long flightId = 2L;

        Flight flight = createFlight(flightId);

        Crew crew = createCrew(
                8L,
                "EMP003",
                "Amit Patil",
                "PILOT",
                "DEL",
                "ACTIVE");

        CrewAvailability availability =
                createAvailableCrewAvailability(8L);

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewRepository.findAll())
                .thenReturn(List.of(crew));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        8L,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(8L))
                .thenReturn(List.of());

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(8L))
                .thenReturn(List.of());

        List<EligibleCrewResponse> result =
                schedulingService.findEligibleCrew(
                        flightId,
                        "PILOT");

        assertEquals(1, result.size());

        assertEquals(
                8L,
                result.get(0).getCrewId());

        assertEquals(
                "EMP003",
                result.get(0).getEmployeeCode());

        assertEquals(
                "Amit Patil",
                result.get(0).getName());

        assertEquals(
                "PILOT",
                result.get(0).getRole());
    }

    // ---------------------------------------------------------
    // 18. No crew matches requested role
    // ---------------------------------------------------------

    @Test
    void shouldReturnEmptyListWhenNoCrewMatchesRequestedRole() {

        Long flightId = 2L;

        Flight flight = createFlight(flightId);

        Crew crew = createCrew(
                8L,
                "EMP003",
                "Amit Patil",
                "PILOT",
                "DEL",
                "ACTIVE");

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewRepository.findAll())
                .thenReturn(List.of(crew));

        List<EligibleCrewResponse> result =
                schedulingService.findEligibleCrew(
                        flightId,
                        "CABIN_CREW");

        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------------------
    // 19. Auto assign crew successfully
    // ---------------------------------------------------------

    @Test
    void shouldAutoAssignEligibleCrewSuccessfully() {

        Long flightId = 2L;
        Long crewId = 8L;

        Flight flight = createFlight(flightId);

        Crew crew = createCrew(
                crewId,
                "EMP003",
                "Amit Patil",
                "PILOT",
                "DEL",
                "ACTIVE");

        CrewAvailability availability =
                createAvailableCrewAvailability(crewId);

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewRepository.findAll())
                .thenReturn(List.of(crew));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        crewId,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of(availability));

        when(leaveRequestRepository
                .findByCrewId(crewId))
                .thenReturn(List.of());

        when(crewAssignmentRepository
                .findByCrewIdOrderByAssignmentStartTimeAsc(crewId))
                .thenReturn(List.of());

        CrewAssignment savedAssignment =
                new CrewAssignment();

        savedAssignment.setId(21L);
        savedAssignment.setCrewId(crewId);
        savedAssignment.setFlightId(flightId);
        savedAssignment.setAssignmentRole("PILOT");
        savedAssignment.setAssignmentStartTime(
                flight.getDepartureTime());
        savedAssignment.setAssignmentEndTime(
                flight.getArrivalTime());
        savedAssignment.setStatus("ASSIGNED");

        when(crewAssignmentRepository.save(
                org.mockito.ArgumentMatchers.any(CrewAssignment.class)))
                .thenReturn(savedAssignment);

        AutoSchedulingRequest request =
                new AutoSchedulingRequest();

        request.setFlightId(flightId);
        request.setAssignmentRole("PILOT");

        SchedulingResponse response =
                schedulingService.autoAssignCrew(request);

        assertEquals(
                21L,
                response.getAssignmentId());

        assertEquals(
                crewId,
                response.getCrewId());

        assertEquals(
                flightId,
                response.getFlightId());

        assertEquals(
                "PILOT",
                response.getAssignmentRole());

        assertEquals(
                "ASSIGNED",
                response.getStatus());
    }

    // ---------------------------------------------------------
    // 20. Auto assignment fails when no eligible crew exists
    // ---------------------------------------------------------

    @Test
    void shouldThrowExceptionWhenAutoAssignmentHasNoEligibleCrew() {

        Long flightId = 2L;

        Flight flight = createFlight(flightId);

        Crew crew = createCrew(
                8L,
                "EMP003",
                "Amit Patil",
                "PILOT",
                "DEL",
                "ACTIVE");

        when(flightRepository.findById(flightId))
                .thenReturn(Optional.of(flight));

        when(crewRepository.findAll())
                .thenReturn(List.of(crew));

        when(crewAvailabilityRepository
                .findByCrewIdAndDate(
                        8L,
                        LocalDate.of(2026, 9, 5)))
                .thenReturn(List.of());

        AutoSchedulingRequest request =
                new AutoSchedulingRequest();

        request.setFlightId(flightId);
        request.setAssignmentRole("PILOT");

        assertThrows(
                IllegalArgumentException.class,
                () -> schedulingService.autoAssignCrew(request));
    }

    // ---------------------------------------------------------
    // Helper methods
    // ---------------------------------------------------------

    private Flight createFlight(Long flightId) {

        Flight flight = new Flight();

        flight.setId(flightId);

        flight.setFlightNumber("AI203");

        flight.setDepartureAirport("BOM");

        flight.setArrivalAirport("DEL");

        flight.setDepartureTime(
                LocalDateTime.of(
                        2026, 9, 5, 11, 0));

        flight.setArrivalTime(
                LocalDateTime.of(
                        2026, 9, 5, 13, 30));

        flight.setAircraftCode("AI-01");

        flight.setStatus("DELAYED");

        return flight;
    }

    private CrewAvailability createAvailableCrewAvailability(
            Long crewId) {

        CrewAvailability availability =
                new CrewAvailability();

        availability.setCrewId(crewId);

        availability.setDate(
                LocalDate.of(
                        2026, 9, 5));

        availability.setStatus("AVAILABLE");

        return availability;
    }

    private CrewAssignment createAssignment(
            Long crewId,
            LocalDateTime start,
            LocalDateTime end) {

        CrewAssignment assignment =
                new CrewAssignment();

        assignment.setCrewId(crewId);

        assignment.setAssignmentStartTime(start);

        assignment.setAssignmentEndTime(end);

        assignment.setStatus("ASSIGNED");

        return assignment;
    }

    private Crew createCrew(
            Long id,
            String employeeCode,
            String name,
            String role,
            String baseAirport,
            String status) {

        Crew crew = new Crew();

        crew.setId(id);

        crew.setEmployeeCode(employeeCode);

        crew.setName(name);

        crew.setEmail(
                name.toLowerCase()
                        .replace(" ", ".")
                        + "@example.com");

        crew.setRole(role);

        crew.setBaseAirport(baseAirport);

        crew.setStatus(status);

        return crew;
    }

    private SchedulingRequest createSchedulingRequest(
            Long crewId,
            Long flightId,
            String role) {

        SchedulingRequest request =
                new SchedulingRequest();

        request.setCrewId(crewId);

        request.setFlightId(flightId);

        request.setAssignmentRole(role);

        return request;
    }
}