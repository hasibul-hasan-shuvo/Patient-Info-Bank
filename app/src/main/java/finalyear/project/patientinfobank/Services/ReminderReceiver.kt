package finalyear.project.patientinfobank.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import finalyear.project.patientinfobank.Database.RoomDatabaseManager
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Reminder.MedicineReminderUtils
import finalyear.project.patientinfobank.Utils.Util
import java.lang.Exception

class ReminderReceiver: BroadcastReceiver() {
    private val TAG = "ReminderReceiver"


    override fun onReceive(context: Context?, intent: Intent?) {

        try {

            // Rendering intent data
            val dbManager = RoomDatabaseManager.getInstance(context!!)
            val bundle = intent?.getBundleExtra(Util.NOTIFICATION_BUNDLE)
            var reminderUtils = bundle?.getSerializable(Util.PENDING_REMINDER) as MedicineReminderUtils

            reminderUtils = dbManager?.getMedicineReminderDAO()?.getMedicineReminder(reminderUtils.id)!!

            Log.d(TAG, "Reminder Total: ${reminderUtils.totalReminder}")
            Log.d(TAG, "Reminder number: ${bundle?.getInt(Util.REMINDER_CODE)}")

            // Building notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    bundle?.getString(Util.REMINDER_ID),
                    Util.REMINDER_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                val notificationManager = context?.getSystemService(NotificationManager::class.java)
                notificationManager?.createNotificationChannel(notificationChannel)
            }

            val notificationBuilder = NotificationCompat.Builder(
                context!!,
                bundle?.getString(Util.REMINDER_ID)!!
            )

            notificationBuilder
                .setContentTitle(bundle?.getString(Util.NOTIFICATION_TITLE))
                .setContentText(bundle?.getString(Util.NOTIFICATION_MESSAGE))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI).priority = NotificationCompat.DEFAULT_ALL

            val notificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManagerCompat.notify(
                bundle?.getInt(Util.PENDING_REMINDER_CODE),
                notificationBuilder.build()
            )

            /** Removing reminder from database
             * if it is last reminder
             */
            if (reminderUtils.totalReminder == bundle?.getInt(Util.REMINDER_CODE)) {
                dbManager.getMedicineReminderDAO().delete(reminderUtils)
            }

        }catch (e: Exception) {
            Log.d(TAG, "NotificationError: ${e.message}")

        }
    }
}