package com.crewops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crewops.entity.Crew;
import com.crewops.repository.CrewRepository;

@Service
public class CrewService {
	
	private final CrewRepository crewRepository;
	
	public CrewService(CrewRepository crewRepository) {
		this.crewRepository = crewRepository;
	}
	
	public Crew createCrew(Crew crew) {
		
		return crewRepository.save(crew);
	}
	
	public List<Crew> getAllCrew(){
		return crewRepository.findAll();
	}

}
