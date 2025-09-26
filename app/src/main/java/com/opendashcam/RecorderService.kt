package com.opendashcam

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class RecorderService : Service() {
    private val CHANNEL_ID = "OpenDashcamRecorder"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("OpenDashcam")
            .setContentText("Recording in progress...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(1, notification)

        // TODO: Initialize CameraX and start loop recording
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: Start video recording logic here
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO: Stop recording, release resources
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "OpenDashcam Recording",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
