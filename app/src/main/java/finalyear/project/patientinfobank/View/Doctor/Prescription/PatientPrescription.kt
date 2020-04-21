package finalyear.project.patientinfobank.View.Doctor.Prescription

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.databinding.ActivityPatientPrescriptionBinding

class PatientPrescription : AppCompatActivity() {

    private lateinit var patientId: String

    private lateinit var binding: ActivityPatientPrescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_prescription)
        window?.enterTransition  = null

        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_prescription)


        patientId = intent.getStringExtra(Util.SEARCH_PATIENT_ID)
        binding.searchId.setText(patientId)
        binding.searchId.setSelection(patientId.length)

        setUpToolbar()

        setUpListener()

    }

    // Setting write prescription listener
    private fun setUpListener() {
        binding.writePrescription.setOnClickListener {
            val intent = Intent(this, PatientPrescriptionWrite::class.java)
            intent.putExtra(Util.SEARCH_PATIENT_ID, patientId)
            startActivity(intent)
            overridePendingTransition(R.anim.bottom_right_corner_to_position, R.anim.positiontotop)
        }
    }

    // Setting toolbar
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
