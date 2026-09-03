package com.crewops.dto;

public class AirportResponse {
	
	 private Long id;
	    private String airportCode;
	    private String airportName;
	    private String city;
	    private String country;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getAirportCode() {
			return airportCode;
		}
		public void setAirportCode(String airportCode) {
			this.airportCode = airportCode;
		}
		public String getAirportName() {
			return airportName;
		}
		public void setAirportName(String airportName) {
			this.airportName = airportName;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}

}
