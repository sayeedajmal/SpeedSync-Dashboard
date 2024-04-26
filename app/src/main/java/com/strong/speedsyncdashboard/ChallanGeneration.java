package com.strong.speedsyncdashboard;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;


public class ChallanGeneration {
    static String URI = "Your Server URL";

    public static boolean GenerateChallan(Context context, String carName, String Highway, float currentSpeed, Location location, Map<String, Object> info) throws JSONException {
        // We have to Correct it..
        boolean[] isError = {true};

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Gson json = new Gson();
        VehicleInfo vehicleInfo = new VehicleInfo(carName, Highway, currentSpeed, location, info);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URI, new JSONObject(json.toJson(vehicleInfo)), response -> {
        }, error -> {
            isError[0] = Objects.requireNonNull(error.getLocalizedMessage()).contains("org.json.JSONException: End of input at character 0 of");
        });
        requestQueue.add(request);
        return isError[0];
    }


}

