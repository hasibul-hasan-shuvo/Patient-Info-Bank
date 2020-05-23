package finalyear.project.patientinfobank.View.Patient.Notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.common.base.Strings.isNullOrEmpty
import com.squareup.picasso.Picasso
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Notification.NotificationUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.databinding.ActivityNotificationViewBinding

class NotificationView : AppCompatActivity() {

    private val TAG = "NotificationView"

    private lateinit var binding: ActivityNotificationViewBinding

    private lateinit var notificationUtils: NotificationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_view)
        window.enterTransition = null

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_view)

        notificationUtils = intent
            .getSerializableExtra(Util.NOTIFICATION_DATA) as NotificationUtils

        setUpToolbar()

        setViews()
    }

    private fun setViews() {
        binding.title.text = notificationUtils.title
        binding.message.text = notificationUtils.message

        if (!isNullOrEmpty(notificationUtils.imageUrl)) {
            Picasso.get()
                .load(notificationUtils.imageUrl)
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .into(binding.image)
        }
    }


    // Setting toolbar
    private fun setUpToolbar() {
        binding.toolbar.title = notificationUtils.title
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
