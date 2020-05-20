package finalyear.project.patientinfobank.Utils.Prescription

import android.util.Log
import finalyear.project.patientinfobank.Utils.UserCategory.UserCategoryUtils
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class PrescriptionUtils(
    var key: String,
    var date: String,
    var time: String,
    var doctorUtils: UserCategoryUtils?,
    var CCList: ArrayList<String>,
    var OEList: ArrayList<String>,
    var adviceList: ArrayList<String>,
    var medicineList: ArrayList<MedicineUtils>
): Serializable, Comparable<PrescriptionUtils>{
    constructor(): this(
        "",
        "",
        "",
        null,
        ArrayList<String>(),
        ArrayList<String>(),
        ArrayList<String>(),
        ArrayList<MedicineUtils>()
    )

    private val TAG = "PrescriptionUtils"

    override fun compareTo(other: PrescriptionUtils): Int {


        val thisDates = date.split(Regex("/"))
        val otherDates = other.date.split(Regex("/"))

        val thisTime = time.split(Regex(":"))
        val otherTime = other.time.split(Regex(":"))

        val thisDate = (Integer.parseInt(thisDates[2].trim()) * 10000000) +
                ((Integer.parseInt(thisDates[1].trim())-1) * 1000000) +
                (Integer.parseInt(thisDates[0].trim())* 10000) +
                (Integer.parseInt(thisTime[0].trim()) * 100) +
                Integer.parseInt(thisTime[1].trim())

        val otherDate = (Integer.parseInt(otherDates[2].trim()) * 10000000) +
                ((Integer.parseInt(otherDates[1].trim())-1) * 1000000) +
                (Integer.parseInt(otherDates[0].trim())* 10000) +
                (Integer.parseInt(otherTime[0].trim()) * 100) +
                Integer.parseInt(otherTime[1].trim())


        Log.d(TAG, thisDate.toString())
        Log.d(TAG, otherDate.toString())

        return otherDate - thisDate
    }

}