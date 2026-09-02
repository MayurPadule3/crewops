package com.crewops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.dto.CrewRequest;
import com.crewops.dto.CrewResponse;
import com.crewops.entity.Crew;
import com.crewops.exception.CrewNotFoundException;
import com.crewops.repository.CrewRepository;

@Service
public class CrewService {

    private final CrewRepository crewRepository;

    public CrewService(CrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    public CrewResponse createCrew(CrewRequest crewRequest) {

        Crew crew = new Crew();

        crew.setEmployeeCode(crewRequest.getEmployeeCode());
        crew.setName(crewRequest.getName());
        crew.setEmail(crewRequest.getEmail());
        crew.setRole(crewRequest.getRole());
        crew.setBaseAirport(crewRequest.getBaseAirport());
        crew.setStatus(crewRequest.getStatus());

        Crew savedCrew = crewRepository.save(crew);
        return mapToResponse(savedCrew);
    }

    public List<CrewResponse> getAllCrew() {

        return crewRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CrewResponse getCrewById(Long id) {

        Crew crew = crewRepository.findById(id)
                .orElseThrow(() ->
                        new CrewNotFoundException("Crew Not Found With id: " + id));

        return mapToResponse(crew);
    }

    public CrewResponse updateCrew(Long id, CrewRequest crewRequest) {

        Crew crew = crewRepository.findById(id)
                .orElseThrow(() ->
                        new CrewNotFoundException("Crew Not Found With id: " + id));

        crew.setEmployeeCode(crewRequest.getEmployeeCode());
        crew.setName(crewRequest.getName());
        crew.setEmail(crewRequest.getEmail());
        crew.setRole(crewRequest.getRole());
        crew.setBaseAirport(crewRequest.getBaseAirport());
        crew.setStatus(crewRequest.getStatus());

       Crew updatedCrew = crewRepository.save(crew);
       return mapToResponse(updatedCrew);
    }

    public void deleteCrew(Long id) {

        Crew crew = crewRepository.findById(id)
                .orElseThrow(() ->
                        new CrewNotFoundException("Crew Not Found With id: " + id));

        crewRepository.delete(crew);
    }

    private CrewResponse mapToResponse(Crew crew) {

        CrewResponse response = new CrewResponse();

        response.setId(crew.getId());
        response.setEmployeeCode(crew.getEmployeeCode());
        response.setName(crew.getName());
        response.setEmail(crew.getEmail());
        response.setRole(crew.getRole());
        response.setBaseAirport(crew.getBaseAirport());
        response.setStatus(crew.getStatus());

        return response;
    }
}