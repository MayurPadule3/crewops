package com.crewops.dto;

import jakarta.validation.constraints.NotNull;

public class SchedulingRequest {

    @NotNull(message = "Crew ID is required")
    private Long crewId;

    @NotNull(message = "Flight ID is required")
    private Long flightId;

    @NotNull(message = "Assignment role is required")
    private String assignmentRole;

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
}