package com.crewops.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}