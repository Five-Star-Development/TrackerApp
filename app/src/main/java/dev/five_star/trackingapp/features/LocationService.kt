package dev.five_star.trackingapp.features

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import dev.five_star.trackingapp.MainActivity
import dev.five_star.trackingapp.R

class LocationService : Service() {

    private val TAG = "LocationService"
    private val CHANNEL_ID = "tracking_service_channel"
    private val NOTIF_ID = 1303

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        startForeground(NOTIF_ID, buildNotification("Tracking active"))
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Location Tracking Channel", // User-visible name in App Info
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(serviceChannel)
    }

    private fun buildNotification(title: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            // Whare are this actually for?
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_MUTABLE else 0
        )

        // What can be left out?
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracker")
            .setContentText(title)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}