package finalyear.project.patientinfobank.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.common.base.Strings.isNullOrEmpty
import finalyear.project.patientinfobank.Database.RoomDatabaseManager
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Notification.NotificationUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.Patient.Notification.NotificationList
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class FirebaseNotificationManager(
    val context: Context,
    val notificationUtils: NotificationUtils,
    val channelId: String?
) {
    private val TAG = "NotificationManager"

    // Building notification
    fun build() {
        try{
            val intent = Intent(context, NotificationList::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            val pendingNotification = PendingIntent.getActivity(
                context,
                notificationUtils.Id,
                intent,
                PendingIntent.FLAG_ONE_SHOT
            )

            if (isNullOrEmpty(notificationUtils.imageUrl)) {
                sendNotification(pendingNotification)
            } else {
                sendNotificationWithImage(pendingNotification)
            }

        }catch (e: Exception) {
            Log.d(TAG, "NotificationBuildError: ${e.message}")
        }
    }

    // Sending notification with image
    private fun sendNotificationWithImage(pendingNotification: PendingIntent?) {

        try {
            Log.d(TAG, "${notificationUtils.imageUrl}")
            val url = URL(notificationUtils.imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(input)

            val builder = NotificationCompat.Builder(
                context,
                channelId!!
            )
                .setContentTitle(notificationUtils.title)
                .setContentText(notificationUtils.message)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setContentIntent(pendingNotification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)

            val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    Util.NOTIFICATION_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )

                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(
                notificationUtils.Id,
                builder.build()
            )
        }catch (e: Exception) {
            Log.d(TAG, "NotificationSendErrorI: ${e.message}")
        }

    }


    // Sending notification without image
    private fun sendNotification(pendingNotification: PendingIntent?) {
        try {
            val builder = NotificationCompat.Builder(
                context,
                channelId!!
            )
                .setContentTitle(notificationUtils.title)
                .setContentText(notificationUtils.message)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingNotification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)

            val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    Util.NOTIFICATION_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )

                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(
                notificationUtils.Id,
                builder.build()
            )
        }catch (e: Exception) {
            Log.d(TAG, "NotificationSendError: ${e.message}")
        }
    }

    // Remove a notification from action bar
    fun cancelNotification(id: Int) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(id)

    }


    // Remove all notification from action bar
    fun cancelAllNotifications() {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancelAll()
    }


    // Function to call save notification
    fun save() {
        SaveNotification().execute()
        increaseNotificationCounter()
    }


    // Increasing notification counter
    private fun increaseNotificationCounter() {
        val sharedPreferences = context.getSharedPreferences(
            Util.SHARED_PREFERENCE_PATH,
            Context.MODE_PRIVATE
        )

        val counterKey = notificationUtils.userId + Util.NOTIFICATION_COUNTER

        val notificationCounter = sharedPreferences.getInt(
            counterKey,
            0
        )

        sharedPreferences
            .edit()
            .putInt(counterKey, notificationCounter + 1)
            .apply()

        Log.d(TAG, "Counter: $notificationCounter")
    }


    // Saving notification data into local database
    private inner class SaveNotification: AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val roomDatabaseManager = RoomDatabaseManager.getInstance(context)
                roomDatabaseManager?.getNotificationDAO()?.insert(notificationUtils)
            }catch (e: Exception) {
                Log.d(TAG, "NotificationSavingError: ${e.message}")
            }
            return null
        }

    }
}