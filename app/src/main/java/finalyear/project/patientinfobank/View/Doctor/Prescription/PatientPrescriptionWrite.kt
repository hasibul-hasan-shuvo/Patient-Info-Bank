package finalyear.project.patientinfobank.View.Doctor.Prescription

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
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
import android.util.Pair
import android.widget.ArrayAdapter
import android.widget.ListView
import java.util.*

class PatientPrescriptionWrite : AppCompatActivity() , View.OnClickListener{
    private val TAG = "PatientPrescription"

    private lateinit var binding: ActivityPatientPrescriptionWriteBinding
    private lateinit var patientInfo: UserCategoryUtils
    private lateinit var patientId: String
    private var writeVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_prescription_write)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_prescription_write)


        setUpToolbar()
        setUpListeners()
    }

    override fun onResume() {
        super.onResume()
//        patientId = intent.getStringExtra(Util.SEARCH_PATIENT_ID)
//        fetchPatientInfo()
        val values = arrayListOf<String>()
        values.add("sfdlkj")
        values.add("sfdlkj")
        values.add("sfdlkj")
        values.add("sfdlkj")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, values)
        binding.ccList.adapter = adapter
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
        menuInflater.inflate(R.menu.menu_be_a_donor, menu)
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
        //
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
        var intent: Intent? = null

        when (optionKey) {
            0 -> {
                intent = Intent(this, CCWrite::class.java)
            }
            1 -> {
                intent = Intent(this, OEWrite::class.java)
            }
            2 -> {
                intent = Intent(this, AdviceWrite::class.java)
            }
            else -> {
                intent = Intent(this, MedicineWrite::class.java)
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
            && resultCode == Activity.RESULT_OK) {
            //
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

}
