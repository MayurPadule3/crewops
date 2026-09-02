package com.crewops.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CrewRequest {
	
	@NotBlank(message = "Employee code is required")
	private String employeeCode;
	
	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	private String email;
	
	@NotBlank(message = "Name is required")
	private String name;
	
	@NotBlank(message = "Role is required")
	private String role;
	
	@NotBlank(message = "Base Airport is required")
	private String baseAirport;
	
	@NotBlank(message = "Status is required")
	private String status;
	public String getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getBaseAirport() {
		return baseAirport;
	}
	public void setBaseAirport(String baseAirport) {
		this.baseAirport = baseAirport;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
