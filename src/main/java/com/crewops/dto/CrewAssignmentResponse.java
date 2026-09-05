package com.crewops.dto;

import java.time.LocalDateTime;

public class CrewAssignmentResponse {

    private Long id;
    private Long crewId;
    private Long flightId;
    private String assignmentRole;
    private LocalDateTime assignmentStartTime;

    private LocalDateTime assignmentEndTime;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
