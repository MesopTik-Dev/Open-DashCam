package com.opendashcam

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.IBinder
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RecorderService : Service() {
    private val CHANNEL_ID = "OpenDashcamRecorder"
    private lateinit var cameraExecutor: ExecutorService
    private var recording: Recording? = null
    private lateinit var videoCapture: VideoCapture<Recorder>

    private val maxStorageBytes = 500L * 1024 * 1024 // 500 MB

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("OpenDashcam")
            .setContentText("Recording in progress...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(1, notification)

        cameraExecutor = Executors.newSingleThreadExecutor()
        startRecording()
    }

    private fun startRecording() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val recorder = Recorder.Builder()
                .setExecutor(cameraExecutor)
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    videoCapture
                )

                recordNewSegment()
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun recordNewSegment() {
        val moviesDir =
            getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: filesDir
        val outFile = File(
            moviesDir,
            "segment_${System.currentTimeMillis()}.mp4"
        )

        val fileOutputOptions = FileOutputOptions.Builder(outFile).build()

        recording = videoCapture.output
            .prepareRecording(this, fileOutputOptions)
            .start(ContextCompat.getMainExecutor(this)) { event ->
                if (event is VideoRecordEvent.Finalize) {
                    // After one clip finalizes, start a new one
                    recordNewSegment()
                    cleanupOldFiles(moviesDir)
                }
            }
    }

    private fun cleanupOldFiles(dir: File) {
        val files = dir.listFiles()?.sortedBy { it.lastModified() } ?: return
        var total = files.sumOf { it.length() }
        for (file in files) {
            if (total <= maxStorageBytes) break
            total -= file.length()
            file.delete()
        }
    }

    override fun onDestroy() {
        recording?.stop()
        cameraExecutor.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

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
