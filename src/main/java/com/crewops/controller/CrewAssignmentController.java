package com.crewops.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crewops.dto.CrewAssignmentRequest;
import com.crewops.dto.CrewAssignmentResponse;
import com.crewops.service.CrewAssignmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/crew-assignments")
public class CrewAssignmentController {

    private final CrewAssignmentService crewAssignmentService;

    public CrewAssignmentController(
            CrewAssignmentService crewAssignmentService) {

        this.crewAssignmentService = crewAssignmentService;
    }

    @PostMapping
    public CrewAssignmentResponse createCrewAssignment(
            @Valid @RequestBody CrewAssignmentRequest crewAssignmentRequest) {

        return crewAssignmentService
                .createCrewAssignment(crewAssignmentRequest);
    }

    @GetMapping
    public List<CrewAssignmentResponse> getAllCrewAssignments() {

        return crewAssignmentService.getAllCrewAssignments();
    }

    @GetMapping("/{id}")
    public CrewAssignmentResponse getCrewAssignmentById(
            @PathVariable Long id) {

        return crewAssignmentService.getCrewAssignmentById(id);
    }

    @PutMapping("/{id}")
    public CrewAssignmentResponse updateCrewAssignment(
            @PathVariable Long id,
            @Valid @RequestBody CrewAssignmentRequest crewAssignmentRequest) {

        return crewAssignmentService
                .updateCrewAssignment(id, crewAssignmentRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteCrewAssignment(@PathVariable Long id) {

        crewAssignmentService.deleteCrewAssignment(id);
    }
}