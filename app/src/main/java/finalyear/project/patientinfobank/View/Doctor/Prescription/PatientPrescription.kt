package finalyear.project.patientinfobank.View.Doctor.Prescription

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.databinding.ActivityPatientPrescriptionBinding

class PatientPrescription : AppCompatActivity() {

    private lateinit var searchId: String

    private lateinit var binding: ActivityPatientPrescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_prescription)
        window?.enterTransition  = null

        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_prescription)


        searchId = intent.getStringExtra(Util.SEARCH_PATIENT_ID)
        binding.searchId.setText(searchId)
        binding.searchId.setSelection(searchId.length)

        setUpToolbar()

    }

    private fun setUpToolbar() {
        binding.toolbar.title = Util.PATIENT_PRESCRIPTION
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
