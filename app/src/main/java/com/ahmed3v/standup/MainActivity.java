package com.ahmed3v.standup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * MainActivity for the Stand up! app. Contains a toggle button that
 * sets an alarm which delivers a Stand up notification every hour.
 */
public class MainActivity extends AppCompatActivity {

    // Notification ID.
    private static final int NOTIFICATION_ID = 0;

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    private ToggleButton alarmToggle;
    private NotificationManager mNotificationManager;
    private AlarmManager alarmManager;

    private PendingIntent notifyPendingIntent;
    private Intent notifyIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        alarmToggle = findViewById(R.id.alarmToggle);

        // Set up the Notification Broadcast Intent.
        notifyIntent = new Intent(this, AlarmReceiver.class);

        boolean alarmUp = (PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);

        alarmToggle.setChecked(alarmUp);

        notifyPendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        // Set the click listener for the toggle button.
        alarmToggle.setOnCheckedChangeListener((buttonView , isChecked) -> {

            String toastMessage;

            if(isChecked) {

                long repeatInterval = AlarmManager.INTERVAL_HOUR;
                long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

                // If the Toggle is turned on, set the repeating alarm with
                // an hour interval.
                if(alarmManager != null) {

                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP ,
                            triggerTime , repeatInterval , notifyPendingIntent);

                }

                // Set the toast message for the "on" case.
                toastMessage = getString(R.string.toast_alarm_on);

            }else {

                // Cancel notification if the alarm is turned off.
                mNotificationManager.cancelAll();

                if(alarmManager != null) {

                    alarmManager.cancel(notifyPendingIntent);
                }

                // Set the toast message for the "off" case.
                toastMessage = getString(R.string.toast_alarm_off);
            }

            // Show a toast to say the alarm is turned on or off.
            Toast.makeText(this , toastMessage, Toast.LENGTH_SHORT).show();
        });

        // Show a toast to say the alarm is turned on or off.
        createNotificationChannel();
    }

    /**
     * Creates a Notification channel, for OREO (API level 27) and higher.
     */
    public void createNotificationChannel(){

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "stand up notification", NotificationManager.IMPORTANCE_HIGH);


            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("It's been an hour stand up and take a walk");

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}