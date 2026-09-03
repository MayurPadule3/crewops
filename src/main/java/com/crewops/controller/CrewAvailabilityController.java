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

import com.crewops.dto.CrewAvailabilityRequest;
import com.crewops.dto.CrewAvailabilityResponse;
import com.crewops.service.CrewAvailabilityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/crew-availability")
public class CrewAvailabilityController {

    private final CrewAvailabilityService crewAvailabilityService;

    public CrewAvailabilityController(
            CrewAvailabilityService crewAvailabilityService) {

        this.crewAvailabilityService = crewAvailabilityService;
    }

    @PostMapping
    public CrewAvailabilityResponse createCrewAvailability(
            @Valid @RequestBody CrewAvailabilityRequest crewAvailabilityRequest) {

        return crewAvailabilityService
                .createCrewAvailability(crewAvailabilityRequest);
    }

    @GetMapping
    public List<CrewAvailabilityResponse> getAllCrewAvailability() {

        return crewAvailabilityService.getAllCrewAvailability();
    }

    @GetMapping("/{id}")
    public CrewAvailabilityResponse getCrewAvailabilityById(
            @PathVariable Long id) {

        return crewAvailabilityService.getCrewAvailabilityById(id);
    }

    @PutMapping("/{id}")
    public CrewAvailabilityResponse updateCrewAvailability(
            @PathVariable Long id,
            @Valid @RequestBody CrewAvailabilityRequest crewAvailabilityRequest) {

        return crewAvailabilityService
                .updateCrewAvailability(id, crewAvailabilityRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteCrewAvailability(@PathVariable Long id) {

        crewAvailabilityService.deleteCrewAvailability(id);
    }
}