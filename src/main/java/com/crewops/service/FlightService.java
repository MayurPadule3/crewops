package com.crewops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.dto.FlightRequest;
import com.crewops.dto.FlightResponse;
import com.crewops.entity.Flight;
import com.crewops.exception.FlightNotFoundException;
import com.crewops.repository.FlightRepository;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public FlightResponse createFlight(FlightRequest flightRequest) {

        if (!flightRequest.getArrivalTime()
                .isAfter(flightRequest.getDepartureTime())) {

            throw new IllegalArgumentException(
                    "Arrival time must be after departure time");
        }

        Flight flight = new Flight();

        flight.setFlightNumber(flightRequest.getFlightNumber());
        flight.setDepartureAirport(flightRequest.getDepartureAirport());
        flight.setArrivalAirport(flightRequest.getArrivalAirport());
        flight.setDepartureTime(flightRequest.getDepartureTime());
        flight.setArrivalTime(flightRequest.getArrivalTime());
        flight.setAircraftCode(flightRequest.getAircraftCode());
        flight.setStatus(flightRequest.getStatus());

        Flight savedFlight = flightRepository.save(flight);
        
        return mapToResponse(savedFlight);
    }

    public List<FlightResponse> getAllFlights() {
        return flightRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public FlightResponse getFlightById(Long id) {
    	Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException(
                        "Flight Not Found With id: " + id));

    	return mapToResponse(flight);
    }

    public FlightResponse updateFlight(Long id, FlightRequest flightRequest) {

        if (!flightRequest.getArrivalTime()
                .isAfter(flightRequest.getDepartureTime())) {

            throw new IllegalArgumentException(
                    "Arrival time must be after departure time");
        }

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException(
                        "Flight Not Found With id: " + id));

        flight.setFlightNumber(flightRequest.getFlightNumber());
        flight.setDepartureAirport(flightRequest.getDepartureAirport());
        flight.setArrivalAirport(flightRequest.getArrivalAirport());
        flight.setDepartureTime(flightRequest.getDepartureTime());
        flight.setArrivalTime(flightRequest.getArrivalTime());
        flight.setAircraftCode(flightRequest.getAircraftCode());
        flight.setStatus(flightRequest.getStatus());

        Flight updatedFlight = flightRepository.save(flight);
        return mapToResponse(updatedFlight);
    }

    public void deleteFlight(Long id) {

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException(
                        "Flight Not Found With id: " + id));

        flightRepository.delete(flight);
    }
    
    private FlightResponse mapToResponse(Flight flight) {

        FlightResponse response = new FlightResponse();

        response.setId(flight.getId());
        response.setFlightNumber(flight.getFlightNumber());
        response.setDepartureAirport(flight.getDepartureAirport());
        response.setArrivalAirport(flight.getArrivalAirport());
        response.setDepartureTime(flight.getDepartureTime());
        response.setArrivalTime(flight.getArrivalTime());
        response.setAircraftCode(flight.getAircraftCode());
        response.setStatus(flight.getStatus());

        return response;
    }
}