package finalyear.project.patientinfobank.View.Patient.Prescription

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.common.base.Strings.isNullOrEmpty
import finalyear.project.patientinfobank.Adapter.Prescription.MedicineAdapter
import finalyear.project.patientinfobank.Database.RoomDatabaseManager
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Prescription.MedicineUtils
import finalyear.project.patientinfobank.Utils.Reminder.MedicineReminderUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.CommonInterfaces.ItemView
import finalyear.project.patientinfobank.databinding.ActivityShowMedicinesBinding
import kotlinx.android.synthetic.main.view_reminder_dialog.view.*
import java.util.*
import kotlin.collections.ArrayList

class ShowMedicines : AppCompatActivity(),  ItemView{

    private val TAG = "ShowMedicine"
    private var medicineList = arrayListOf<MedicineUtils>()
    private var startDate = arrayListOf<Int>()
    private var endDate = arrayListOf<Int>()

    private var time = arrayListOf<Int>()

    private lateinit var startDateString: String
    private lateinit var endDateString: String
    private lateinit var timeString: String


    private lateinit var interval: String


    private lateinit var binding: ActivityShowMedicinesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_medicines)

        // Fetching intent data
        if (intent.extras?.get(Util.MEDICINE_LIST) != null)
            medicineList.addAll(intent.extras?.get(Util.MEDICINE_LIST) as Collection<MedicineUtils>)

        // Rendering current date
        renderDate()

        renderTime()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_medicines)
        setUpToolbar()
    }



    override fun onResume() {
        super.onResume()

        setListView()
    }

    // Fetching current time
    private fun renderTime() {
        time.clear()
        val calendar = Calendar.getInstance()

        time.add(calendar.get(Calendar.HOUR))
        time.add(calendar.get(Calendar.MINUTE))
    }

    // Fetching current date
    private fun renderDate() {
        startDate.clear()
        endDate.clear()
        val calendar = Calendar.getInstance()
        startDate.add(calendar.get(Calendar.DAY_OF_MONTH))
        startDate.add(calendar.get(Calendar.MONTH)+1)
        startDate.add(calendar.get(Calendar.YEAR))

        Log.d(TAG, startDate.toString())
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1)


        endDate.add(calendar.get(Calendar.DAY_OF_MONTH))
        endDate.add(calendar.get(Calendar.MONTH)+1)
        endDate.add(calendar.get(Calendar.YEAR))
        Log.d(TAG, endDate.toString())
    }


    private fun setListView() {
        val adapter = MedicineAdapter(this, medicineList, false, this)
        binding.list.adapter = adapter
    }
    // Setting toolbar
    private fun setUpToolbar() {
        binding.toolbar.title = Util.MEDICINE_TITLE
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

    /** End toolbar setup **/

    override fun onItemClick(position: Int) {
        openDialog(position)
    }

    // Dialog interface
    private fun openDialog(position: Int) {
        val builder = AlertDialog.Builder(this)

        val dialogView = LayoutInflater
                .from(this)
                .inflate(
                    R.layout.view_reminder_dialog,
                    null
                )

        builder.setView(dialogView)

        setDateString()
        setTimeString()

        dialogView.startDate.text = startDateString
        dialogView.endDate.text = endDateString
        dialogView.startTime.text = timeString
        dialogView.endTime.text = timeString

        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialogView.set.setOnClickListener {
            interval = dialogView.interval.text.toString()
            if (!isNullOrEmpty(interval)) {
                if (isDateValid()) {
                    alertDialog.dismiss()

                    setDateString()
                    setTimeString()

                    Log.d(TAG, "$startDateString $endDateString")

                    saveReminder(position)
                    renderDate()
                    renderTime()
                } else {
                    if (dialogView.error.visibility == View.INVISIBLE)
                        dialogView.error.visibility = View.VISIBLE
                }
            } else {
                dialogView.interval.error = Util.EMPTY_ERROR_MESSAGE
            }
        }

        dialogView.cancel.setOnClickListener {
            alertDialog.dismiss()
            renderDate()
            renderTime()
        }

        dialogView.startDate.setOnClickListener {
            if (dialogView.error.visibility == View.VISIBLE)
                dialogView.error.visibility = View.INVISIBLE
            startDate = openDatePicker(startDate, dialogView.startDate)
            Log.d(TAG, startDate.toString())
        }
        dialogView.startTime.setOnClickListener {
            time = openTimePicker(time, dialogView)
        }
        dialogView.endDate.setOnClickListener {
            if (dialogView.error.visibility == View.VISIBLE)
                dialogView.error.visibility = View.INVISIBLE
            endDate = openDatePicker(endDate, dialogView.endDate)
        }


        alertDialog.show()


    }

    private fun setTimeString() {
        timeString = "%02d:%02d".format(
            time[0], time[1]
        )
    }

    private fun setDateString() {
        startDateString =  "%02d/%02d/%d".format(
            startDate[0], startDate[1], startDate[2]
        )

        endDateString = "%02d/%02d/%d".format(
            endDate[0], endDate[1], endDate[2]
        )
    }

    private fun isDateValid(): Boolean {


        val startCalendar = Calendar.getInstance()
        startCalendar.set(
            startDate[2],
            startDate[1]-1,
            startDate[0],
            time[1],
            time[0]
        )

        val endCalendar = Calendar.getInstance()
        endCalendar.set(
            endDate[2],
            endDate[1]-1,
            endDate[0],
            time[1],
            time[0]
        )
        Log.d(TAG, Calendar.getInstance().timeInMillis.toString())
        Log.d(TAG, startCalendar.timeInMillis.toString())
        Log.d(TAG, endCalendar.timeInMillis.toString())

        if (
            Calendar.getInstance().timeInMillis <= endCalendar.timeInMillis
            && startCalendar.timeInMillis < endCalendar.timeInMillis
        ) {
            return true
        }

        return false
    }

    private fun openTimePicker(
        time: ArrayList<Int>,
        dialogView: View
    ): ArrayList<Int> {
        val timePicker = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                time[0] = hourOfDay
                time[1] = minute

                setTimeString()
                dialogView.startTime.text = timeString

                dialogView.endTime.text = timeString
            },
            time[0],
            time[1],
            false
        )

        timePicker.show()

        return time
    }

    private fun openDatePicker(
        date: ArrayList<Int>,
        dateTextView: TextView
    ): ArrayList<Int> {

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                date[0] = day
                date[1] = month
                date[2] = year

                dateTextView.text = "%02d/%02d/%d".format(
                    date[0], date[1], date[2]
                )
            },
            date[2],
            date[1],
            date[0]
        )

        datePickerDialog.show()

        return date
    }


    private fun saveReminder(position: Int) {

        try {
            var id = medicineList[position].medicineName + timeString

            var medicineReminder: MedicineReminderUtils
            medicineReminder = MedicineReminderUtils(
                id,
                medicineList[position].medicineName,
                startDateString,
                endDateString,
                timeString,
                interval
            )
            val dbManager = RoomDatabaseManager.getInstance(this)

            dbManager?.getMedicineReminderDAO()?.insert(medicineReminder)

            Toast.makeText(
                this,
                Util.REMINDER_SUCCESSFUL_MESSAGE,
                Toast.LENGTH_SHORT
            ).show()

            Log.d(TAG, "Medicine Reminder: $medicineReminder")

        }catch (e: Exception) {

            Toast.makeText(
                this,
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
