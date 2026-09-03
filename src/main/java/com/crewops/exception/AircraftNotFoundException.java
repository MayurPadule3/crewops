package com.crewops.exception;

public class AircraftNotFoundException extends RuntimeException {

    public AircraftNotFoundException(String message) {
        super(message);
    }
}