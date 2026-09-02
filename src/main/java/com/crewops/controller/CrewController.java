package com.crewops.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crewops.entity.Crew;
import com.crewops.service.CrewService;


@RestController
@RequestMapping("/api/crew")

public class CrewController {

	private final CrewService crewService;
	
	public CrewController(CrewService crewService) { 
		this.crewService = crewService;
	}
	
	@GetMapping
	public List<Crew> getAllCrew(){
		return crewService.getAllCrew();
	}
	
	@PostMapping
	public Crew createCrew(@RequestBody Crew crew) {
		return crewService.createCrew(crew);
	}
	
	
}
