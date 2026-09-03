package com.crewops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.dto.CrewAvailabilityRequest;
import com.crewops.dto.CrewAvailabilityResponse;
import com.crewops.entity.CrewAvailability;
import com.crewops.exception.CrewAvailabilityNotFoundException;
import com.crewops.repository.CrewAvailabilityRepository;

@Service
public class CrewAvailabilityService {

    private final CrewAvailabilityRepository crewAvailabilityRepository;

    public CrewAvailabilityService(
            CrewAvailabilityRepository crewAvailabilityRepository) {

        this.crewAvailabilityRepository = crewAvailabilityRepository;
    }

    public CrewAvailabilityResponse createCrewAvailability(
            CrewAvailabilityRequest crewAvailabilityRequest) {

        CrewAvailability crewAvailability = new CrewAvailability();

        crewAvailability.setCrewId(crewAvailabilityRequest.getCrewId());
        crewAvailability.setDate(crewAvailabilityRequest.getDate());
        crewAvailability.setStatus(crewAvailabilityRequest.getStatus());

        CrewAvailability savedCrewAvailability =
                crewAvailabilityRepository.save(crewAvailability);

        return mapToResponse(savedCrewAvailability);
    }

    public List<CrewAvailabilityResponse> getAllCrewAvailability() {

        return crewAvailabilityRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CrewAvailabilityResponse getCrewAvailabilityById(Long id) {

        CrewAvailability crewAvailability =
                crewAvailabilityRepository.findById(id)
                .orElseThrow(() -> new CrewAvailabilityNotFoundException(
                        "Crew Availability Not Found With id: " + id));

        return mapToResponse(crewAvailability);
    }

    public CrewAvailabilityResponse updateCrewAvailability(
            Long id,
            CrewAvailabilityRequest crewAvailabilityRequest) {

        CrewAvailability crewAvailability =
                crewAvailabilityRepository.findById(id)
                .orElseThrow(() -> new CrewAvailabilityNotFoundException(
                        "Crew Availability Not Found With id: " + id));

        crewAvailability.setCrewId(crewAvailabilityRequest.getCrewId());
        crewAvailability.setDate(crewAvailabilityRequest.getDate());
        crewAvailability.setStatus(crewAvailabilityRequest.getStatus());

        CrewAvailability updatedCrewAvailability =
                crewAvailabilityRepository.save(crewAvailability);

        return mapToResponse(updatedCrewAvailability);
    }

    public void deleteCrewAvailability(Long id) {

        CrewAvailability crewAvailability =
                crewAvailabilityRepository.findById(id)
                .orElseThrow(() -> new CrewAvailabilityNotFoundException(
                        "Crew Availability Not Found With id: " + id));

        crewAvailabilityRepository.delete(crewAvailability);
    }

    private CrewAvailabilityResponse mapToResponse(
            CrewAvailability crewAvailability) {

        CrewAvailabilityResponse response =
                new CrewAvailabilityResponse();

        response.setId(crewAvailability.getId());
        response.setCrewId(crewAvailability.getCrewId());
        response.setDate(crewAvailability.getDate());
        response.setStatus(crewAvailability.getStatus());

        return response;
    }
}