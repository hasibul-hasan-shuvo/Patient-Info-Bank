package finalyear.project.patientinfobank.View.Patient.Notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import finalyear.project.patientinfobank.R

class NotificationList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)
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
