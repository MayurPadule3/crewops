package com.crewops.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AutoSchedulingRequest {

    @NotNull(message = "Flight ID is required")
    private Long flightId;

    @NotBlank(message = "Assignment role is required")
    private String assignmentRole;

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