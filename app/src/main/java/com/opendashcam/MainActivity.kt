package com.opendashcam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startBtn = findViewById<Button>(R.id.btn_start)
        val stopBtn = findViewById<Button>(R.id.btn_stop)

        startBtn.setOnClickListener {
            val intent = Intent(this, RecorderService::class.java)
            startForegroundService(intent)
        }

        stopBtn.setOnClickListener {
            val intent = Intent(this, RecorderService::class.java)
            stopService(intent)
        }
    }
}
