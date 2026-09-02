package com.crewops.dto;

public class CrewRequest {
	
	private String employeeCode;
	private String email;
	private String name;
	private String role;
	private String baseAirport;
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
