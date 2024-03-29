package finalyear.project.patientinfobank.View.Patient.Reminders

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import finalyear.project.patientinfobank.Adapter.Reminder.MedicineReminderAdapter
import finalyear.project.patientinfobank.Database.RoomDatabaseManager
import finalyear.project.patientinfobank.Services.ReminderManager
import finalyear.project.patientinfobank.Utils.Reminder.MedicineReminderUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.CommonInterfaces.ItemView
import finalyear.project.patientinfobank.databinding.FragmentPatientRemindersBinding

class PatientReminders : Fragment(), ItemView {
    private val TAG = "PatientReminders"
    private lateinit var binding: FragmentPatientRemindersBinding
    private var reminderList = arrayListOf<MedicineReminderUtils>()
    private lateinit var medicineReminderAdapter: MedicineReminderAdapter

    private lateinit var dbManager: RoomDatabaseManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "Created")

        binding = FragmentPatientRemindersBinding.inflate(inflater, container, false)

        dbManager = RoomDatabaseManager.getInstance(context!!)!!

        setUpToolbar()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    // Fetching data from local database
    private fun fetchData() {
        reminderList = dbManager?.getMedicineReminderDAO()?.getAllReminders() as ArrayList<MedicineReminderUtils>
        emptyChecker()
    }

    private fun setUpToolbar() {
        binding.toolbar.title = Util.REMINDER_TITLE
        binding.toolbar.setTitleTextColor(Color.WHITE)
    }

    private fun emptyChecker() {
        if (reminderList.isEmpty()) {
            binding.emptyListMessage.visibility = View.VISIBLE
        } else {
            binding.emptyListMessage.visibility = View.GONE
        }
        setUpReminderList()
    }

    // Setting reminders into list
    private fun setUpReminderList() {
        medicineReminderAdapter = MedicineReminderAdapter(
            context!!,
            reminderList,
            this
        )

        Log.d(TAG, "Activity: $activity")
        binding.list.adapter = medicineReminderAdapter

        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.setItemViewCacheSize(1000)
    }

    override fun onItemClick(position: Int) {
        Log.d(TAG, "Clicked: ${reminderList[position].name}")

        try {

            val reminderManager = ReminderManager(
                context = context!!,
                reminderUtils = reminderList[position]
            )

            reminderManager.create()

            // Canceling reminder
            if (reminderManager.cancelReminder()) {

                // Removing reminder from local database
                dbManager.getMedicineReminderDAO().delete(reminderList[position])
                reminderList.removeAt(position)
                Toast.makeText(
                    context,
                    Util.OPERATION_SUCCESSFUL_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
                emptyChecker()
            }
        }catch (e: Exception) {
            Toast.makeText(
                context,
                Util.OPERATION_FAILED_MESSAGE,
                Toast.LENGTH_SHORT
            ).show()
            Log.d(TAG, "Error: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RoomDatabaseManager.destroy()
    }

}
