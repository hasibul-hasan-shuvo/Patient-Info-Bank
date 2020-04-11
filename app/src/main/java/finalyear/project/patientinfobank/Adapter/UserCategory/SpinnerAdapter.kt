package finalyear.project.patientinfobank.Adapter.UserCategory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Spinner.SpinnerCategoryUtils
import kotlinx.android.synthetic.main.view_spinner.view.*

class SpinnerAdapter(context: Context, val categories: ArrayList<SpinnerCategoryUtils>): ArrayAdapter<SpinnerCategoryUtils>(context, 0, categories) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent, false)
    }

    override fun getItem(position: Int): SpinnerCategoryUtils? {
        return categories[position]
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent, true)
    }

    private fun initView(position: Int, convertView: View?, viewGroup: ViewGroup, isDropDown: Boolean ):View {
        var view: View
        if(convertView == null) {
            view = LayoutInflater.from(context).inflate(
                R.layout.view_spinner,
                viewGroup,
                false
            )
        } else {
            view = convertView
        }

        if (getItem(position) != null) {
            view.spinnerItemText.text = categories[position].itemName
            view.spinnerItemIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    categories[position].itemIcon
                )
            )
        }

        if (isDropDown) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            if (position == categories.size - 1) {
                view.spinnerItemDivider.visibility = View.GONE
            }
        } else {
            view.spinnerItemDivider.visibility = View.GONE
        }


        return view

    }
}