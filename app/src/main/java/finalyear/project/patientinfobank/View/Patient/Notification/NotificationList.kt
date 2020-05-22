package finalyear.project.patientinfobank.View.Patient.Notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import finalyear.project.patientinfobank.Database.RoomDatabaseManager
import finalyear.project.patientinfobank.R

class NotificationList : AppCompatActivity() {

    private val TAG = "NotificationList"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)

        val roomDatabaseManager = RoomDatabaseManager.getInstance(this)
        val notificationList = roomDatabaseManager?.getNotificationDAO()?.getAllNotifications()
        Log.d(TAG, "List: $notificationList")
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.righttoleft)
    }
}
