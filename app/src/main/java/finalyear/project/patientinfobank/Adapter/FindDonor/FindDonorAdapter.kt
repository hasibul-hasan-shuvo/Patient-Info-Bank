package finalyear.project.patientinfobank.Adapter.FindDonor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.BloodDonor.BloodDonorInformationUtils
import kotlinx.android.synthetic.main.view_donor_list.view.*

class FindDonorAdapter(
    val context: Context,
    val bloodDonorInformationList: ArrayList<BloodDonorInformationUtils>):
    RecyclerView.Adapter<FindDonorAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(
                    R.layout.view_donor_list,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int = bloodDonorInformationList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.bloodGroup.text = bloodDonorInformationList[position].bloodGroup
        holder.itemView.name.text = bloodDonorInformationList[position].fullName
        holder.itemView.division.text = bloodDonorInformationList[position].division
        holder.itemView.district.text = bloodDonorInformationList[position].district

        holder.itemView.setOnClickListener {
            //
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {}
}