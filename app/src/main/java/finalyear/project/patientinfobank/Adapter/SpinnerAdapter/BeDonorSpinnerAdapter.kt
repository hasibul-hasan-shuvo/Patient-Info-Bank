package finalyear.project.patientinfobank.Adapter.SpinnerAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import finalyear.project.patientinfobank.R
import kotlinx.android.synthetic.main.view_user_category_spinner.view.*

class BeDonorSpinnerAdapter(context: Context, val items: Array<String>): ArrayAdapter<String>(context, 0, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent, false)
    }

    override fun getItem(position: Int): String? = items[position]

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent, true)
    }

    private fun initView(position: Int, convertView: View?, viewGroup: ViewGroup, isDropDown: Boolean ):View {
        var view: View
        if(convertView == null) {
            view = LayoutInflater.from(context).inflate(
                R.layout.view_be_donor_spinner,
                viewGroup,
                false
            )
        } else {
            view = convertView
        }

        if (getItem(position) != null) {
            view.spinnerItemText.text = items[position]
        }

        if (isDropDown) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            if (position == items.size - 1) {
                view.spinnerItemDivider.visibility = View.GONE
            }
        } else {
            view.spinnerItemDivider.visibility = View.GONE
        }


        return view

    }
}