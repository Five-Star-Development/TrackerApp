package dev.five_star.trackingapp.core.location.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dev.five_star.trackingapp.core.location.R
import dev.five_star.trackingapp.core.location.data.LocationUpdate
import dev.five_star.trackingapp.core.location.data.toDomain
import dev.five_star.trackingapp.core.location.tracking.LocationContainer
import dev.five_star.trackingapp.core.location.tracking.LocationContainerProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Foreground service that keeps receiving locations, also when the app is closed.
 * Every location is published to [dev.five_star.trackingapp.core.location.tracking.TrackingStatus];
 * it is only uploaded to the repositories while upload is enabled.
 */
class LocationService : LifecycleService() {

    private val TAG = "LocationService"
    private val CHANNEL_ID = "tracking_service_channel"
    private val NOTIF_ID = 1303

    private lateinit var container: LocationContainer
    private var locationJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        container = (application as LocationContainerProvider).locationContainer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }

        // starting a location foreground service without permission throws on Android 14+
        if (!container.locationDataSource.hasPermission()) {
            Log.w(TAG, "location permission missing, stopping service")
            stopSelf()
            return START_NOT_STICKY
        }

        ServiceCompat.startForeground(
            this,
            NOTIF_ID,
            buildNotification(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION else 0
        )
        startLocationUpdates()
        return START_STICKY
    }

    private fun startLocationUpdates() {
        if (locationJob != null) {
            return
        }
        val status = container.trackingStatusImpl
        locationJob = lifecycleScope.launch {
            container.locationDataSource.getLocationUpdates()
                .catch { e ->
                    Log.e(TAG, "location updates failed", e)
                    stopSelf()
                }
                .collect { update ->
                    when (update) {
                        is LocationUpdate.Availability -> status.setLocationAvailable(update.isAvailable)
                        is LocationUpdate.Fix -> {
                            val location = update.location.toDomain()
                            status.updateLocation(location)
                            if (status.uploadEnabled.value) {
                                container.repositories.forEach { it.save(location) }
                            }
                        }
                    }
                }
        }
    }

    override fun onDestroy() {
        // the last location stays visible in the UI, but without a running service there is no signal
        container.trackingStatusImpl.setLocationAvailable(false)
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.tracking_channel_name), // User-visible name in App Info
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(serviceChannel)
    }

    private fun buildNotification(): Notification {
        val openIntent = packageManager.getLaunchIntentForPackage(packageName) ?: Intent()
        val openPendingIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopPendingIntent = PendingIntent.getService(
            this, 1,
            Intent(this, LocationService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.tracking_notification_title))
            .setContentText(getString(R.string.tracking_notification_text))
            .setSmallIcon(R.drawable.ic_notification_location)
            .setContentIntent(openPendingIntent)
            .addAction(0, getString(R.string.tracking_notification_stop), stopPendingIntent)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val ACTION_STOP = "dev.five_star.trackingapp.core.location.STOP_TRACKING"
    }
}
