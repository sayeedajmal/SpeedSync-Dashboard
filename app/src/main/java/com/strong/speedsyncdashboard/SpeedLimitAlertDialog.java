package com.strong.speedsyncdashboard;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

/**
 * The SpeedLimitAlertDialog class provides methods to show and dismiss an alert dialog indicating a speed limit violation.
 */
public class SpeedLimitAlertDialog {

    private static AlertDialog alertDialog; // Alert dialog instance
    private static MediaPlayer mediaPlayer; // Media player instance for alarm sound
    private static Vibrator vibrator; // Vibrator instance for vibration feedback

    private static final int REQUEST_OVERLAY_PERMISSION = 1001; // Request code for overlay permission

    /**
     * Displays an alert dialog indicating a speed limit violation.
     *
     * @param context The context in which the alert dialog is shown.
     * @param message The message to be displayed in the alert dialog.
     */
    public static void showAlertDialog(Context context, String message) {
        // Check if the app has permission to draw overlays
        if (!Settings.canDrawOverlays(context)) {
            // Request overlay permission if not granted
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            ((Dashboard) context).startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
        } else {
            // Check if an alert dialog is already showing
            if (alertDialog != null && alertDialog.isShowing()) {
                return;
            }

            // Create and configure the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
            View customView = LayoutInflater.from(context).inflate(R.layout.custom_warning, null, true);
            TextView messageTextView = customView.findViewById(R.id.messageTextView);
            messageTextView.setText(message);
            builder.setView(customView);

            // Create and show the alert dialog
            alertDialog = builder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            alertDialog.show();

            // Start playing alarm sound if not already playing
            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }

            // Start vibrating if vibrator is available and enabled
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 1000, 500}, 0));
            }
        }
    }

    /**
     * Dismisses the alert dialog and stops any associated media playback or vibration.
     */
    public static void dismissAlertDialog() {
        // Dismiss the alert dialog if showing
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        // Stop and release media player
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Cancel vibration
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }
}
