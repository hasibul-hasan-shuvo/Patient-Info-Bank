package finalyear.project.patientinfobank.Adapter.Reminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Reminder.MedicineReminderUtils
import finalyear.project.patientinfobank.View.CommonInterfaces.ItemView
import kotlinx.android.synthetic.main.view_reminder_list.view.*

class MedicineReminderAdapter(
    val context: Context,
    val reminderList: ArrayList<MedicineReminderUtils>,
    private val mainView: ItemView?
): RecyclerView.Adapter<MedicineReminderAdapter.ViewHolder>() {



    // Creating view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(
                    R.layout.view_reminder_list,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int = reminderList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView

        view.name.text = reminderList[position].name
        view.startDate.text = reminderList[position].startDate
        view.endDate.text = reminderList[position].endDate
        view.time.text = reminderList[position].time
        view.interval.text = reminderList[position].interval

        view.deleteButton.setOnClickListener {
            mainView?.onItemClick(position)
        }
    }



    // Holding the view
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {}
}