package com.example.pushnotificationsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pushnotificationsample.util.NotificationUtil

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationUtil.createNotificationChannel(this)
    }
}