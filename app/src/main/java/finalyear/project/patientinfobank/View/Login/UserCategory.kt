package finalyear.project.patientinfobank.View.Login

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import finalyear.project.patientinfobank.Adapter.UserCategory.SpinnerAdapter
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Spinner.SpinnerCategoryUtils
import finalyear.project.patientinfobank.Utils.UserCategory.UserCategoryUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.Doctor.DoctorActivity
import finalyear.project.patientinfobank.View.Patient.PatientActivity
import finalyear.project.patientinfobank.databinding.ActivityUserCategoryBinding
import maes.tech.intentanim.CustomIntent
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class UserCategory : AppCompatActivity() {

    private val TAG: String = "UserCategory"
    private lateinit var binding: ActivityUserCategoryBinding
    private var categoryValues = arrayListOf<SpinnerCategoryUtils>()
    private var degreesList = arrayListOf<String>()
    private lateinit var spinnerAdapter: SpinnerAdapter
    private lateinit var animation: Animation
    private var isDoctor by Delegates.notNull<Boolean>()
    private var isInValidDate = true

    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_category)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_category)

        categoryValues.add(SpinnerCategoryUtils("Doctor", R.drawable.ic_doctor))
        categoryValues.add(SpinnerCategoryUtils("Patient", R.drawable.ic_patient))

        spinnerAdapter = SpinnerAdapter(this, categoryValues)
        binding.category.setAdapter(spinnerAdapter)

        getCurrentDate()

        setSpinnerOnItemSelectedListener()

        setPatientBirthDate()

        setDegreesListener()

        setFinalSubmissionListener()


    }

    // Setting up final submit button
    private fun setFinalSubmissionListener() {
        binding.userCategorySubmit.setOnClickListener {
            if (checkValidity()) {
                Log.d(TAG, "Information is correct:")
                val phoneNumber = binding.phoneNumber.text.toString()
                val patientBirthDate = binding.patientBirthDate.text.toString()
                if (isDoctor) {
                    val userCategoryUtils = UserCategoryUtils(isDoctor, phoneNumber, doctorDegreeList = degreesList)
                    Log.d(TAG, "Submit: $userCategoryUtils")
                    uploadToDatabase(userCategoryUtils)
                } else {
                    val userCategoryUtils = UserCategoryUtils(isDoctor, phoneNumber, patientBirthDate, ArrayList())
                    Log.d(TAG, "Submit: $userCategoryUtils")
                    uploadToDatabase(userCategoryUtils)
                }
            }
        }
    }

    // Uploading data to database
    private fun uploadToDatabase(userCategoryUtils: UserCategoryUtils) {

        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.heart_beat)
        binding.progressHeart.startAnimation(animation)
        binding.progress.visibility = View.VISIBLE


        val email = FirebaseAuth.getInstance().currentUser?.email
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        try {
            if (email != null) {
                database.collection(Util.USER_CATEGORY_DATABASE)
                    .document(email)
                    .set(userCategoryUtils)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            Util.REGISTRATION_SUCCESSFUL_MESSAGE,
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progress.visibility = View.GONE

                        val intent: Intent

                        if (isDoctor) {
                            intent = Intent(this, DoctorActivity::class.java)
                        } else {
                            intent = Intent(this, PatientActivity::class.java)
                        }
                        startActivity(intent)
                        CustomIntent.customType(this, "fadein-to-fadeout")
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            Util.REGISTRATION_FAILED_MESSAGE,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }
    }


    // Checking validity of the fields,
    // is all information is valid or not
    private fun checkValidity(): Boolean {
        val phoneNumber = binding.phoneNumber.text.toString()
        val patientBirthDate = binding.patientBirthDate.text.toString()

        if (phoneNumber == null ||
            phoneNumber == Util.EMPTY_VALUE) {
            binding.phoneNumber.requestFocus()
            binding.phoneNumber.error = Util.EMPTY_ERROR_MESSAGE
            return false
        }

        // Checking the phone number is valid or not
        val regex = "^01".toRegex()

        if (phoneNumber.length < 11 ||
                !regex.containsMatchIn(phoneNumber)) {
            binding.phoneNumber.requestFocus()
            binding.phoneNumber.error = Util.INVALID_PHONE_NUMBER_ERROR_MESSAGE
            return false
        }

        if (isDoctor && degreesList.size == 0) {
            binding.doctorDegree.error = Util.NO_DEGREE_ADDED_ERROR_MESSAGE
            binding.doctorDegree.requestFocus()
            return false
        }
        Log.d(TAG, "Validity: $patientBirthDate")
        if(!isDoctor &&
            patientBirthDate == "dd/mm/yyyy") {
            binding.patientBirthDate.error = Util.INVALID_BIRTH_DATE_ERROR_MESSAGE
            return false
        }
        if ( !isDoctor && isInValidDate) {
            binding.patientBirthDate.error = Util.INVALID_BIRTH_DATE_ERROR_MESSAGE
            return false
        }
        

        return true
    }

    private fun setSpinnerOnItemSelectedListener() {
        binding.category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d(TAG, "OnItemSelected: $position")

                animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
                if (position == 0) {

                    isDoctor = true
                    // Animation added on visibility
                    binding.doctorDegreeLayout.animation = animation
                    binding.degreesListLayout.animation = animation

                    binding.patientBirthDateLayout.visibility = View.GONE
                    binding.doctorDegreeLayout.visibility = View.VISIBLE
                    binding.degreesListLayout.visibility = View.VISIBLE
                } else {
                    isDoctor = false
                    // Animation adding on visibility
                    binding.patientBirthDateLayout.animation = animation

                    binding.patientBirthDateLayout.visibility = View.VISIBLE
                    binding.doctorDegreeLayout.visibility = View.GONE
                    binding.degreesListLayout.visibility = View.GONE
                }
            }

        }

    }

    // adding degree on click degreeAddButton
    private fun setDegreesListener() {
        Log.d(TAG, "DegreeList: ${degreesList.size}")

        if (degreesList.size == 0) {
            Log.d(TAG, "DegreeList:")
            binding.degreesList.visibility = View.GONE
            binding.degreesListHint.visibility = View.GONE
            binding.degreesListUndo.visibility = View.GONE
        }

        binding.addDegreeButton.setOnClickListener {
            binding.doctorDegree.error = null
            if (binding.doctorDegree.text.toString() == null
                || binding.doctorDegree.text.toString() == "") {
                binding.doctorDegree.error = Util.EMPTY_ERROR_MESSAGE
            } else {

                if (binding.degreesList.visibility == View.GONE) {
                    animation = AnimationUtils.loadAnimation(this, R.anim.bottomtotop)
                    binding.degreesList.startAnimation(animation)
                    binding.degreesListHint.startAnimation(animation)
                    binding.degreesListUndo.startAnimation(animation)
                    binding.degreesList.visibility = View.VISIBLE
                    binding.degreesListHint.visibility = View.VISIBLE
                    binding.degreesListUndo.visibility = View.VISIBLE
                }
                animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
                binding.doctorDegree.startAnimation(animation)

                val degree = binding.doctorDegree.text.toString()
                degreesList.add(degree)
                binding.doctorDegree.setText(Util.EMPTY_VALUE)
                setDegreeList()
            }
        }

        binding.degreesListUndo.setOnClickListener {
            degreesList.removeAt(degreesList.size - 1)
            if (degreesList.size == 0) {
                Log.d(TAG, "DegreeList:")
                animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
                binding.degreesList.startAnimation(animation)
                binding.degreesListHint.startAnimation(animation)
                binding.degreesListUndo.startAnimation(animation)
                binding.degreesList.visibility = View.GONE
                binding.degreesListHint.visibility = View.GONE
                binding.degreesListUndo.visibility = View.GONE
            }

            setDegreeList()
        }
    }

    // Setting degrees list in listview
    private fun setDegreeList() {
        val arrayAdapter = ArrayAdapter(this, R.layout.view_degrees_list, R.id.degreeViewId, degreesList)
        binding.degreesList.adapter = arrayAdapter
    }

    private fun getCurrentDate() {
        val calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
    }

    // Setting uppatient birthdate on click and focus
    private fun setPatientBirthDate() {
        binding.patientBirthDate.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                openDatePicker()
            }
        }
        binding.patientBirthDate.setOnClickListener {
            openDatePicker()
        }
    }

    // Opening datepicker to pick the patient birth date
    private fun openDatePicker() {

        binding.patientBirthDate.error = null
        Log.d(TAG, "DatePicker: ")

        val datePickerDialog: DatePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                this.day = day
                this.month = month
                this.year = year

                val date = "%02d/%02d/%d".format(day, month, year)
                binding.patientBirthDate.setText(date)
                var calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, day)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.YEAR, year)
                if (calendar.timeInMillis >
                        Calendar.getInstance().timeInMillis) {
                    isInValidDate = true
                    binding.patientBirthDate.error = Util.INVALID_BIRTH_DATE_ERROR_MESSAGE
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

}
