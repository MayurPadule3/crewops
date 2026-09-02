
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

import com.crewops.dto.FlightRequest;
import com.crewops.entity.Flight;
import com.crewops.service.FlightService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
	
	private final FlightService flightService;
	public FlightController(FlightService flightService) {
		this.flightService = flightService;
	}
	
	
	@PostMapping
	public Flight createFlight(@Valid @RequestBody FlightRequest flightRequest) {
		return flightService.createFlight(flightRequest);
	}
	
	@GetMapping
	public List<Flight> getAllFlights(){
		return flightService.getAllFlights();
	}
	
	@GetMapping("/{id}")
	public Flight getFlightById(@PathVariable Long id) {
		return flightService.getFlightById(id);
	}
	
	@PutMapping("/{id}")
	public Flight updateFlight(
	        @PathVariable Long id,
	        @Valid
	        @RequestBody FlightRequest flightRequest) {

	    return flightService.updateFlight(id, flightRequest);
	}
	
	@DeleteMapping("/{id}")
	public void deleteFlight(@PathVariable Long id) {
	    flightService.deleteFlight(id);
	}

}