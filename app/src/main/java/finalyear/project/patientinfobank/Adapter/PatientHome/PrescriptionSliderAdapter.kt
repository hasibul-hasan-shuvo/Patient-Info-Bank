package finalyear.project.patientinfobank.Adapter.PatientHome

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Prescription.PrescriptionUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.Patient.Prescription.ShowPrescription
import kotlinx.android.synthetic.main.view_list_prescription.view.*

class PrescriptionSliderAdapter(
    val context: Context,
    val prescriptionList: ArrayList<PrescriptionUtils>
) : RecyclerView.Adapter<PrescriptionSliderAdapter.ViewHolder>(){


    private val TAG = "PrescriptionSlider"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(
                    R.layout.view_list_prescription,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int = prescriptionList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val name = prescriptionList[position].doctorUtils?.name
            holder.itemView.doctorName.text = "Dr. $name"
            holder.itemView.date.text = prescriptionList[position].date
            Log.d(TAG, prescriptionList[position].doctorUtils?.name)
            holder.itemView.setOnClickListener {
                Log.d(TAG, "Item clicked ${prescriptionList[position].CCList}")
                val intent = Intent(context, ShowPrescription::class.java)

                intent.putExtra(
                    Util.DOCTOR_INFORMATION,
                    prescriptionList[position].doctorUtils
                )

                intent.putExtra(
                    Util.PRESCRIPTION_DATE,
                    prescriptionList[position].date
                )

                intent.putExtra(
                    Util.CC_LIST,
                    prescriptionList[position].CCList
                )

                intent.putExtra(
                    Util.OE_LIST,
                    prescriptionList[position].OEList
                )

                intent.putExtra(
                    Util.ADVICE_LIST,
                    prescriptionList[position].adviceList
                )

                intent.putExtra(
                    Util.MEDICINE_LIST,
                    prescriptionList[position].medicineList
                )
                context.startActivity(intent)
                (context as Activity).
                overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft)
            }
        }catch (e: Exception) {
            Log.d(TAG, e.message)
        }
    }

    // Holding the view
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {}
}