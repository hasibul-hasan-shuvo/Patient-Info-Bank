package finalyear.project.patientinfobank.Services

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Notification.NotificationUtils
import finalyear.project.patientinfobank.Utils.Util
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class FirebaseNotificationService: FirebaseMessagingService() {

    private val TAG = "NotificationSerivce"
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        firebaseAuth = FirebaseAuth.getInstance()

        Log.d(TAG, "MessageReceived: ${firebaseAuth.currentUser?.displayName}")

        if (p0.data.isNotEmpty()) {
            try {
                val jsonObject = JSONObject(p0.data.toString())
                val jsonObjectData = jsonObject.getJSONObject(Util.NOTIFICATION_JSON_DATA)
                Log.d(TAG, "Data: $jsonObjectData")

                handleNotification(jsonObjectData)
            } catch (e: JSONException) {
                Log.d(TAG, "JSONException: ${e.message}")
            }
        }

        if (p0.notification != null) {
            handleNotification(p0.notification!!)
        }

    }

    private fun handleNotification(notification: RemoteMessage.Notification) {
        Log.d(TAG, "NotificationUtils ${notification.title}")


        val dateTime = getDateTime()

        val notificationId = generateNotificationId(dateTime)
        val channelId: String? = getString(R.string.default_notification_channel_id)
        val title: String? = notification.title
        val message: String? = notification.body
        val topic: String? = notification.tag
        val imageUri = notification.imageUrl
        val userId = firebaseAuth.currentUser?.email?.split(Regex("@"))?.get(0)

        var imageUrl: String? = null

        if (imageUri != null) {
            imageUrl = imageUri.toString()
        }

        val date = "%02d/%02d/%02d".format(
            dateTime[2], dateTime[1], dateTime[0])

        val time = "%02d:%02d".format(
            dateTime[3],dateTime[4]
        )

        val notificationUtils = NotificationUtils(
            userId,
            notificationId,
            title,
            message,
            topic,
            imageUrl,
            date,
            time
        )

        val notificationManager = FirebaseNotificationManager(
            this,
            notificationUtils,
            channelId
        )

        notificationManager.save()

        notificationManager.build()

    }

    private fun handleNotification(jsonObjectData: JSONObject) {
        Log.d(TAG, "Data: $jsonObjectData")

        val dateTime = getDateTime()

        val notificationId = generateNotificationId(dateTime)
        val channelId: String? = getString(R.string.default_notification_channel_id)
        val title: String? = jsonObjectData.get(Util.NOTIFICATION_TITLE) as String
        val message: String? = jsonObjectData.get(Util.NOTIFICATION_MESSAGE) as String
        val topic: String? = jsonObjectData.get(Util.NOTIFICATION_TOPIC) as String
        val imageUrl: String? = jsonObjectData.get(Util.NOTIFICATION_IMAGE_URL) as String
        val userId = firebaseAuth.currentUser?.email?.split(Regex("@"))?.get(0)

        val date = "%02d/%02d/%02d".format(
            dateTime[2], dateTime[1], dateTime[0])

        val time = "%02d:%02d".format(
            dateTime[3],dateTime[4]
        )
            
        val notificationUtils = NotificationUtils(
            userId,
            notificationId,
            title,
            message,
            topic,
            imageUrl,
            date,
            time
        )

        val notificationManager = FirebaseNotificationManager(
            this,
            notificationUtils,
            channelId
        )

        notificationManager.save()

        notificationManager.build()

    }

    private fun generateNotificationId(dateTime: ArrayList<Int>): Int {


        val NOTIFICATION_ID = dateTime[1] * 100000000 +
                dateTime[2] * 1000000 +
                dateTime[3] * 10000 +
                dateTime[4] * 100 +
                dateTime[5]

        Log.d(TAG, "NotificationServiceUtils: $NOTIFICATION_ID")

        return NOTIFICATION_ID
    }

    private fun getDateTime(): ArrayList<Int> {
// Creating notification id based on time and day

        val dateTime = arrayListOf<Int>()
        // Creating notification id based on time and day
        dateTime.add(Calendar.getInstance().get(Calendar.YEAR))
        dateTime.add(Calendar.getInstance().get(Calendar.MONTH)+1)
        dateTime.add(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        dateTime.add(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
        dateTime.add(Calendar.getInstance().get(Calendar.MINUTE))
        dateTime.add(Calendar.getInstance().get(Calendar.SECOND))

        Log.d(TAG, "NotificationServiceUtils: $dateTime")

        return dateTime
    }
}