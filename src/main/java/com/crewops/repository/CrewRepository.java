package com.crewops.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.crewops.entity.Crew;

public interface CrewRepository extends JpaRepository<Crew, Long> {

}
