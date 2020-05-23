package finalyear.project.patientinfobank.View.Patient.Notification

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import finalyear.project.patientinfobank.Adapter.Notification.NotificationAdapter
import finalyear.project.patientinfobank.Database.RoomDatabaseManager
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Notification.NotificationUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.CommonInterfaces.ItemView
import finalyear.project.patientinfobank.databinding.ActivityNotificationListBinding

class NotificationList : AppCompatActivity(), ItemView {

    private val TAG = "NotificationList"

    private lateinit var binding: ActivityNotificationListBinding

    private lateinit var userId: String
    
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener

    private var notificationList = arrayListOf<NotificationUtils>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_list)

        setUpToolbar()
        setUpSharedPreference()

    }

    override fun onResume() {
        super.onResume()
        getEmail()

        fetchData()
    }

    private fun fetchData() {
        val roomDatabaseManager = RoomDatabaseManager.getInstance(this)

        notificationList = roomDatabaseManager
            ?.getNotificationDAO()
            ?.getNotificationByUserId(userId) as ArrayList<NotificationUtils>

        notificationList.reverse()
        emptyChecker()
    }

    private fun emptyChecker() {
        if (notificationList.size == 0) {
            binding.emptyListMessage.visibility = View.VISIBLE
        } else {
            binding.emptyListMessage.visibility = View.GONE
        }

        setNotificationList()
    }

    private fun setNotificationList() {
        val notificationAdapter = NotificationAdapter(
            this,
            notificationList,
            this
        )

        binding.list.adapter = notificationAdapter

        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.setItemViewCacheSize(1000)
    }


    private fun getEmail() {
        userId = FirebaseAuth
            .getInstance()
            .currentUser
            ?.email
            ?.split(
                Regex("@")
            )!![0]
    }


    private fun setUpSharedPreference() {
        sharedPreferences = this.getSharedPreferences(
            Util.SHARED_PREFERENCE_PATH,
            MODE_PRIVATE
        )!!

        sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            Log.d(TAG, "SharedPreferencecs: $key")
            if (key == userId+ Util.NOTIFICATION_COUNTER) {
                fetchData()
                sharedPreferences
                    .edit()
                    .putInt(key, 0)
                    .apply()
            }
        }
    }

    // Setting toolbar
    private fun setUpToolbar() {
        binding.toolbar.title = Util.NOTIFICATION_LIST_TITLE
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

    override fun onItemClick(position: Int) {
        val roomDatabaseManager = RoomDatabaseManager.getInstance(this)
        roomDatabaseManager
            ?.getNotificationDAO()
            ?.delete(notificationList[position])

        notificationList.removeAt(position)

        setNotificationList()
    }

}
