package com.strong.speedsyncdashboard;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.strong.speedsyncdashboard.Network.NetworkChangeReceiver;
import com.strong.speedsyncdashboard.Network.NetworkUtils;
import com.strong.speedsyncdashboard.databinding.ActivityDashboardBinding;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a dashboard activity that allows users to monitor and adjust their speed on different highways.
 */
public class Dashboard extends AppCompatActivity {

    ActivityDashboardBinding BindDash;
    static int maxSpeed;
    static String lane;
    static CountDownTimer timer;
    static String Highway;
    // Flag to track if a lane has been selected by the user
    boolean laneSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(BindDash.getRoot());

        // Register network callback to monitor connectivity changes
        NetworkChangeReceiver.registerNetworkCallback(this);

        // Array of highways to be displayed in the highway spinner
        String[] highwayList = {"Select", "NH 44", "NH 19", "NH 1100", "NH 67", "NH 340"};
        // Creating an ArrayAdapter for the highway spinner
        ArrayAdapter<String> highwayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, highwayList);
        BindDash.Highway.setAdapter(highwayAdapter);

        // Setting initial values for the speedometer and highway variables
        BindDash.meter.setMaxSpeed(0);
        Highway = highwayList[0];
        lane = "";

        // Listener for highway spinner selection
        BindDash.Highway.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Getting the selected highway
                Highway = highwayList[position];


                // Resetting lane selection flag and speedometer values
                laneSelected = false;
                BindDash.meter.setSpeed(0, 100L, null);
                BindDash.SpeedLimit.setText(null);


                // Populating the lane spinner based on the selected highway
                String[] availableLanes = getAvailableLanes(Highway);
                ArrayAdapter<String> laneAdapter = new ArrayAdapter<>(Dashboard.this, android.R.layout.simple_spinner_dropdown_item, availableLanes);
                BindDash.Lane.setAdapter(laneAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Dashboard.this, "Please Select The Highway First", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener for lane spinner selection
        BindDash.Lane.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Check if a valid lane is selected
                if (position != 0) {
                    laneSelected = true;
                    // Update the selected lane
                    lane = (String) parent.getItemAtPosition(position);
                    // Set the maximum speed limit based on lane selection
                    maxSpeed = SpeedLimit(Highway, lane);
                    BindDash.meter.setMaxSpeed(maxSpeed + 50); // Adding 50 to show cross speed limit
                    BindDash.speedAdjust.setValueTo(maxSpeed + 50);
                    BindDash.SpeedLimit.setText("Speed Limit: " + maxSpeed);
                    Snackbar.make(BindDash.speedAdjust, lane + " with Limit: " + maxSpeed, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if no lane is selected
            }
        });

        // Listener for speed adjust slider changes
        BindDash.speedAdjust.addOnChangeListener((slider, value, fromUser) -> {
            if (laneSelected && !lane.isEmpty()) {
                // Setting the current speed on the speedometer
                BindDash.meter.setSpeed((int) slider.getValue(), 100L, null);

                // Check if the speed exceeds the maximum speed limit
                if (slider.getValue() > maxSpeed) {
                    // Change UI to indicate speed limit violation
                    BindDash.meter.setFillColor(getColor(R.color.OrangeRed));
                    BindDash.meter.setTextColor(getColor(R.color.OrangeRed));
                    BindDash.SpeedLimit.setTextColor(getColor(R.color.OrangeRed));
                    BindDash.speedAdjust.setThumbStrokeColor(ColorStateList.valueOf(getColor(R.color.OrangeRed)));

                    // Start countdown timer for speed limit violation
                    if (timer == null) {
                        timer = new CountDownTimer(10000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                // Display alert dialog for speed limit violation
                                SpeedLimitAlertDialog.showAlertDialog(Dashboard.this, "You Have Only 10 Seconds \nYou Have Only 10 Seconds \nYou Have Only 10 Seconds \n");
                            }

                            @Override
                            public void onFinish() {
                                // Prepare car information for challan generation
                                Map<String, Object> info = new HashMap<>();
                                info.put("carColor", "Brown");
                                info.put("carModel", "MLX23");

                                NetworkUtils.checkAndStoreDataToSQLite(Dashboard.this, "SAYEED123", Highway, slider.getValue(), new Location(123, 123), info);
                                // Dismiss alert dialog after countdown
                                SpeedLimitAlertDialog.dismissAlertDialog();
                            }
                        }.start();
                    }
                } else {
                    // Reset UI if speed is within the limit
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    SpeedLimitAlertDialog.dismissAlertDialog();

                    BindDash.meter.setFillColor(getColor(R.color.Green));
                    BindDash.meter.setTextColor(getColor(R.color.Green));
                    BindDash.SpeedLimit.setTextColor(getColor(R.color.Green));
                    BindDash.speedAdjust.setThumbStrokeColor(ColorStateList.valueOf(getColor(R.color.Green)));
                }
            }
        });
    }

    // Method to get available lanes for a selected highway
    private String[] getAvailableLanes(String highway) {
        switch (highway) {
            case "NH 44":
            case "NH 67":
                return new String[]{"Select", "Lane 2", "Lane 4"};
            case "NH 19":
            case "NH 340":
                return new String[]{"Select", "Lane 2"};
            case "NH 1100":
                return new String[]{"Select", "Lane 4"};
            default:
                return new String[]{"Select"};
        }
    }

    // Method to get the speed limit based on highway and lane
    private int SpeedLimit(String highway, String lane) {
        switch (highway) {
            case "NH 44":
                return lane.equals("Lane 2") ? 80 : 120;
            case "NH 19":
                return 100;
            case "NH 1100":
                return 80;
            case "NH 67":
                return lane.equals("Lane 2") ? 90 : 110;
            case "NH 340":
                return 90;
            default:
                return 0;
        }
    }
}
