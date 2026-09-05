package com.crewops.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrewAssignmentRequest {

    @NotNull(message = "Crew ID is required")
    private Long crewId;

    @NotNull(message = "Flight ID is required")
    private Long flightId;

    @NotBlank(message = "Assignment role is required")
    private String assignmentRole;

    @NotNull(message = "Assignment start time is required")
    private LocalDateTime assignmentStartTime;

    @NotNull(message = "Assignment end time is required")
    private LocalDateTime assignmentEndTime;

    @NotBlank(message = "Status is required")
    private String status;

    public Long getCrewId() {
        return crewId;
    }

    public void setCrewId(Long crewId) {
        this.crewId = crewId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getAssignmentRole() {
        return assignmentRole;
    }

    public void setAssignmentRole(String assignmentRole) {
        this.assignmentRole = assignmentRole;
    }

    

    public LocalDateTime getAssignmentStartTime() {
		return assignmentStartTime;
	}

	public void setAssignmentStartTime(LocalDateTime assignmentStartTime) {
		this.assignmentStartTime = assignmentStartTime;
	}

	public LocalDateTime getAssignmentEndTime() {
		return assignmentEndTime;
	}

	public void setAssignmentEndTime(LocalDateTime assignmentEndTime) {
		this.assignmentEndTime = assignmentEndTime;
	}

	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}