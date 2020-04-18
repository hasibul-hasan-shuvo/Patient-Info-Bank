package finalyear.project.patientinfobank.Utils

import android.util.Log
import java.util.*

class UtilFunctions {
    companion object{
        private val TAG = "UtilFunctions"
        fun getAge(date: String): Int {
            val birthDates = date.split("/")

            val day = birthDates[0]
            val month = birthDates[1]
            val year = birthDates[2]

            val birthCalendar = Calendar.getInstance()

            birthCalendar.set(year.toInt(), month.toInt()-1, day.toInt())

            Log.d(TAG, date)
            Log.d(TAG, birthCalendar.time.toString())


            var age = Calendar.getInstance().get(Calendar.YEAR) -
                    birthCalendar.get(Calendar.YEAR)
            // if current month is less than birth month then minus 1
            if (birthCalendar.get(Calendar.MONTH) >
                Calendar.getInstance().get(Calendar.MONTH))
                age--

            else if (birthCalendar.get(Calendar.MONTH) ==
                Calendar.getInstance().get(Calendar.MONTH)) {
                if (birthCalendar.get(Calendar.DAY_OF_YEAR) >
                    Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
                    age--
            }

            return age
        }
    }
}