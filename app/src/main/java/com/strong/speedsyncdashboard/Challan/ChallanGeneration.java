package com.strong.speedsyncdashboard.Challan;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.strong.speedsyncdashboard.Location;
import com.strong.speedsyncdashboard.VehicleInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ChallanGeneration {
    public interface ChallanGenerationCallback {
        void onChallanGenerated(int httpStatusCode);
    }

    static String URI = "https://speedsyncrestful-production.up.railway.app/api/v1/vehicles/challan";

    public static void GenerateChallan(Context context, String carName, String Highway, float currentSpeed, Location location, Map<String, Object> info, ChallanGenerationCallback callback) {
        Gson json = new Gson();
        VehicleInfo vehicleInfo = new VehicleInfo(carName, Highway, currentSpeed, location, info);
        String jsonString = json.toJson(vehicleInfo);
        new GenerateChallanTask(callback).execute(jsonString);
    }

    private static class GenerateChallanTask extends AsyncTask<String, Void, Integer> {
        ChallanGenerationCallback callback;

        public GenerateChallanTask(ChallanGenerationCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(String... params) {
            String jsonString = params[0];
            int responseCode = -1;

            try {
                URL url = new URL(URI);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.write(jsonString.getBytes());
                }

                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                Log.e("Request ERROR", "Failed to generate challan", e);
            }

            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if (callback != null) {
                callback.onChallanGenerated(responseCode);
            }
        }
    }
}

