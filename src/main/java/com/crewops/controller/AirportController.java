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

import com.crewops.dto.AirportRequest;
import com.crewops.dto.AirportResponse;
import com.crewops.service.AirportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @PostMapping
    public AirportResponse createAirport(
           @Valid @RequestBody AirportRequest airportRequest) {

        return airportService.createAirport(airportRequest);
    }

    @GetMapping
    public List<AirportResponse> getAllAirports() {

        return airportService.getAllAirports();
    }

    @GetMapping("/{id}")
    public AirportResponse getAirportById(@PathVariable Long id) {

        return airportService.getAirportById(id);
    }

    @PutMapping("/{id}")
    public AirportResponse updateAirport(
            @PathVariable Long id,
          @Valid  @RequestBody AirportRequest airportRequest) {

        return airportService.updateAirport(id, airportRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteAirport(@PathVariable Long id) {

        airportService.deleteAirport(id);
    }
}