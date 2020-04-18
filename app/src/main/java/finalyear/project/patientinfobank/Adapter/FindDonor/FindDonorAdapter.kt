package finalyear.project.patientinfobank.Adapter.FindDonor

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.BloodDonor.BloodDonorInformationUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.Patient.Blood.DonorInformation
import kotlinx.android.synthetic.main.view_donor_list.view.*
import android.util.Pair as UtilPair

class FindDonorAdapter(
    val context: Context,
    val bloodDonorInformationList: ArrayList<BloodDonorInformationUtils>):
    RecyclerView.Adapter<FindDonorAdapter.ViewHolder>() {


    // Creating the view
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

    // Binding the item views
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.bloodGroup.text = bloodDonorInformationList[position].bloodGroup
        holder.itemView.name.text = bloodDonorInformationList[position].fullName
        holder.itemView.division.text = bloodDonorInformationList[position].division
        holder.itemView.district.text = bloodDonorInformationList[position].district

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DonorInformation::class.java)

            // Passing selected item data
            intent.putExtra(Util.DONOR_INFORMATION, bloodDonorInformationList[position])


            // Creating animation options
            val options = ActivityOptions
                .makeSceneTransitionAnimation(
                    context as Activity,
                    UtilPair.create(
                        holder.itemView.imageView,
                        context.resources.getString(R.string.donorBloodGroupIconTransition)
                    ),
                    UtilPair.create(
                        holder.itemView.bloodGroup,
                        context.resources.getString(R.string.donorBloodGroupTransition)
                    ),
                    UtilPair.create(
                        holder.itemView.name,
                        context.resources.getString(R.string.donorNameTransition)
                    ),
                    UtilPair.create(
                        holder.itemView.division,
                        context.resources.getString(R.string.donorDivisionTransition)
                    ),
                    UtilPair.create(
                        holder.itemView.district,
                        context.resources.getString(R.string.donorDistrictTransition)
                    )
                )
            context.window.exitTransition = null

            context.startActivity(intent, options.toBundle())
        }
    }

    // Holding the view
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {}
}