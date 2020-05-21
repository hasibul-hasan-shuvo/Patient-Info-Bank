package finalyear.project.patientinfobank.Services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import finalyear.project.patientinfobank.Utils.Reminder.MedicineReminderUtils
import finalyear.project.patientinfobank.Utils.Util
import java.util.*
import kotlin.collections.ArrayList

class ReminderManager(
    val context: Context,
    val reminderUtils: MedicineReminderUtils
) {
    private val TAG = "ReminderManager"

    private var oneDayInMilliSecond = 60000

    private var REMINDER_REQUEST_CODE: Int = 0

    private lateinit var startDate: ArrayList<Int>
    private lateinit var endDate: ArrayList<Int>
    private lateinit var time: ArrayList<Int>
    private var interval: Int = 0

    private lateinit var startCalendar: Calendar
    private lateinit var endCalendar: Calendar

    private lateinit var alarmManager: AlarmManager


    fun create() {

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        startDate = parseDate(reminderUtils.startDate)
        endDate = parseDate(reminderUtils.endDate)
        time = parseTime(reminderUtils.time)
        interval = Integer.parseInt(reminderUtils.interval)

        REMINDER_REQUEST_CODE = getRequestCode()

        startCalendar = setCalendar(startDate)
        endCalendar = setCalendar(endDate)

        Log.d(TAG, "Start: ${startCalendar.timeInMillis}")
        Log.d(TAG, "End: ${endCalendar.timeInMillis}")


    }


    // Generating reminder request code
    private fun getRequestCode(): Int = time[0] * 100 + time[1]

    // Setting end date calendar
    private fun setCalendar(date: ArrayList<Int>): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(
            date[2],
            date[1]-1,
            date[0],
            time[0],
            time[1],
            0
        )

        return calendar
    }

    // Parsing time from string time
    private fun parseTime(time: String): ArrayList<Int> {
        val timeString = time.split(Regex(":"))
        val timeArray = arrayListOf<Int>()
        timeArray.add(Integer.parseInt(timeString[0]))
        timeArray.add(Integer.parseInt(timeString[1]))
        return  timeArray
    }

    // Parsing date from string date
    private fun parseDate(date: String): ArrayList<Int> {
        val dateString = date.split(Regex("/"))
        val dateArray = arrayListOf<Int>()

        dateArray.add(Integer.parseInt(dateString[0]))
        dateArray.add(Integer.parseInt(dateString[1]))
        dateArray.add(Integer.parseInt(dateString[2]))

        return  dateArray
    }


    private fun setPendingIntent(code: Int): PendingIntent {

        val reminderCode = REMINDER_REQUEST_CODE + code

        val reminderIntent = Intent(context, ReminderReceiver::class.java)
        val bundle = Bundle()
        bundle.putSerializable(Util.PENDING_REMINDER, reminderUtils)
        bundle.putInt(Util.PENDING_REMINDER_CODE, REMINDER_REQUEST_CODE)
        bundle.putInt(Util.REMINDER_CODE, code)
        bundle.putString(Util.REMINDER_ID, reminderCode.toString())
        bundle.putString(Util.NOTIFICATION_TITLE, reminderUtils.name)
        bundle.putString(Util.NOTIFICATION_MESSAGE, Util.MEDICINE_REMINDER_MESSAGE)
        reminderIntent.putExtra(Util.NOTIFICATION_BUNDLE, bundle)

        val pendingReminderIntent = PendingIntent.getBroadcast(
            context,
            reminderCode,
            reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return pendingReminderIntent
    }


    fun setReminder(): Int {
        val currentTime = Calendar.getInstance().timeInMillis
        val startTime = startCalendar.timeInMillis
        val endTime = endCalendar.timeInMillis

        var counter = 0

        for (reminderTime in startTime until endTime + 100
                step (AlarmManager.INTERVAL_DAY * interval)) {
            if (reminderTime >= currentTime) {
                counter++
                val reminderPendingIntent = setPendingIntent(counter)
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    reminderPendingIntent
                )
            }
        }
        Log.d(TAG, "TotalReminder: $counter")
        return counter
    }

    fun cancelReminder(): Boolean {
        for (code in 1..reminderUtils.totalReminder) {
            val reminderPendingIntent = setPendingIntent(code)
            alarmManager.cancel(reminderPendingIntent)
        }
        return true
    }
}