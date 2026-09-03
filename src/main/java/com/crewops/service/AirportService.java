package com.crewops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.dto.AirportRequest;
import com.crewops.dto.AirportResponse;
import com.crewops.entity.Airport;
import com.crewops.exception.AirportNotFoundException;
import com.crewops.repository.AirportRepository;

@Service
public class AirportService {

    private final AirportRepository airportRepository;

    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public AirportResponse createAirport(AirportRequest airportRequest) {

        Airport airport = new Airport();

        airport.setAirportCode(airportRequest.getAirportCode());
        airport.setAirportName(airportRequest.getAirportName());
        airport.setCity(airportRequest.getCity());
        airport.setCountry(airportRequest.getCountry());

        Airport savedAirport = airportRepository.save(airport);

        return mapToResponse(savedAirport);
    }

    public List<AirportResponse> getAllAirports() {

        return airportRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AirportResponse getAirportById(Long id) {

        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new AirportNotFoundException(
                        "Airport Not Found With id: " + id));

        return mapToResponse(airport);
    }

    public AirportResponse updateAirport(
            Long id,
            AirportRequest airportRequest) {

        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new AirportNotFoundException(
                        "Airport Not Found With id: " + id));

        airport.setAirportCode(airportRequest.getAirportCode());
        airport.setAirportName(airportRequest.getAirportName());
        airport.setCity(airportRequest.getCity());
        airport.setCountry(airportRequest.getCountry());

        Airport updatedAirport = airportRepository.save(airport);

        return mapToResponse(updatedAirport);
    }

    public void deleteAirport(Long id) {

        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new AirportNotFoundException(
                        "Airport Not Found With id: " + id));

        airportRepository.delete(airport);
    }

    private AirportResponse mapToResponse(Airport airport) {

        AirportResponse response = new AirportResponse();

        response.setId(airport.getId());
        response.setAirportCode(airport.getAirportCode());
        response.setAirportName(airport.getAirportName());
        response.setCity(airport.getCity());
        response.setCountry(airport.getCountry());

        return response;
    }
}