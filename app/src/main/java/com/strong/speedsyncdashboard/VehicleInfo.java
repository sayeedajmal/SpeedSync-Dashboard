package com.strong.speedsyncdashboard;

import java.util.Map;

public class VehicleInfo {
    private String carNumber;
    private String highway;
    private float currentSpeed;
    private String email;
    private Location location;
    private Map<String, Object> vehicleDetails;

    public VehicleInfo(String carNumber, String email, String highway, float currentSpeed, Location location,
            Map<String, Object> vehicleDetails) {
        this.carNumber = carNumber;
        this.email = email;
        this.highway = highway;
        this.currentSpeed = currentSpeed;
        this.location = location;
        this.vehicleDetails = vehicleDetails;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getHighway() {
        return highway;
    }

    public void setHighway(String highway) {
        this.highway = highway;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Map<String, Object> getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(Map<String, Object> vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

}
