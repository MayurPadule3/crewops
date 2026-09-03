package com.crewops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.dto.AircraftRequest;
import com.crewops.dto.AircraftResponse;
import com.crewops.entity.Aircraft;
import com.crewops.exception.AircraftNotFoundException;
import com.crewops.repository.AircraftRepository;

@Service
public class AircraftService {

    private final AircraftRepository aircraftRepository;

    public AircraftService(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    public AircraftResponse createAircraft(AircraftRequest aircraftRequest) {

        Aircraft aircraft = new Aircraft();

        aircraft.setAircraftCode(aircraftRequest.getAircraftCode());
        aircraft.setAircraftType(aircraftRequest.getAircraftType());
        aircraft.setCapacity(aircraftRequest.getCapacity());
        aircraft.setStatus(aircraftRequest.getStatus());

        Aircraft savedAircraft = aircraftRepository.save(aircraft);

        return mapToResponse(savedAircraft);
    }

    public List<AircraftResponse> getAllAircraft() {

        return aircraftRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AircraftResponse getAircraftById(Long id) {

        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new AircraftNotFoundException(
                        "Aircraft Not Found With id: " + id));

        return mapToResponse(aircraft);
    }

    public AircraftResponse updateAircraft(
            Long id,
            AircraftRequest aircraftRequest) {

        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new AircraftNotFoundException(
                        "Aircraft Not Found With id: " + id));

        aircraft.setAircraftCode(aircraftRequest.getAircraftCode());
        aircraft.setAircraftType(aircraftRequest.getAircraftType());
        aircraft.setCapacity(aircraftRequest.getCapacity());
        aircraft.setStatus(aircraftRequest.getStatus());

        Aircraft updatedAircraft = aircraftRepository.save(aircraft);

        return mapToResponse(updatedAircraft);
    }

    public void deleteAircraft(Long id) {

        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new AircraftNotFoundException(
                        "Aircraft Not Found With id: " + id));

        aircraftRepository.delete(aircraft);
    }

    private AircraftResponse mapToResponse(Aircraft aircraft) {

        AircraftResponse response = new AircraftResponse();

        response.setId(aircraft.getId());
        response.setAircraftCode(aircraft.getAircraftCode());
        response.setAircraftType(aircraft.getAircraftType());
        response.setCapacity(aircraft.getCapacity());
        response.setStatus(aircraft.getStatus());

        return response;
    }
}