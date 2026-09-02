package com.crewops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.dto.FlightRequest;
import com.crewops.entity.Flight;
import com.crewops.exception.FlightNotFoundException;
import com.crewops.repository.FlightRepository;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public Flight createFlight(FlightRequest flightRequest) {

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

        return flightRepository.save(flight);
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Flight getFlightById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException(
                        "Flight Not Found With id: " + id));
    }

    public Flight updateFlight(Long id, FlightRequest flightRequest) {

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

        return flightRepository.save(flight);
    }

    public void deleteFlight(Long id) {

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException(
                        "Flight Not Found With id: " + id));

        flightRepository.delete(flight);
    }
}