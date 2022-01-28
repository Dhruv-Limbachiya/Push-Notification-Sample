package com.example.pushnotificationsample.util

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pushnotificationsample.R
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created By Dhruv Limbachiya on 27-01-2022 06:11 PM.
 */
object NotificationUtil {

    private const val NOTIFICATION_CHANNEL = "Push Notification Sample"
    private const val NOTIFICATION_CHANNEL_ID = "PUSH_NOTIFICATION_SAMPLE_CHANNEL_ID"
    private const val TAG = "NotificationUtil"
    private const val TEMP_NOTIFICATION_ID = 1
    private const val TEMP_NOTIFICATION_ID_2 = 2

    fun createNotificationChannel(context: Context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelName = NOTIFICATION_CHANNEL
            val importance = NotificationManager.IMPORTANCE_HIGH

            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                importance
            )

            getNotificationManager(context).createNotificationChannel(notificationChannel)
        }
    }

    /**
     * Normal notification
     */
    fun showNormalNotification(context: Context,title: String, message: String) {
        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)

        getNotificationManager(context).notify(TEMP_NOTIFICATION_ID_2,notificationBuilder.build())
    }

    /**
     * Build notification using big picture style template
     */
    fun showBigPictureStyleNotification(context: Context, title: String, message: String, bitmap: Bitmap) {

        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(bitmap)
            .bigLargeIcon(null)
            .setBigContentTitle(title)


        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(bitmap)
            .setStyle(bigPictureStyle)

        getNotificationManager(context).notify(TEMP_NOTIFICATION_ID,notificationBuilder.build());
    }


    /**
     * Helper methods
     */
    private fun getNotificationManager(context: Context) : NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun getBitmapFromUrl(imageUrl: String) : Bitmap?{
        return try {
            val url = URL(imageUrl)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.doInput = true
            httpURLConnection.connect()
            val inputStream = httpURLConnection.inputStream
            BitmapFactory.decodeStream(inputStream);
        } catch (e: IOException) {
            Log.e(TAG, "getBitmapFromUrl: ${e.message}", e)
            null;
        }
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

}