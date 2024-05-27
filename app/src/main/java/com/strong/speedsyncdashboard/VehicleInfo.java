package com.strong.speedsyncdashboard;

import java.util.Map;

public class VehicleInfo {
    String carNumber;
    String Highway;
    float currentSpeed;
    Location location;
    String email;
    Map<String, Object> vehicleDetails;

    public VehicleInfo(String carNumber, String highway, float currentSpeed, Location location, Map<String, Object> vehicleDetails, String email) {
        this.carNumber = carNumber;
        this.Highway = highway;
        this.currentSpeed = currentSpeed;
        this.location = location;
        this.vehicleDetails = vehicleDetails;
        this.email = email;
    }

}
