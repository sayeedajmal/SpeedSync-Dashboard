package com.strong.speedsyncdashboard.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class NetworkChangeReceiver {

    public static void registerNetworkCallback(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

//        NETWORK REQUEST BUILDER TO BUILD THE NETWORK CHECKING OF WIFI AND CELLULAR
        NetworkRequest.Builder builder = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);

//        ON CHANGING OF NETWORK
        connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Toast.makeText(context, "Network Connected", Toast.LENGTH_SHORT).show();
                scheduleDataSync(context);
            }

            @Override
            public void onLost(@NonNull Network network) {
                Toast.makeText(context, "Network Lost", Toast.LENGTH_SHORT).show();
                super.onLost(network);
            }
        });
    }

//    SCHEDULE THE WORK (STORED THE DATA THAT IS DATASYNCWORKER) ONE BY ONE AND SET TO QUEUE FOR PUSHING
    private static void scheduleDataSync(Context context) {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

        OneTimeWorkRequest syncWorkRequest = new OneTimeWorkRequest.Builder(DataSyncWorker.class).setConstraints(constraints).build();

        WorkManager.getInstance(context).enqueue(syncWorkRequest);
    }
}
