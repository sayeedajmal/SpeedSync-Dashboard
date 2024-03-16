package com.strong.speedsyncdashboard;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.strong.speedsyncdashboard.databinding.ActivityDashboardBinding;

public class Dashboard extends AppCompatActivity {

    ActivityDashboardBinding BindDash;
    static int maxSpeed;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(BindDash.getRoot());

        String[] highways = {"NH 1", "NH 12", "NH 15"};

        ArrayAdapter<String> highwayAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, highways);
        BindDash.Highway.setAdapter(highwayAdaptor);

        BindDash.meter.setMaxSpeed(170);
        BindDash.speedAdjust.setValueTo(170);
        BindDash.Highway.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BindDash.meter.setSpeed(0, 100L, null);
                BindDash.speedAdjust.setValue(0);
                maxSpeed = SpeedLimit(highways[position]);
                BindDash.SpeedLimit.setText("Speed Limit: " + maxSpeed);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Dashboard.this, "Please Select The Highway First", Toast.LENGTH_SHORT).show();
            }
        });

        BindDash.speedAdjust.addOnChangeListener((slider, value, fromUser) -> {
            BindDash.meter.setSpeed((int) slider.getValue(), 100L, null);
            if (slider.getValue() > maxSpeed) {
                BindDash.meter.setFillColor(getColor(R.color.OrangeRed));
                BindDash.meter.setTextColor(getColor(R.color.OrangeRed));
                BindDash.SpeedLimit.setTextColor(getColor(R.color.OrangeRed));
                BindDash.speedAdjust.setThumbStrokeColor(ColorStateList.valueOf(getColor(R.color.OrangeRed)));
                OverSpeed();
            } else {
                BindDash.meter.setFillColor(getColor(R.color.Green));
                BindDash.meter.setTextColor(getColor(R.color.Green));
                BindDash.SpeedLimit.setTextColor(getColor(R.color.Green));
                BindDash.speedAdjust.setThumbStrokeColor(ColorStateList.valueOf(getColor(R.color.Green)));
            }
        });
    }

    private void OverSpeed() {
        Toast.makeText(this, "Your Are OverSpeeding Limit", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Tracking up to 200 Meters", Toast.LENGTH_SHORT).show();
    }

    private int SpeedLimit(String Highway) {
        switch (Highway) {
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