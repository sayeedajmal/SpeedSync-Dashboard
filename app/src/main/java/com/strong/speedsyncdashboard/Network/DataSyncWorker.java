package com.strong.speedsyncdashboard.Network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.strong.speedsyncdashboard.Challan.ChallanGeneration;
import com.strong.speedsyncdashboard.LocalDB.DBHelper;
import com.strong.speedsyncdashboard.Location;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

//CLASS that performs work synchronously on a background thread provided by WorkManage
public class DataSyncWorker extends Worker {

    private final DBHelper dbHelper;

    public DataSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Cursor cursor = dbHelper.getAllData();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String carNumber = cursor.getString(cursor.getColumnIndex("car_number"));
                @SuppressLint("Range") String highway = cursor.getString(cursor.getColumnIndex("highway"));
                @SuppressLint("Range") float currentSpeed = cursor.getFloat(cursor.getColumnIndex("current_speed"));
                @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                @SuppressLint("Range") String infoJson = cursor.getString(cursor.getColumnIndex("info"));

                Map<String, Object> info = new Gson().fromJson(infoJson, HashMap.class);

                ChallanGeneration.GenerateChallan(getApplicationContext(), carNumber, highway, currentSpeed, new Location(latitude, longitude), info, httpStatusCode -> {
                    if (httpStatusCode == HttpURLConnection.HTTP_CREATED) {
                        Toast.makeText(getApplicationContext(), "Challan Synced To Cloud", Toast.LENGTH_SHORT).show();
                        dbHelper.deleteData(carNumber, highway, currentSpeed, latitude, longitude, infoJson);
                    } else
                        Toast.makeText(getApplicationContext(), "Challan Not Synced", Toast.LENGTH_SHORT).show();
                });

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return Result.success();
    }
}
