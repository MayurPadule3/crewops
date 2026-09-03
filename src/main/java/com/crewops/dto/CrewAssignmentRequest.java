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

    @NotNull(message = "Assignment time is required")
    private LocalDateTime assignmentTime;

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

    public LocalDateTime getAssignmentTime() {
        return assignmentTime;
    }

    public void setAssignmentTime(LocalDateTime assignmentTime) {
        this.assignmentTime = assignmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}