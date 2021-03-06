package com.example.pushnotificationsample.firebase

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.pushnotificationsample.R
import com.example.pushnotificationsample.util.NotificationUtil
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom


/**
 * Created By Dhruv Limbachiya on 20-12-2021 01:20 PM.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * callback fire whenever new token is generated.
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "onNewToken: $token")

        // store token in shared preference for further use.
        storeTokenInSharedPreference(token)

        // register token to server.
        registerTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.i(TAG, "onMessageReceived: ")

        // remote message is notification
        remoteMessage.notification?.let { notification ->
            handleNotification(notification)
        }

        // remote message is data message.
        if (remoteMessage.data.isNotEmpty()) {
            handleDataMessage(remoteMessage.data)
        }
    }

    var title: String? = null
    var message: String? = null
    var image: String? = null

    /**
     * Handles data message and build & fire notification using data.
     */
    private fun handleDataMessage(data: Map<String, String>) {
        title = data[TITLE_KEY]

        message = data[MESSAGE_KEY]

        image = data[IMAGE_URL_KEY]

        loadImageFromUrl(image)
    }

    /**
     * Handles notification in both the states(foreground/background).
     */
    private fun handleNotification(notification: RemoteMessage.Notification) {
        /**
         * Check app state(background/foreground).
         * if it's foreground then build and fire notification else firebase handle notification by itself in background state.
         */
        if (!NotificationUtil.isAppIsInBackground(applicationContext)) {
            // In foreground
            title = notification.title
            message = notification.body
            image = notification.imageUrl.toString()

            loadImageFromUrl(image)
        } else {
            NotificationUtil.playSound(applicationContext);
        }
    }


    private fun buildNotification(
        title: String?,
        message: String?,
        bitmap: Bitmap? = null
    ) {
        if (title != null && message != null) {
            if (bitmap != null) {
                NotificationUtil.showBigPictureStyleNotification(
                    applicationContext,
                    title,
                    message,
                    bitmap
                )
                return
            }

            NotificationUtil.showNormalNotification(applicationContext, title, message)
            return
        }

    }


    private var target = object : com.squareup.picasso.Target {

        override fun onBitmapLoaded(bitmap: Bitmap?, from: LoadedFrom?) {
            buildNotification(title, message, bitmap)
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
    }

    /**
     * load the image from the url into custom target.
     */
    private fun loadImageFromUrl(image: String?) {
        val uiHandler = Handler(Looper.getMainLooper())
        if (image != null && image.isNotEmpty()) {
            uiHandler.post {
                Picasso.get()
                    .load(image)
                    .into(target)
            }
        } else {
            buildNotification(title, message) // build normal notification.
        }
    }


    /**
     * Stores newly generated token in shared preference.
     */
    private fun storeTokenInSharedPreference(token: String) {
        val appName = applicationContext.getString(R.string.app_name)
        val sharedPref = applicationContext.getSharedPreferences(appName, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(GCM_TOKEN_KEY, token)
        editor.apply()
    }

    /**
     * Register token to server
     */
    private fun registerTokenToServer(token: String) {
        Log.i(TAG, "registerTokenToServer: $token")
    }

    companion object {
        private const val TAG = "MyFirebaseMessaging"
        private const val TITLE_KEY = "title"
        private const val MESSAGE_KEY = "content"
        private const val IMAGE_URL_KEY = "imageUrl"

        const val GCM_TOKEN_KEY = "GCM_TOKEN"
    }
}

