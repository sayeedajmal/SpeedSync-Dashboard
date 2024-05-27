package com.strong.speedsyncdashboard.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.strong.speedsyncdashboard.Challan.ChallanGeneration;
import com.strong.speedsyncdashboard.LocalDB.DBHelper;
import com.strong.speedsyncdashboard.Location;

import java.net.HttpURLConnection;
import java.util.Map;

public class NetworkUtils {

    public static void checkAndStoreDataToSQLite(Context context, String email, String carNumber, String highway, float currentSpeed, Location location, Map<String, Object> info) {
        DBHelper mDBHelper = new DBHelper(context);

        if (isConnected(context)) {
            ChallanGeneration.GenerateChallan(context, carNumber, email, highway, currentSpeed, location, info, httpStatusCode -> {
                // Handle response code
                if (httpStatusCode == HttpURLConnection.HTTP_CREATED) {
                    Toast.makeText(context, "Challan Generated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Storing Challan", Toast.LENGTH_SHORT).show();
                    mDBHelper.insertData(carNumber, email, highway, currentSpeed, location.getLatitude(), location.getLongitude(), info);
                }
            });
        } else {
            // If not connected, store data to SQLite
            Toast.makeText(context, "Storing Challan", Toast.LENGTH_SHORT).show();
            mDBHelper.insertData(carNumber, email, highway, currentSpeed, location.getLatitude(), location.getLongitude(), info);
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
}
