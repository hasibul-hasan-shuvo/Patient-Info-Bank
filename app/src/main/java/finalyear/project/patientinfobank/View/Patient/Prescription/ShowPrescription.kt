package finalyear.project.patientinfobank.View.Patient.Prescription

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.common.base.Strings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import finalyear.project.patientinfobank.Adapter.Prescription.PrescriptionMedicineAdapter
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Prescription.MedicineUtils
import finalyear.project.patientinfobank.Utils.Prescription.PrescriptionUtils
import finalyear.project.patientinfobank.Utils.UserCategory.UserCategoryUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.Utils.UtilFunctions
import finalyear.project.patientinfobank.View.Doctor.Prescription.AdviceWrite
import finalyear.project.patientinfobank.View.Doctor.Prescription.CCWrite
import finalyear.project.patientinfobank.View.Doctor.Prescription.MedicineWrite
import finalyear.project.patientinfobank.View.Doctor.Prescription.OEWrite
import finalyear.project.patientinfobank.databinding.ActivityShowPrescriptionBinding
import java.util.*

class ShowPrescription : AppCompatActivity() , View.OnClickListener{

    private val TAG = "ShowPrescription"

    private lateinit var binding: ActivityShowPrescriptionBinding
    private var writeVisible = false


    private var prescription: PrescriptionUtils? = null
    private lateinit var date: String
    private lateinit var doctorInformation: UserCategoryUtils
    private var ccList = arrayListOf<String>()
    private var oeList = arrayListOf<String>()
    private var adviceList = arrayListOf<String>()
    private var medicineList = arrayListOf<MedicineUtils>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_prescription_write)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_prescription)


        setUpToolbar()
        setUpListeners()

        fetchIntentData()

        Log.d(TAG, "CCList: $ccList")
        setDoctorInformation()
        setCCList()
        setOEList()
        setAdviceList()
        setMedicineList()
    }


    // Fetching data from parent activity
    private fun fetchIntentData() {

        prescription = intent.getSerializableExtra(Util.PRESCRIPTION_DATA) as? PrescriptionUtils

        date = prescription?.date!!
        doctorInformation = prescription?.doctorUtils!!
        ccList.addAll(prescription!!.CCList)
        oeList.addAll(prescription!!.OEList)
        adviceList.addAll(prescription!!.adviceList)
        medicineList.addAll(prescription!!.medicineList)
    }

    // Setting doctor information
    private fun setDoctorInformation() {
        binding.name.text = doctorInformation.name
        binding.date.text = date
    }

    // Setting cc list
    private fun setCCList() {
        val adapter = ArrayAdapter(
            this,
            R.layout.view_prescription_cc_oe_advice_list,
            R.id.text,
            ccList
        )
        if (ccList.size == 0)
            binding.ccList.adapter = null
        else {
            binding.ccList.adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }

    // Setting oe list
    private fun setOEList() {
        val adapter = ArrayAdapter(
            this,
            R.layout.view_prescription_cc_oe_advice_list,
            R.id.text,
            oeList
        )

        if (oeList.size == 0)
            binding.oeList.adapter = null
        else {
            binding.oeList.adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }

    // Setting advice list
    private fun setAdviceList() {
        val adapter = ArrayAdapter(
            this,
            R.layout.view_prescription_cc_oe_advice_list,
            R.id.text,
            adviceList
        )

        if (adviceList.size == 0)
            binding.adviceList.adapter = null
        else {
            binding.adviceList.adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }

    // Setting medicine list
    private fun setMedicineList() {
        val adapter = PrescriptionMedicineAdapter(this, medicineList)
        if (medicineList.size == 0)
            binding.medicineList.adapter = null
        else {
            binding.medicineList.adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }




    private fun setUpListeners() {
        binding.zoom.setOnClickListener(this)
        binding.ccWriteButton.setOnClickListener(this)
        binding.oeWriteButton.setOnClickListener(this)
        binding.adviceWriteButton.setOnClickListener(this)
        binding.medicineWriteButton.setOnClickListener(this)
    }


    // Setting toolbar
    private fun setUpToolbar() {
        binding.toolbar.title = Util.PRESCRIPTION_TITLE
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.zoom -> {
                if (writeVisible)
                    closeOptionButtons()
                else
                    openOptionButtons()
            }
            R.id.ccWriteButton -> openZoomActivity(0)
            R.id.oeWriteButton -> openZoomActivity(1)
            R.id.adviceWriteButton -> openZoomActivity(2)
            R.id.medicineWriteButton -> openZoomActivity(3)
        }
    }

    private fun openZoomActivity(optionKey: Int) {
        var intent: Intent?

        when (optionKey) {
            0 -> {
                intent = Intent(this, ShowOECCAdvice::class.java)
                intent.putExtra(Util.CC_LIST, ccList)
            }
            1 -> {
                intent = Intent(this, ShowOECCAdvice::class.java)
                intent.putExtra(Util.OE_LIST, oeList)
            }
            2 -> {
                intent = Intent(this, ShowOECCAdvice::class.java)
                intent.putExtra(Util.ADVICE_LIST, adviceList)
            }
            else -> {
                intent = Intent(this, ShowMedicines::class.java)
                intent.putExtra(Util.MEDICINE_LIST, medicineList)
            }
        }

        Log.d(TAG, "$optionKey")
        startActivity(intent)
        overridePendingTransition(R.anim.left_to_right, R.anim.righttoleft)

    }
    // Closing option buttons with animation
    private fun closeOptionButtons() {
        if (binding.ccWriteButton.visibility == View.VISIBLE
            && binding.oeWriteButton.visibility == View.VISIBLE
            && binding.adviceWriteButton.visibility == View.VISIBLE
            && binding.medicineWriteButton.visibility == View.VISIBLE
        ) {
            val duration: Long = 300
            val animation = AnimationUtils.loadAnimation(this, R.anim.positiontobottom)


            binding.ccWriteButton.startAnimation(animation)
            binding.ccWriteButton.postDelayed({
                binding.ccWriteButton.visibility = View.GONE
                binding.oeWriteButton.startAnimation(animation)
                binding.oeWriteButton.postDelayed({
                    binding.adviceWriteButton.startAnimation(animation)
                    binding.oeWriteButton.visibility = View.GONE
                    binding.adviceWriteButton.postDelayed({
                        binding.medicineWriteButton.startAnimation(animation)
                        binding.adviceWriteButton.visibility = View.GONE
                        binding.medicineWriteButton.postDelayed({
                            binding.medicineWriteButton.visibility = View.GONE
                            writeVisible = false
                        }, duration)
                    }, duration)
                }, duration)
            }, duration)
        }
    }

    // Opening option buttons with animation
    private fun openOptionButtons() {
        if (binding.ccWriteButton.visibility == View.GONE
            && binding.oeWriteButton.visibility == View.GONE
            && binding.adviceWriteButton.visibility == View.GONE
            && binding.medicineWriteButton.visibility == View.GONE
        ) {
            val duration: Long = 100

            val animation = AnimationUtils.loadAnimation(this, R.anim.bottom_to_up)
            val animation1 = AnimationUtils.loadAnimation(this, R.anim.bottom_to_up)
            val animation2 = AnimationUtils.loadAnimation(this, R.anim.bottom_to_up)
            val animation3 = AnimationUtils.loadAnimation(this, R.anim.bottom_to_up)


            binding.medicineWriteButton.startAnimation(animation)
            binding.medicineWriteButton.visibility = View.VISIBLE
            binding.medicineWriteButton.postDelayed({
                binding.adviceWriteButton.startAnimation(animation1)
                binding.adviceWriteButton.visibility = View.VISIBLE
                binding.adviceWriteButton.postDelayed({
                    binding.oeWriteButton.startAnimation(animation2)
                    binding.oeWriteButton.visibility = View.VISIBLE
                    binding.oeWriteButton.postDelayed({
                        binding.ccWriteButton.startAnimation(animation3)
                        binding.ccWriteButton.visibility = View.VISIBLE
                        writeVisible = true
                    }, duration)
                }, duration)
            }, duration)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft)
    }


    private fun stopProgress() {
        binding.progress.visibility = View.GONE
    }

    private fun runProgess() {

        val animation = AnimationUtils.loadAnimation(this, R.anim.heart_beat)
        binding.progressHeart.startAnimation(animation)

        binding.progress.visibility = View.VISIBLE
    }



}
