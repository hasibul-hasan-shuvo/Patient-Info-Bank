package finalyear.project.patientinfobank.View.Patient.Blood

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import finalyear.project.patientinfobank.Adapter.SpinnerAdapter.SpinnerAdapter
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Util
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
        overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft)
        return true
    }

    private fun setDivisionSpinner() {
        val divisionsList = resources.getStringArray(R.array.divisions)

        val spinnerAdapter = SpinnerAdapter(this, divisionsList)

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

    private fun setDistrictSpinner(identifier: Int) {
        val districtList = resources.getStringArray(identifier)

        val spinnerAdapter = SpinnerAdapter(this, districtList)

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

    private fun setBloodGroupSpinner() {
        val bloodGroupsList = resources.getStringArray(R.array.bloodGroup)

        val spinnerAdapter = SpinnerAdapter(this, bloodGroupsList)

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

                val date = "%02d/%02d/%d".format(day, month, year)
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
    
    private fun saveDonorInformation() {
        Log.d(TAG, "Save")
    }


}
