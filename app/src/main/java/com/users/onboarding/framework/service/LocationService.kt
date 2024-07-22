package com.users.onboarding.framework.service

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.FirebaseFirestore
import com.users.onboarding.R
import org.koin.android.ext.android.inject

class LocationService : Service() {

    private val fusedLocationClient: FusedLocationProviderClient by inject()
    private val db: FirebaseFirestore by inject()

    private lateinit var serviceLooper: Looper
    private lateinit var serviceHandler: Handler

    private val runnable = object : Runnable {
        override fun run() {
            getLocationAndUpload()
            serviceHandler.postDelayed(this, INTERVAL)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val handlerThread = HandlerThread("LocationServiceThread").apply {
            start()
        }
        serviceLooper = handlerThread.looper
        serviceHandler = Handler(serviceLooper)
        serviceHandler.post(runnable)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceHandler.removeCallbacks(runnable)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getLocationAndUpload() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                uploadLocationToFirestore(location)
                showNotification()
            }
        }
    }

    private fun uploadLocationToFirestore(location: android.location.Location) {
        val locationData = hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("locations")
            .add(locationData)
            .addOnSuccessListener { documentReference ->
                Log.d("LocationService", "Location added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("LocationService", "Error adding location", e)
            }
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, "location_channel")
            .setSmallIcon(R.drawable.ic_map)
            .setContentTitle("Location Update")
            .setContentText("Your location has been updated.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, builder.build())
    }

    companion object {
        private const val INTERVAL = 300000L // 5 minutes
    }
}