package com.strong.speedsyncdashboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.strong.speedsyncdashboard.databinding.ActivityDashboardBinding;

public class Dashboard extends AppCompatActivity {

    ActivityDashboardBinding BindDash;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindDash = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(BindDash.getRoot());

        String[] highways = {"NH 1", "NH 12", "NH 15"};

        ArrayAdapter<String> highwayAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, highways);
        BindDash.Highway.setAdapter(highwayAdaptor);

        BindDash.Highway.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BindDash.meter.setSpeed(0, 100L, null);
                BindDash.speedAdjust.setValue(0);
                int maxSpeed = SpeedLimit(highways[position]);
                BindDash.SpeedLimit.setText("Limit: " + maxSpeed);
                BindDash.meter.setMaxSpeed(maxSpeed);
                BindDash.speedAdjust.setValueTo(maxSpeed);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Dashboard.this, "Please Select The Highway First", Toast.LENGTH_SHORT).show();
            }
        });

        BindDash.speedAdjust.addOnChangeListener((slider, value, fromUser) -> BindDash.meter.setSpeed((int) slider.getValue(), 100L, null));
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