package finalyear.project.patientinfobank.Adapter.Prescription

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.common.base.Strings.isNullOrEmpty
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Prescription.MedicineUtils
import finalyear.project.patientinfobank.View.Doctor.Prescription.MedicineWrite
import kotlinx.android.synthetic.main.view_medicine_view.view.*

class PrescriptionMedicineAdapter(
    context: Context,
    val medicineList: ArrayList<MedicineUtils>
): ArrayAdapter<MedicineUtils>(
    context,
    0,
    medicineList
) {
    private val TAG = "MedicineAdapter"

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(
                R.layout.view_prescription_medicine_view,
                parent,
                false
            )
        } else {
            view = convertView
        }

        if (getItem(position) != null) {
            view.medicineType.text = medicineList[position].medicineType
            view.medicineName.text = medicineList[position].medicineName
            view.medicineTime.text = medicineList[position].medicineTime
            view.mealTime.text = medicineList[position].mealTime
            view.medicinePeriod.text = medicineList[position].medicinePeriod

            Log.d(TAG, medicineList[position].comment)
            if (isNullOrEmpty(medicineList[position].comment))
                view.comment.visibility = View.GONE
            else
                view.comment.text = medicineList[position].comment
        }

        return view
    }

    override fun getItem(position: Int): MedicineUtils? = medicineList[position]


}