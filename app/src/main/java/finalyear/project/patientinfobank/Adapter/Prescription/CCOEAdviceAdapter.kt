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
import finalyear.project.patientinfobank.View.CommonInterfaces.PrescriptionItemView
import finalyear.project.patientinfobank.View.Doctor.Prescription.MedicineWrite
import kotlinx.android.synthetic.main.view_medicine_view.view.*
import kotlinx.android.synthetic.main.view_prescription_write_cc_oe_advice_list.view.*

class CCOEAdviceAdapter(
    context: Context,
    val list: ArrayList<String>,
    private val mainView: PrescriptionItemView?
): ArrayAdapter<String>(
    context,
    0,
    list
) {
    private val TAG = "MedicineAdapter"

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(
                R.layout.view_prescription_write_cc_oe_advice_list,
                parent,
                false
            )
        } else {
            view = convertView
        }

        if (getItem(position) != null) {
            view.text.text = list[position]


            view.deleteButton.setOnClickListener {
                mainView?.onItemClick(position)
            }
        }

        return view
    }

    override fun getItem(position: Int): String? = list[position]


}