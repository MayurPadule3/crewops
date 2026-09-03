package com.crewops.dto;

public class AircraftResponse {
	
	 private Long id;
	    private String aircraftCode;
	    private String aircraftType;
	    private Integer capacity;
	    private String status;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getAircraftCode() {
			return aircraftCode;
		}
		public void setAircraftCode(String aircraftCode) {
			this.aircraftCode = aircraftCode;
		}
		public String getAircraftType() {
			return aircraftType;
		}
		public void setAircraftType(String aircraftType) {
			this.aircraftType = aircraftType;
		}
		public Integer getCapacity() {
			return capacity;
		}
		public void setCapacity(Integer capacity) {
			this.capacity = capacity;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

	

}
