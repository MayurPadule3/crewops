package com.crewops.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CrewRequest {
	
	@NotBlank
	private String employeeCode;
	
	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String role;
	
	@NotBlank
	private String baseAirport;
	
	@NotBlank
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
