package com.crewops.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crewops.dto.AutoSchedulingRequest;
import com.crewops.dto.EligibleCrewResponse;
import com.crewops.dto.SchedulingRequest;
import com.crewops.dto.SchedulingResponse;
import com.crewops.service.SchedulingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/scheduling")
public class SchedulingController {

    private final SchedulingService schedulingService;

    public SchedulingController(
            SchedulingService schedulingService) {

        this.schedulingService = schedulingService;
    }

    @PostMapping("/assign")
    public ResponseEntity<SchedulingResponse> createAssignment(
            @Valid @RequestBody SchedulingRequest schedulingRequest) {

        SchedulingResponse response =
                schedulingService.createAssignment(
                        schedulingRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/eligible-crew/{flightId}")
    public ResponseEntity<List<EligibleCrewResponse>> findEligibleCrew(
            @PathVariable Long flightId,
            @RequestParam String role) {

        List<EligibleCrewResponse> eligibleCrew =
                schedulingService.findEligibleCrew(
                        flightId,
                        role);

        return ResponseEntity.ok(eligibleCrew);
    }

    @PostMapping("/auto-assign")
    public ResponseEntity<SchedulingResponse> autoAssignCrew(
            @Valid @RequestBody AutoSchedulingRequest request) {

        SchedulingResponse response =
                schedulingService.autoAssignCrew(request);

        return ResponseEntity.ok(response);
    }
}