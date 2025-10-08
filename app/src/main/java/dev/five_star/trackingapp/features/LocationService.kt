package dev.five_star.trackingapp.features

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import dev.five_star.trackingapp.MainActivity
import dev.five_star.trackingapp.R
import java.util.Date

class LocationService : Service() {

    private val TAG = "LocationService"
    private val CHANNEL_ID = "tracking_service_channel"
    private val NOTIF_ID = 1303

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        buildLocationCallback()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIF_ID, buildNotification("Tracking active"))
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        stopLocationUpdates()
        super.onDestroy()
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

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (loc in result.locations) {
                    handleLocation(loc)
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                // Optional: notify server if location not available
            }
        }
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

    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(
                PRIORITY_HIGH_ACCURACY, 0L
            ).apply {
                setMinUpdateIntervalMillis(2000L)
                setMinUpdateDistanceMeters(5F)
            }.build()
            fusedClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)


        } catch (e: SecurityException) {
            e.printStackTrace()
            stopSelf()
        }

    }

    private fun stopLocationUpdates() {
        fusedClient.removeLocationUpdates(locationCallback)
    }

    private fun handleLocation(location: Location) {
        Log.d(TAG, "onLocationResult.latitude: ${location.latitude}")
        Log.d(TAG, "onLocationResult.longitude: ${location.longitude}")
        Log.d(TAG, "onLocationResult.time: ${Date(location.time)}")
    }
}