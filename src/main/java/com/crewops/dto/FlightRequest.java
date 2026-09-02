package com.crewops.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FlightRequest {
	
	@NotBlank(message = "Flight number is required")
	private String flightNumber;

	@NotBlank(message = "Departure airport is required")
	private String departureAirport;

	@NotBlank(message = "Arrival airport is required")
	private String arrivalAirport;

	@NotNull(message = "Departure time is required")
	private LocalDateTime departureTime;

	@NotNull(message = "Arrival time is required")
	private LocalDateTime arrivalTime;

	@NotBlank(message = "Aircraft code is required")
	private String aircraftCode;

	@NotBlank(message = "Status is required")
	private String status;
	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	public String getDepartureAirport() {
		return departureAirport;
	}
	public void setDepartureAirport(String departureAirport) {
		this.departureAirport = departureAirport;
	}
	public String getArrivalAirport() {
		return arrivalAirport;
	}
	public void setArrivalAirport(String arrivalAirport) {
		this.arrivalAirport = arrivalAirport;
	}
	public LocalDateTime getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(LocalDateTime departureTime) {
		this.departureTime = departureTime;
	}
	public LocalDateTime getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(LocalDateTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getAircraftCode() {
		return aircraftCode;
	}
	public void setAircraftCode(String aircraftCode) {
		this.aircraftCode = aircraftCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
