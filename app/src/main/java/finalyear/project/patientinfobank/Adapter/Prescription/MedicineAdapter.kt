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
import finalyear.project.patientinfobank.View.CommonInterfaces.ItemView
import kotlinx.android.synthetic.main.view_medicine_view.view.*

class MedicineAdapter(
    context: Context,
    val medicineList: ArrayList<MedicineUtils>,
    val isDoctor: Boolean,
    private val mainView: ItemView?
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
                R.layout.view_medicine_view,
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

            if (isDoctor) {
                view.actionButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_delete))
            } else {
                view.actionButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_add_alert))
            }

            view.actionButton.setOnClickListener {
                mainView?.onItemClick(position)
            }
        }

        return view
    }

    override fun getItem(position: Int): MedicineUtils? = medicineList[position]


}