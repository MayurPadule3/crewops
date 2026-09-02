package com.crewops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.dto.CrewRequest;
import com.crewops.entity.Crew;
import com.crewops.exception.CrewNotFoundException;
import com.crewops.repository.CrewRepository;

@Service
public class CrewService {
	
	private final CrewRepository crewRepository;
	
	public CrewService(CrewRepository crewRepository) {
		this.crewRepository = crewRepository;
	}
	
	public Crew createCrew(CrewRequest crewRequest) {
		
		Crew crew = new Crew();
		
		crew.setEmployeeCode(crewRequest.getEmployeeCode());
		crew.setName(crewRequest.getName());
		crew.setEmail(crewRequest.getEmail());
		crew.setRole(crewRequest.getRole());
		crew.setBaseAirport(crewRequest.getBaseAirport());
		crew.setStatus(crewRequest.getStatus());
		
		return crewRepository.save(crew);
	}
	
	public List<Crew> getAllCrew(){
		return crewRepository.findAll();
	}
	
	public Crew updateCrew(Long id, CrewRequest crewRequest) {

	    Crew crew = crewRepository.findById(id)
	            .orElseThrow(() -> new CrewNotFoundException("Crew Not Found With id: "+id));

	    crew.setEmployeeCode(crewRequest.getEmployeeCode());
	    crew.setName(crewRequest.getName());
	    crew.setEmail(crewRequest.getEmail());
	    crew.setRole(crewRequest.getRole());
	    crew.setBaseAirport(crewRequest.getBaseAirport());
	    crew.setStatus(crewRequest.getStatus());

	    return crewRepository.save(crew);
	}
	
	public void deleteCrew(Long id) {
	    Crew crew = crewRepository.findById(id)
	            .orElseThrow(() -> new CrewNotFoundException("Crew Not Found With id: " + id));

	    crewRepository.delete(crew);
	}

}
