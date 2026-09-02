package com.crewops.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crewops.dto.CrewRequest;
import com.crewops.entity.Crew;
import com.crewops.service.CrewService;

import jakarta.validation.Valid;


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
	public Crew createCrew(@Valid @RequestBody CrewRequest crewRequest) {
		return crewService.createCrew(crewRequest);
	}
	
	
	@PutMapping("/{id}")
	public Crew updateCrew(@PathVariable Long id, @Valid @RequestBody CrewRequest crewRequest) {
		return crewService.updateCrew(id, crewRequest);
	}
	
	
}
