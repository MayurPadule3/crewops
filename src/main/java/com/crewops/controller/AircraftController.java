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

import com.crewops.dto.AircraftRequest;
import com.crewops.dto.AircraftResponse;
import com.crewops.service.AircraftService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/aircraft")
public class AircraftController {

    private final AircraftService aircraftService;

    public AircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @PostMapping
    public AircraftResponse createAircraft(
           @Valid @RequestBody AircraftRequest aircraftRequest) {

        return aircraftService.createAircraft(aircraftRequest);
    }

    @GetMapping
    public List<AircraftResponse> getAllAircraft() {

        return aircraftService.getAllAircraft();
    }

    @GetMapping("/{id}")
    public AircraftResponse getAircraftById(
            @PathVariable Long id) {

        return aircraftService.getAircraftById(id);
    }

    @PutMapping("/{id}")
    public AircraftResponse updateAircraft(
            @PathVariable Long id,
            @Valid @RequestBody AircraftRequest aircraftRequest) {

        return aircraftService.updateAircraft(id, aircraftRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteAircraft(@PathVariable Long id) {

        aircraftService.deleteAircraft(id);
    }
}