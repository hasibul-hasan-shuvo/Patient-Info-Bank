package finalyear.project.patientinfobank.View.Doctor.Prescription

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Services.InternetConnection
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.databinding.ActivityPatientPrescriptionBinding

class PatientPrescription : AppCompatActivity() {
    private val TAG = "PatientPrescription"

    private lateinit var patientId: String

    private lateinit var binding: ActivityPatientPrescriptionBinding
    private lateinit var firestore: FirebaseFirestore
    private var medicineList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_prescription)
        window?.enterTransition = null
        window?.exitTransition = null

        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_prescription)

        firestore = FirebaseFirestore.getInstance()

        setSearchId()

        setUpToolbar()

        setUpListener()

        if (checkConnection()) {
            fetchUserCategory()
        }
    }


    // Checking internet connection
    fun checkConnection(): Boolean {

        if (InternetConnection.checkConnection(this)) {
            return true
        }

        binding.searchButton.snack(Util.NO_INTERNET_CONNECTION)

        return false
    }

    // Snackbar view setting
    fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        val snackbar = Snackbar.make(
            this,
            Util.NO_INTERNET_CONNECTION,
            Snackbar.LENGTH_SHORT
        )
        var snackbarView = snackbar.view
        snackbarView.setBackgroundColor(resources.getColor(R.color.doctorHomeColor))
        snackbar.show()
    }

    private fun setSearchId() {

        patientId = intent.getStringExtra(Util.SEARCH_PATIENT_ID)
        binding.searchId.setText(patientId)
        binding.searchId.setSelection(patientId.length)
        binding.searchId.clearFocus()

        // Hiding keyboard
        if (binding.searchId.isFocused) {
            var imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(
                binding.searchId.windowToken,
                0
            )
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

    }


    // Finding patient id
    private fun fetchUserCategory() {
        firestore
            .collection(Util.USER_CATEGORY_DATABASE)
            .document("$patientId@gmail.com")
            .get()
            .addOnSuccessListener {
                Log.d(TAG, it.toString())
                if (it.data == null) {

                    binding.medicineBeforeTakenTitleLayout.visibility = View.GONE
                    binding.medicinesList.visibility = View.GONE
                    binding.writePrescription.visibility = View.GONE
                    binding.patientIdNotFound.visibility = View.VISIBLE
                } else {
                    binding.medicineBeforeTakenTitleLayout.visibility = View.VISIBLE
                    binding.medicinesList.visibility = View.VISIBLE
                    binding.writePrescription.visibility = View.VISIBLE
                    binding.patientIdNotFound.visibility = View.GONE
                    fetchMedicineList()
                }
            }
            .addOnFailureListener {
                Log.d(TAG, it.message)
            }
    }

    // Fetching medicine list
    private fun fetchMedicineList() {
        medicineList.clear()
        binding.medicineLoadingProgress.visibility = View.VISIBLE
        firestore
            .collection(Util.PATIENT_PRESCRIPTION_DATABASE)
            .document(patientId)
            .collection(Util.MEDICINE_LIST_DATABASE)
            .addSnapshotListener{value, e ->
                if (e != null) {
                    Log.d(TAG, e.message)
                    binding.medicineLoadingProgress.visibility = View.GONE
                    return@addSnapshotListener
                } else {
                    for (doc in value!!.documentChanges) {
                        when(doc.type) {
                            DocumentChange.Type.ADDED -> {
                                medicineList.add(doc.document.getString(Util.MEDICINE_NAME)!!)
                            }
                        }
                    }
                    Log.d(TAG, medicineList.toString())
                    binding.medicineLoadingProgress.visibility = View.GONE
                    setMedicineList()
                }
            }
    }

    private fun setMedicineList() {
        val arrayAdapter = ArrayAdapter(
            this,
            R.layout.view_doctor_medicine_list,
            R.id.medicineName,
            medicineList
        )
        binding.medicinesList.adapter = arrayAdapter
    }

    // Setting write prescription listener
    private fun setUpListener() {
        binding.writePrescription.setOnClickListener {
            val intent = Intent(this, PatientPrescriptionWrite::class.java)
            intent.putExtra(Util.SEARCH_PATIENT_ID, patientId)
            startActivity(intent)
            overridePendingTransition(R.anim.bottom_right_corner_to_position, R.anim.bottom_right_corner_to_position)
        }
        binding.searchButton.setOnClickListener {
            patientId = binding.searchId.text.toString()
            if (checkConnection()) {
                fetchUserCategory()
            }
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
