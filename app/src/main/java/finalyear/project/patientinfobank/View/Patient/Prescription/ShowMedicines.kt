package finalyear.project.patientinfobank.View.Patient.Prescription

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import finalyear.project.patientinfobank.Adapter.Prescription.MedicineAdapter
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Prescription.MedicineUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.CommonInterfaces.PrescriptionItemView
import finalyear.project.patientinfobank.databinding.ActivityShowMedicinesBinding

class ShowMedicines : AppCompatActivity(),  PrescriptionItemView{

    private val TAG = "ShowMedicine"
    private var medicineList = arrayListOf<MedicineUtils>()


    private lateinit var binding: ActivityShowMedicinesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_medicines)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_medicines)
        setUpToolbar()
    }


    override fun onStart() {
        super.onStart()
        // Fetching intent data
        if (intent.extras?.get(Util.MEDICINE_LIST) != null)
            medicineList.addAll(intent.extras?.get(Util.MEDICINE_LIST) as Collection<MedicineUtils>)
    }

    override fun onResume() {
        super.onResume()

        setListView()
    }


    private fun setListView() {
        val adapter = MedicineAdapter(this, medicineList, false, this)
        binding.list.adapter = adapter
    }
    // Setting toolbar
    private fun setUpToolbar() {
        binding.toolbar.title = Util.MEDICINE_TITLE
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.righttoleft)
    }

    override fun onItemClick(position: Int) {
        //
    }
}
