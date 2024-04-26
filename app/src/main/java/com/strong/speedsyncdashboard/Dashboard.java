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
import com.strong.speedsyncdashboard.databinding.ActivityDashboardBinding;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a dashboard activity that allows users to monitor and adjust their speed on different highways.
 */
public class Dashboard extends AppCompatActivity {

    ActivityDashboardBinding BindDash; // Binding for the dashboard layout
    static int maxSpeed; // Maximum speed limit on the selected highway
    static CountDownTimer timer; // Countdown timer for speed limit violation
    static String Highway; // Highway used to limit the speed

    /**
     * This method is called when the activity is first created.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash = ActivityDashboardBinding.inflate(getLayoutInflater()); // Inflating the dashboard layout
        setContentView(BindDash.getRoot()); // Setting the content view to the root view of the dashboard layout

        // Array of highwayLists
        String[] highwayList = {"NH 1", "NH 12", "NH 15"};

        // Creating an ArrayAdapter for the highway spinner
        ArrayAdapter<String> highwayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, highwayList);
        BindDash.Highway.setAdapter(highwayAdapter); // Setting the adapter for the highway spinner

        // Setting maximum speed for the speedometer and initial values in speedAdjust and Highway
        BindDash.meter.setMaxSpeed(170);
        BindDash.speedAdjust.setValueTo(170);
        Highway = highwayList[0];

        // Listener for the highway spinner selection
        BindDash.Highway.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Resetting speedometer and speed adjust slider
                BindDash.meter.setSpeed(0, 100L, null);
                BindDash.speedAdjust.setValue(0);

                // Getting Highway and maximum speed limit for the selected highway
                Highway = highwayList[position];
                maxSpeed = SpeedLimit(highwayList[position]);

                // Updating UI with the selected highway and its speed limit
                BindDash.SpeedLimit.setText("Speed Limit: " + maxSpeed);
                Snackbar.make(BindDash.speedAdjust, highwayList[position] + " with Limit: " + maxSpeed, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Displaying a toast if no highway is selected
                Toast.makeText(Dashboard.this, "Please Select The Highway First", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener for the speed adjust slider
        BindDash.speedAdjust.addOnChangeListener((slider, value, fromUser) -> {
            // Setting speed on the speedometer
            BindDash.meter.setSpeed((int) slider.getValue(), 100L, null);


            // Checking if the speed exceeds the maximum speed limit
            if (slider.getValue() > maxSpeed) {
                // Changing UI to indicate speed limit violation
                BindDash.meter.setFillColor(getColor(R.color.OrangeRed));
                BindDash.meter.setTextColor(getColor(R.color.OrangeRed));
                BindDash.SpeedLimit.setTextColor(getColor(R.color.OrangeRed));
                BindDash.speedAdjust.setThumbStrokeColor(ColorStateList.valueOf(getColor(R.color.OrangeRed)));

                // Starting countdown timer for speed limit violation
                if (timer == null) {
                    timer = new CountDownTimer(10000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            // Displaying alert dialog for speed limit violation
                            SpeedLimitAlertDialog.showAlertDialog(Dashboard.this, "You Have Only 5 Seconds \nYou Have Only 5 Seconds \nYou Have Only 5 Seconds \n");
                        }

                        @Override
                        public void onFinish() {
                            // Getting the Info of Car by CarNumber and All other Details
                            Map<String, Object> info = new HashMap<>();
                            info.put("carColor", "Brown");
                            info.put("carModel", "MLX23");


                            // The Data of Car fetched by a Govt API.
                            ChallanGeneration.GenerateChallan(Dashboard.this, "SAYEED123", Highway, slider.getValue(), new Location(123, 123), info, httpStatusCode -> {
                                // Handle the HTTP status code received from ChallanGeneration
                                if (httpStatusCode == HttpURLConnection.HTTP_CREATED) {
                                    Snackbar.make(BindDash.speedAdjust, "Challan generated successfully", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(BindDash.speedAdjust, "Failed to generate challan, HTTP response code: " + httpStatusCode, Snackbar.LENGTH_SHORT).show();
                                }
                            });

                            // Dismissing alert dialog and generating challan after countdown
                            SpeedLimitAlertDialog.dismissAlertDialog();
                        }
                    }.start();
                }
            } else {
                // Resetting UI if speed is within the limit
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
        });
    }


    /**
     * Method to retrieve the speed limit for a given highway.
     *
     * @param highway The highway for which the speed limit is requested.
     * @return The speed limit for the given highway.
     */
    private int SpeedLimit(String highway) {
        // Switch case to determine speed limit based on the highway
        switch (highway) {
            case "NH 1":
                return 90;
            case "NH 12":
                return 120;
            case "NH 15":
                return 150;
        }
        return 0;
    }
}
