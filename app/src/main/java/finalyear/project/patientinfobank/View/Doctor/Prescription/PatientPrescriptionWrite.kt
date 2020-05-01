package finalyear.project.patientinfobank.View.Doctor.Prescription

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.common.base.Strings
import com.google.firebase.firestore.FirebaseFirestore
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.UserCategory.UserCategoryUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.Utils.UtilFunctions
import finalyear.project.patientinfobank.databinding.ActivityPatientPrescriptionWriteBinding
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import finalyear.project.patientinfobank.Adapter.Prescription.PrescriptionMedicineAdapter
import finalyear.project.patientinfobank.Utils.Prescription.MedicineUtils
import finalyear.project.patientinfobank.Utils.Prescription.PrescriptionUtils
import kotlinx.android.synthetic.main.activity_cc_write.*
import java.util.*

class PatientPrescriptionWrite : AppCompatActivity() , View.OnClickListener{
    private val TAG = "PatientPrescription"

    private lateinit var binding: ActivityPatientPrescriptionWriteBinding
    private lateinit var patientInfo: UserCategoryUtils
    private lateinit var patientId: String
    private var writeVisible = false

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var doctorUtils: UserCategoryUtils
    private var isDoctorPrescribedBefore = false
    private lateinit var doctorEmail: String
    private lateinit var doctorId: String

    private var ccList = arrayListOf<String>()
    private var oeList = arrayListOf<String>()
    private var adviceList = arrayListOf<String>()
    private var medicineList = arrayListOf<MedicineUtils>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_prescription_write)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_prescription_write)


        setUpToolbar()
        setUpListeners()
        fetchPatientInfo()
    }

    override fun onResume() {
        super.onResume()

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        FetchDoctorInformation().execute()
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

    // Setting views
    private fun setViews() {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)


        binding.name.text = patientInfo.name
        binding.age.text = UtilFunctions.getAge(patientInfo.patientBirthDate!!).toString()
        binding.date.text = "%02d/%02d/%d".format(day, month+1, year)
    }


    // Fetching patient info from database
    private fun fetchPatientInfo() {
        patientId = intent.getStringExtra(Util.SEARCH_PATIENT_ID)
        runProgess()
        val email = "$patientId@gmail.com"
        if (!Strings.isNullOrEmpty(email)) {
            val firebaseFirestore = FirebaseFirestore.getInstance()
            firebaseFirestore
                .collection(Util.USER_CATEGORY_DATABASE)
                .document(email)
                .get()
                .addOnSuccessListener {
                    if (it != null)
                        patientInfo = it.toObject(UserCategoryUtils::class.java)!!
                    setViews()
                    stopProgress()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        Util.DATA_NOT_FOUND_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()
                    stopProgress()
                }
        }
    }

    private fun setUpListeners() {
        binding.write.setOnClickListener(this)
        binding.ccWriteButton.setOnClickListener(this)
        binding.oeWriteButton.setOnClickListener(this)
        binding.adviceWriteButton.setOnClickListener(this)
        binding.medicineWriteButton.setOnClickListener(this)
    }

    // Setting menu bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Setting toolbar
    private fun setUpToolbar() {
        binding.toolbar.title = Util.PATIENT_PRESCRIPTION
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.done -> savePrescription()
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(it)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun savePrescription() {
        if (medicineList.size > 0) {

            val calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val email = firebaseAuth.currentUser?.email
            val key = ("${email?.split("@")?.get(0)}-$day${month+1}$year")

            Log.d(TAG, key)

            val prescriptionUtils = PrescriptionUtils(
                doctorUtils,
                ccList,
                oeList,
                adviceList,
                medicineList
            )

            firestore
                .collection(Util.PATIENT_PRESCRIPTION_DATABASE)
                .document(patientId)
                .collection(Util.PRESCRIPTION_DATABASE)
                .document(key)
                .set(prescriptionUtils)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        Util.PRESCRIPTION_SUCCESSFUL_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()

                    SaveMedicinesAndDoctorInformation().execute()
                    finish()
                    overridePendingTransition(R.anim.left_to_right, R.anim.righttoleft)

                }.addOnFailureListener {
                    Toast.makeText(
                        this,
                        Util.PRESCRIPTION_FAILED_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, it.message)
                }


        } else {
            Toast.makeText(
                this,
                Util.MEDICINE_LIST_IS_EMPTY,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.write -> {
                if (writeVisible)
                    closeOptionButtons()
                else
                    openOptionButtons()
            }
            R.id.ccWriteButton -> openEditorActivity(0)
            R.id.oeWriteButton -> openEditorActivity(1)
            R.id.adviceWriteButton -> openEditorActivity(2)
            R.id.medicineWriteButton -> openEditorActivity(3)
        }
    }

    private fun openEditorActivity(optionKey: Int) {
        var intent: Intent?

        when (optionKey) {
            0 -> {
                intent = Intent(this, CCWrite::class.java)
                intent.putExtra(Util.CC_LIST, ccList)
            }
            1 -> {
                intent = Intent(this, OEWrite::class.java)
                intent.putExtra(Util.OE_LIST, oeList)
            }
            2 -> {
                intent = Intent(this, AdviceWrite::class.java)
                intent.putExtra(Util.ADVICE_LIST, adviceList)
            }
            else -> {
                intent = Intent(this, MedicineWrite::class.java)
                intent.putExtra(Util.MEDICINE_LIST, medicineList)
            }
        }

        Log.d(TAG, "$optionKey")
        startActivityForResult(
            intent,
            Util.WRITE_PRESCRIPTION_REQUEST_CODE
        )
        overridePendingTransition(R.anim.left_to_right, R.anim.righttoleft)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

         if (requestCode == Util.WRITE_PRESCRIPTION_REQUEST_CODE
            && resultCode == Util.RESULT_OE && resultCode != Activity.RESULT_CANCELED) {

            oeList.clear()
            oeList.addAll(
                data
                    ?.extras
                    ?.get(Util.OE_LIST) as Collection<String>
            )

            setOEList()

        } else if (requestCode == Util.WRITE_PRESCRIPTION_REQUEST_CODE
            && resultCode == Util.RESULT_CC
        ) {
             try {
                 val tempValue = data?.extras?.get(Util.CC_LIST) as Collection<String>
                 Log.d(TAG, tempValue.size.toString())

                 ccList.clear()


                 ccList.addAll(
                     data
                         ?.extras
                         ?.get(Util.CC_LIST) as Collection<String>
                 )

                 Log.d(TAG, ccList.size.toString())
                 setCCList()
             } catch (e: Exception) {
                 Log.d(TAG, e.message)
             }



        } else if (requestCode == Util.WRITE_PRESCRIPTION_REQUEST_CODE
            && resultCode == Util.RESULT_ADVICE && resultCode != Activity.RESULT_CANCELED) {

            adviceList.clear()

            adviceList.addAll(
                data
                    ?.extras
                    ?.get(Util.ADVICE_LIST) as Collection<String>
            )

            Log.d(TAG, adviceList.size.toString())
            setAdviceList()

        } else if (requestCode == Util.WRITE_PRESCRIPTION_REQUEST_CODE
            && resultCode == Util.RESULT_MEDICINE && resultCode != Activity.RESULT_CANCELED) {
            medicineList.clear()
            medicineList.addAll(
                data
                    ?.extras
                    ?.get(Util.MEDICINE_LIST) as Collection<MedicineUtils>
            )
            Log.d(TAG, "Medicine: " + medicineList[0].medicineName)

            setMedicineList()
        }
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
        overridePendingTransition(R.anim.top_left_corner_to_position, R.anim.fadeout)
    }


    private fun stopProgress() {
        binding.progress.visibility = View.GONE
    }

    private fun runProgess() {

        val animation = AnimationUtils.loadAnimation(this, R.anim.heart_beat)
        binding.progressHeart.startAnimation(animation)

        binding.progress.visibility = View.VISIBLE
    }


    // Fetching doctor information in background
    private inner class FetchDoctorInformation: AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            doctorEmail = firebaseAuth.currentUser?.email!!
            doctorId = doctorEmail?.split("@")?.get(0)

            Log.d(TAG, doctorEmail)
            if (doctorEmail != null) {
                firestore
                    .collection(Util.USER_CATEGORY_DATABASE)
                    .document(doctorEmail)
                    .get()
                    .addOnSuccessListener {
                            if (it != null)
                                doctorUtils = it.toObject(UserCategoryUtils::class.java)!!

                        Log.d(TAG, "user: $doctorUtils")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, it.message)
                    }
                firestore
                    .collection(Util.PATIENT_PRESCRIPTION_DATABASE)
                    .document(patientId)
                    .collection(Util.DOCTOR_LIST_DATABASE)
                    .document(doctorId!!)
                    .get()
                    .addOnSuccessListener {
                        isDoctorPrescribedBefore = it != null
                        Log.d(TAG, isDoctorPrescribedBefore.toString())
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Failed Doctor: ${it.message}")
                    }
            }
            return null
        }

    }

    private inner class SaveMedicinesAndDoctorInformation: AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {

            medicineList.forEach {
                val medicineName = it.medicineName.trim()
                val medicine = Pair("medicineName", medicineName)

                // Adding medicine to the patient list
                firestore
                    .collection(Util.PATIENT_PRESCRIPTION_DATABASE)
                    .document(patientId)
                    .collection(Util.MEDICINE_LIST_DATABASE)
                    .document(medicineName)
                    .set(medicine)
                    .addOnSuccessListener {name ->
                        Log.d(TAG, "Medicine name: ${it.medicineName}")
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "Medicine failed: ${e.message}")
                    }
            }

            // Adding doctor information to patient prescription
            // if it is the first time of visiting
            if (!isDoctorPrescribedBefore) {
                firestore
                    .collection(Util.PATIENT_PRESCRIPTION_DATABASE)
                    .document(patientId)
                    .collection(Util.DOCTOR_LIST_DATABASE)
                    .document(doctorId)
                    .set(doctorUtils)
                    .addOnSuccessListener {
                        Log.d(TAG, "Doctor list success")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Doctor list Failure")
                    }
            }
            return  null
        }

    }

}
