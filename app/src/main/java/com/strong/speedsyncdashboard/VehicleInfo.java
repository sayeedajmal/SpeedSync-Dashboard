package com.strong.speedsyncdashboard;

import java.util.Map;

public class VehicleInfo {
    private String carNumber;
    private String Highway;
    private float currentSpeed;
    private Location location;
    private Map<String, Object> vehicleDetails;

    public VehicleInfo(String carNumber, String highway, float currentSpeed, Location location, Map<String, Object> vehicleDetails) {
        this.carNumber = carNumber;
        Highway = highway;
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
        return Highway;
    }

    public void setHighway(String highway) {
        Highway = highway;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
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
