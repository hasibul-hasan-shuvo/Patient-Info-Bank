package finalyear.project.patientinfobank.Services

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import finalyear.project.patientinfobank.Utils.Util
import org.json.JSONException
import org.json.JSONObject

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
        Log.d(TAG, "Notification ${notification.title}")
    }

    private fun handleNotification(jsonObjectData: JSONObject) {
        Log.d(TAG, "Data: $jsonObjectData")
    }
}