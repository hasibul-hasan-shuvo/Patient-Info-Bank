package finalyear.project.patientinfobank.Adapter.Notification

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.common.base.Strings.isNullOrEmpty
import com.squareup.picasso.Picasso
import finalyear.project.patientinfobank.Database.RoomDatabaseManager
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Services.FirebaseNotificationManager
import finalyear.project.patientinfobank.Utils.Notification.NotificationUtils
import finalyear.project.patientinfobank.Utils.Reminder.MedicineReminderUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.CommonInterfaces.ItemView
import finalyear.project.patientinfobank.View.Patient.Blood.DonorInformation
import finalyear.project.patientinfobank.View.Patient.Notification.NotificationView
import kotlinx.android.synthetic.main.view_donor_list.view.*
import kotlinx.android.synthetic.main.view_notification_list.view.*
import kotlinx.android.synthetic.main.view_reminder_list.view.*
import kotlinx.android.synthetic.main.view_reminder_list.view.deleteButton
import kotlinx.android.synthetic.main.view_reminder_list.view.name

class NotificationAdapter(
    val context: Context,
    var notificationList: ArrayList<NotificationUtils>,
    private val mainView: ItemView?
): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {



    // Creating view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(
                    R.layout.view_notification_list,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int = notificationList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView

        if (!notificationList[position].isChecked) {
            view.title.setTextColor(context.getColor(R.color.newNotificationColor))
        }

        view.title.text = notificationList[position].title
        view.message.text = notificationList[position].message
        val date = notificationList[position].date
        val time = notificationList[position].time
        val imageUrl = notificationList[position].imageUrl

        view.dateTime.text = "$date, $time"

        if (!isNullOrEmpty(imageUrl)) {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .into(view.image)
        } else {
            view.image.scaleType = ImageView.ScaleType.FIT_CENTER
        }

        view.deleteButton.setOnClickListener {
            mainView?.onItemClick(position)
        }

        view.setOnClickListener {
            Log.d("Adapter", "NotificationView: $position")

            if (!notificationList[position].isChecked) {
                updateNotification(position)
            }


            val intent = Intent(context, NotificationView::class.java)

            // Passing selected item data
            intent.putExtra(Util.NOTIFICATION_DATA, notificationList[position])


            // Creating animation options
            val options = ActivityOptions
                .makeSceneTransitionAnimation(
                    context as Activity,
                    Pair.create(
                        view.image,
                        context.resources.getString(R.string.notificationImageTransition)
                    ),
                    Pair.create(
                        view.title,
                        context.resources.getString(R.string.notificationTitleTransition)
                    ),
                    Pair.create(
                        view.message,
                        context.resources.getString(R.string.notificationMessageTransition)
                    )
                )
            context.window.exitTransition = null

            context.startActivity(intent, options.toBundle())
        }
    }

    private fun updateNotification(position: Int) {
        val channelId = context.getString(R.string.default_notification_channel_id)
        val notificationManager = FirebaseNotificationManager(
            context,
            notificationList[position],
            channelId
        )
        notificationManager.cancelNotification(notificationList[position].Id)

        val roomDatabaseManager = RoomDatabaseManager.getInstance(context)
        notificationList[position].isChecked = true
        roomDatabaseManager
            ?.getNotificationDAO()
            ?.update(notificationList[position])
    }


    // Holding the view
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {}
}