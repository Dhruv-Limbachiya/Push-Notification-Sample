package com.example.pushnotificationsample.util

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.pushnotificationsample.R
import kotlin.random.Random


/**
 * Created By Dhruv Limbachiya on 27-01-2022 06:11 PM.
 */
object NotificationUtil {

    private const val NOTIFICATION_CHANNEL = "Push Notification Sample"
    private const val NOTIFICATION_CHANNEL_ID = "PUSH_NOTIFICATION_SAMPLE_CHANNEL_ID"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelName = NOTIFICATION_CHANNEL
            val importance = NotificationManager.IMPORTANCE_HIGH

            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                importance
            ).apply {
                setSound(getSound(context), getNotificationAudioAttributes())
                enableLights(true);
                enableVibration(true);
            }

            getNotificationManager(context).createNotificationChannel(notificationChannel)
        }
    }

    /**
     * Normal notification
     */
    fun showNormalNotification(context: Context, title: String, message: String) {
        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set the interrupting behaviour by giving priority.
            .setAutoCancel(true)

        getNotificationManager(context).notify(
            getRandomNotificationId(),
            notificationBuilder.build()
        )
    }

    /**
     * Build notification using big picture style template
     */
    fun showBigPictureStyleNotification(
        context: Context,
        title: String,
        message: String,
        bitmap: Bitmap
    ) {

        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(bitmap)
            .bigLargeIcon(null)
            .setBigContentTitle(title)


        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(Notification.PRIORITY_MAX)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(bitmap)
            .setSound(getSound(context))
            .setStyle(bigPictureStyle)

        getNotificationManager(context).notify(
            getRandomNotificationId(),
            notificationBuilder.build()
        )
    }


    // Helper methods

    /**
     * Method to parse raw sound file into uri for notification.
     */
    private fun getSound(context: Context): Uri {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.notification_sound)
    }

    private fun getNotificationAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
    }

    /**
     * Generates and return random number from 0 to 1000
     */
    private fun getRandomNotificationId(): Int {
        return Random(System.currentTimeMillis()).nextInt(1000)
    }

    /**
     * Get the notification manager from the system service
     */
    private fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * Method checks if the app is in background or not
     */
    fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == context.packageName) {
                        isInBackground = false
                    }
                }
            }
        }
        return isInBackground
    }

    /**
     * plays notification sound
     */
    fun playSound(context: Context) {
        try {
            val alarmSound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.notification_sound)
            val r = RingtoneManager.getRingtone(context, alarmSound)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}