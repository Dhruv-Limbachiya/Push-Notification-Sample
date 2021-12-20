package com.example.pushnotificationsample.firebase

import android.util.Log
import com.example.pushnotificationsample.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

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

        // remote message is notification
        remoteMessage.notification?.let { notification ->
            handleNotification(notification)
        }

        // remote message is data message.
        if(remoteMessage.data.isNotEmpty()) {
            handleDataMessage(remoteMessage.data)
        }
    }

    /**
     * Handles data message and build & fire notification using data.
     */
    private fun handleDataMessage(data: Map<String, String>) {
        // TODO : Extract data from Map,build notification and fire notification.

        val title = data[TITLE_KEY]
        val message = data[MESSAGE_KEY]
        val image = data[IMAGE_URL_KEY]

        buildNotification(title,message,image)
    }

    /**
     * Handles notification in both the states(foreground/background).
     */
    private fun handleNotification(notification: RemoteMessage.Notification) {
        // TODO : Extract notification info,build notification and fire notification
        /**
         * Check app state(background/foreground).
         * if it's foreground then build and fire notification else firebase handle notification by itself in background state.
         */
    }


    private fun buildNotification(title: String?, message: String?, image: String?) {
        if(title != null && message != null) {
            if(image != null && image.isNotEmpty()) {
                // Todo : show Big Picture Style notification
//                showBigPictureStyleNotification(title,message,image)
                return
            }

            // show normal notification
            // Todo: show Big Text or default style notification.
        }

    }


    /**
     * Stores newly generated token in shared preference.
     */
    private fun storeTokenInSharedPreference(token: String) {
        val appName = applicationContext.getString(R.string.app_name)
        val sharedPref = applicationContext.getSharedPreferences(appName, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(GCM_TOKEN_KEY,token)
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
        private const val MESSAGE_KEY = "message"
        private const val IMAGE_URL_KEY = "image"

        const val GCM_TOKEN_KEY = "GCM_TOKEN"
    }
}

