package finalyear.project.patientinfobank.View.Doctor.Prescription

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.common.base.Strings.isNullOrEmpty
import finalyear.project.patientinfobank.Adapter.Prescription.MedicineAdapter
import finalyear.project.patientinfobank.Database.RoomDatabaseManager
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Prescription.MedicineUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.CommonInterfaces.ItemView
import finalyear.project.patientinfobank.databinding.ActivityWriteMedicineBinding

class MedicineWrite : AppCompatActivity(), ItemView {
    private val TAG = "MedicineWrite"
    private var medicineList = arrayListOf<MedicineUtils>()
    private val medicineTypes = arrayListOf<String>("Tab.", "Cap.", "Inj.", "Syrup.")

    private lateinit var selectedDayMonthYear: String

    private var dayMonthYear = arrayListOf<String>()

    private var medicineSugestionList = arrayListOf<String>()

    private lateinit var roomDatabaseManager: RoomDatabaseManager

    private lateinit var binding: ActivityWriteMedicineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_medicine)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_write_medicine)
        roomDatabaseManager = RoomDatabaseManager.getInstance(this)!!

        setUpToolbar()
        setMedicineTypeAdapter()

        setListeners()

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

    private fun setMedicineTypeAdapter() {
        val medicineTypeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            medicineTypes
        )
        binding.medicineType.setAdapter(medicineTypeAdapter)
    }

    private fun setMedicineNameAdapter() {

        val medicineSuggestionAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            medicineSugestionList
        )

        binding.medicineName.setAdapter(medicineSuggestionAdapter)
    }




    // Setting listener
    private fun setListeners() {
        binding.addButton.setOnClickListener {
            binding.medicineType.error = null
            binding.medicineName.error = null
            addMedicine()
        }

        dayMonthYear.addAll(resources.getStringArray(R.array.dayMonthYear))

        binding.periodDayMonthYear.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedDayMonthYear = dayMonthYear[position]
            }

        }

        // On medicine type selected setting medicine suggestion list
        binding.medicineType.setOnItemClickListener { parent, view, position, id ->
            try {
                medicineSugestionList = roomDatabaseManager
                    .getMedicineDAO()
                    .getAllMedicinesBasedOnTypes(medicineTypes[position]) as ArrayList<String>
                binding.medicineName.setText(Util.EMPTY_VALUE)
                setMedicineNameAdapter()
            }catch (e: Exception) {
                Log.d(TAG, "LocalDBFeatchingError: ${e.message}")
            }
        }
    }

    // Adding medicine to list
    private fun addMedicine() {
        var medicineType: String
        var medicineName: String
        var medicineTime: String
        var mealTime: String
        var medicinePeriod: String
        var comment: String

        if (checkValidity()) {

            medicineType = binding.medicineType.text.toString()
            medicineName = binding.medicineName.text.toString()

            // Trimming medicine name
            medicineName = medicineName.trim()



            if (medicineName.length > 1)
                medicineName = medicineName[0].toUpperCase() + medicineName.substring(1)

            medicineTime = if (binding.breakfastCheckbox.isChecked)
                "1"
            else
                "0"

            medicineTime += if (binding.lunchCheckbox.isChecked)
                " - 1"
            else
                " - 0"

            medicineTime += if (binding.dinnerCheckbox.isChecked)
                " - 1"
            else
                " - 0"

            mealTime = ""

            if (binding.beforeMeal.isChecked
                && binding.afterMeal.isChecked) {
                mealTime = "Before or after meal"
            } else {
                if (binding.beforeMeal.isChecked)
                    mealTime = "Before meal"

                if (binding.afterMeal.isChecked) {
                    mealTime = "After meal"
                }
            }
            Log.d(TAG, selectedDayMonthYear)

            if (binding.period.text.toString() == "1")
                medicinePeriod = binding.period.text.toString() + " " + selectedDayMonthYear
            else
                medicinePeriod = binding.period.text.toString() + " " + selectedDayMonthYear + "s"

            comment = ""

            if (!isNullOrEmpty(binding.comment.text.toString()))
                comment = binding.comment.text.toString()

            if (!isNullOrEmpty(comment) && comment.length > 1)
                comment = comment[0].toUpperCase() + comment.substring(1)

            medicineList.add(
                MedicineUtils(
                    medicineType,
                    medicineName,
                    medicineTime,
                    mealTime,
                    medicinePeriod,
                    comment
                )
            )

            binding.medicineType.setText("")
            binding.medicineName.setText("")
            binding.period.setText("")
            binding.comment.setText("")
            binding.beforeMeal.isChecked = false
            binding.afterMeal.isChecked = false
            binding.breakfastCheckbox.isChecked = false
            binding.lunchCheckbox.isChecked = false
            binding.dinnerCheckbox.isChecked = false

            setListView()
        }

    }

    // Setting medicines into list
    private fun setListView() {
        val adapter = MedicineAdapter(this, medicineList, true, this)
        binding.medicineList.adapter = adapter
    }

    // Checking all field have right value
    private fun checkValidity(): Boolean {

        if (isNullOrEmpty(binding.medicineType.text.toString())) {
            binding.medicineType.error = Util.EMPTY_ERROR_MESSAGE
            return false
        }

        if (isNullOrEmpty(binding.medicineName.text.toString())) {
            binding.medicineName.error = Util.EMPTY_ERROR_MESSAGE
            return false
        }
        if (isNullOrEmpty(binding.period.text.toString())) {
            binding.period.error = Util.EMPTY_ERROR_MESSAGE
            return false
        }

        if (!binding.breakfastCheckbox.isChecked
            && !binding.lunchCheckbox.isChecked
            && !binding.dinnerCheckbox.isChecked) {
            Toast.makeText(
                this,
                Util.MEDICINE_TIME_ERROR_MESSAGE,
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (!binding.afterMeal.isChecked
            && !binding.beforeMeal.isChecked) {
            Toast.makeText(
                this,
                Util.MEDICINE_MEAL_ERROR_MESSAGE,
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }


    // Setting menu bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        return super.onCreateOptionsMenu(menu)
    }


    // Sending full list to parent activity
    private fun sendResult() {

        SaveMedicinesToLocalDB().execute()

        var resultIntent = Intent()
        resultIntent.putExtra(Util.MEDICINE_LIST, medicineList)

        setResult(Util.RESULT_MEDICINE, resultIntent)
        finish()

        overridePendingTransition(R.anim.left_to_right, R.anim.righttoleft)
    }

    // Setting toolbar
    private fun setUpToolbar() {
        binding.toolbar.title = Util.MEDICINE_TITLE
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.done -> sendResult()
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(it)
        }
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
        Log.d(TAG, position.toString())
        medicineList.removeAt(position)
        setListView()
    }


    // Saving medicines to local database
    private inner class SaveMedicinesToLocalDB: AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {

            // Saving medicines into local database
            medicineList.forEach {
                try {
                    roomDatabaseManager
                        .getMedicineDAO()
                        .insert(it)
                } catch (e: Exception) {
                    Log.d(TAG, "LocalDBError: ${e.message}")
                }
            }

            return  null
        }
    }


}
