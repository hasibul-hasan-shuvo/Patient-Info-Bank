package finalyear.project.patientinfobank.Utils.Prescription

import android.util.Log
import finalyear.project.patientinfobank.Utils.UserCategory.UserCategoryUtils
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class PrescriptionUtils(
    var key: String,
    var date: String,
    var doctorUtils: UserCategoryUtils?,
    var CCList: ArrayList<String>,
    var OEList: ArrayList<String>,
    var adviceList: ArrayList<String>,
    var medicineList: ArrayList<MedicineUtils>
): Serializable, Comparable<PrescriptionUtils>{
    constructor(): this(
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

        val thisDate = (Integer.parseInt(thisDates[2].trim()) * 1000) +
                ((Integer.parseInt(thisDates[1].trim())-1) * 100) +
                Integer.parseInt(thisDates[0].trim())

        val otherDate = (Integer.parseInt(otherDates[2].trim()) * 1000) +
                ((Integer.parseInt(otherDates[1].trim())-1) * 100) +
                Integer.parseInt(otherDates[0].trim())


        Log.d(TAG, thisDate.toString())
        Log.d(TAG, otherDate.toString())

        return otherDate - thisDate
    }

}