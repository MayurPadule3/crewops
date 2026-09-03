package com.crewops.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrewAvailabilityRequest {
	
	@NotNull(message = "Crew ID is required")
    private Long crewId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotBlank(message = "Status is required")
    private String status;

	public Long getCrewId() {
		return crewId;
	}

	public void setCrewId(Long crewId) {
		this.crewId = crewId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
    

}
