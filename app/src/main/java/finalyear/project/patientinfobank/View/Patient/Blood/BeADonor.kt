package finalyear.project.patientinfobank.View.Patient.Blood

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.common.base.Strings.isNullOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import finalyear.project.patientinfobank.Adapter.SpinnerAdapter.BeDonorSpinnerAdapter
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.BloodDonor.BloodDonorInformationUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.Utils.ValidityChecker
import finalyear.project.patientinfobank.databinding.ActivityBeADonorBinding
import java.util.*

class BeADonor : AppCompatActivity() {

    private val TAG = "BeADonar"

    private lateinit var binding: ActivityBeADonorBinding
    private var bloodGroup: String? = null
    private var division: String? = null
    private var district: String? = null

    private var isInValidDate = true

    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0
    private lateinit var fullname: String
    private lateinit var address: String
    private lateinit var phoneNumber: String
    private lateinit var email: String
    private lateinit var birthDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_be_a_donor)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_be_a_donor)

        setUpToolbar()
        setDivisionSpinner()
        setBloodGroupSpinner()
        setBirthDate()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_be_a_donor, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setUpToolbar() {

        binding.toolbar.title = Util.BE_A_DONOR
        binding.toolbar.setTitleTextColor(Color.WHITE)
        binding.toolbar.setNavigationIcon(R.drawable.ic_close)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.done -> saveDonorInformation()
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(it)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d(TAG, "Back pressed")
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft)
    }

    // Setting division spinner
    private fun setDivisionSpinner() {
        val divisionsList = resources.getStringArray(R.array.divisions)

        val spinnerAdapter = BeDonorSpinnerAdapter(this, divisionsList)

        binding.division.adapter = spinnerAdapter

        binding.division.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                division = divisionsList[position]

                Log.d(TAG, "Division: $division")

                setDistrictSpinner(resources.getIdentifier(
                    division,
                    "array",
                    packageName
                ))
            }
        }
    }

    // Setting district spinner
    private fun setDistrictSpinner(identifier: Int) {
        val districtList = resources.getStringArray(identifier)

        val spinnerAdapter = BeDonorSpinnerAdapter(this, districtList)

        binding.district.adapter = spinnerAdapter

        binding.district.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                district = districtList[position]

                Log.d(TAG, "District: $district")
            }

        }
    }

    // Setting blood group spinner
    private fun setBloodGroupSpinner() {
        val bloodGroupsList = resources.getStringArray(R.array.bloodGroup)

        val spinnerAdapter = BeDonorSpinnerAdapter(this, bloodGroupsList)

        binding.bloodGroup.adapter = spinnerAdapter

        binding.bloodGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                bloodGroup = bloodGroupsList[position]

                Log.d(TAG, "BloodGroup: $bloodGroup")
            }

        }
    }

    // Method to get current date
    private fun getCurrentDate() {
        val calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
    }

    // Setting up patient birth date on click and focus
    private fun setBirthDate() {
        getCurrentDate()
        binding.birthDate.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                openDatePicker()
            }
        }
        binding.birthDate.setOnClickListener {
            openDatePicker()
        }
    }

    // Opening datepicker to pick the patient birth date
    private fun openDatePicker() {

        binding.birthDate.error = null
        Log.d(TAG, "DatePicker: ")

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                this.day = day
                this.month = month
                this.year = year

                val date = "%02d/%02d/%d".format(day, month+1, year)
                binding.birthDate.setText(date)
                var calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, day)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.YEAR, year)
                if (calendar.timeInMillis >
                    Calendar.getInstance().timeInMillis) {
                    isInValidDate = true
                    binding.birthDate.error = Util.INVALID_BIRTH_DATE_ERROR_MESSAGE
                } else {
                    isInValidDate = false
                }
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }


    // Saving information into database
    private fun saveDonorInformation() {
        Log.d(TAG, "Save")

        if (isValidData()) {
            runProgess()

            val bloodDonorInformationUtils = BloodDonorInformationUtils(
                fullname,
                division!!,
                district!!,
                address,
                phoneNumber,
                email,
                birthDate,
                bloodGroup!!
            )

            val database: FirebaseFirestore = FirebaseFirestore.getInstance()

            database
                .collection(Util.BLOOD_DONOR_DATABASE)
                .document(district!!)
                .collection(bloodGroup!!)
                .document(phoneNumber)
                .set(bloodDonorInformationUtils)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        Util.REGISTRATION_SUCCESSFUL_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()
                    stopProgress()
                    finish()
                    overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        Util.REGISTRATION_FAILED_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()
                    stopProgress()
                }
        }
    }


    // Checking input data validity
    fun isValidData(): Boolean {
        fullname = binding.fullName.text.toString()
        address = binding.address.text.toString()
        phoneNumber = binding.phoneNumber.text.toString()
        email = binding.email.text.toString()
        birthDate = binding.birthDate.text.toString()

        if (isNullOrEmpty(fullname)) {
            binding.fullName.requestFocus()
            binding.fullName.error = Util.EMPTY_ERROR_MESSAGE
            return false
        } else if (fullname.length < 3) {
            binding.fullName.requestFocus()
            binding.fullName.error = Util.LENGTH_LESS_THAN_3_ERROR_MESSAGE
            binding.fullName.setSelection(fullname.length)
            return false
        }

        if (isNullOrEmpty(address)) {
            binding.address.requestFocus()
            binding.address.error = Util.EMPTY_ERROR_MESSAGE
            return false
        }

        if (isNullOrEmpty(phoneNumber)) {
            binding.phoneNumber.requestFocus()
            binding.phoneNumber.error = Util.EMPTY_ERROR_MESSAGE
            return false
        }

        // Checking the phone number is valid or not

        val validityChecker = ValidityChecker()
        if (!validityChecker.isValidPhoneNumber(phoneNumber)) {
            binding.phoneNumber.requestFocus()
            binding.phoneNumber.error = Util.INVALID_PHONE_NUMBER_ERROR_MESSAGE
            binding.phoneNumber.setSelection(phoneNumber.length)
            return false
        }

        if (!validityChecker.isValidEmail(email)) {
            binding.email.requestFocus()
            binding.email.error = Util.INVALID_EMAIL_ERROR_MESSAGE
            binding.email.setSelection(email.length)
            return false
        }

        if(isInValidDate) {
            binding.birthDate.requestFocus()
            binding.birthDate.error = Util.INVALID_BIRTH_DATE_ERROR_MESSAGE
            return false
        }

        return true
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
