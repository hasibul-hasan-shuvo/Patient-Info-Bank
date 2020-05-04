package finalyear.project.patientinfobank.Adapter.PatientHome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.UserCategory.UserCategoryUtils
import kotlinx.android.synthetic.main.view_list_doctor.view.*

class DoctorListSliderAdapter(
    val context: Context,
    val doctorList: ArrayList<UserCategoryUtils>
) : RecyclerView.Adapter<DoctorListSliderAdapter.ViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(
                    R.layout.view_list_doctor,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int = doctorList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            doctorName.text = "Dr. " + doctorList[position].name
            doctorPhoneNumber.text = doctorList[position].phoneNumber

            val arrayAdapter = ArrayAdapter(
                context,
                R.layout.view_slider_doctor_list,
                R.id.degree,
                doctorList[position].doctorDegreeList
            )

            degreesList.adapter = arrayAdapter
        }
    }

    // Holding the view
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {}
}