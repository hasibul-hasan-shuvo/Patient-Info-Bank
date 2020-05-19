package finalyear.project.patientinfobank.View.Patient.Blood

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import finalyear.project.patientinfobank.Adapter.FindDonor.FindDonorAdapter
import finalyear.project.patientinfobank.Adapter.SpinnerAdapter.BeDonorSpinnerAdapter
import finalyear.project.patientinfobank.Adapter.SpinnerAdapter.FindDonorSpinnerAdapter
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.BloodDonor.BloodDonorInformationUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.databinding.ActivityFindDonorBinding
import kotlinx.android.synthetic.main.view_find_donor_dialog.view.*

class FindDonor : AppCompatActivity() {

    private val TAG = "FindDonor"

    private lateinit var binding: ActivityFindDonorBinding
    private var donorInformationList = arrayListOf<BloodDonorInformationUtils>()
    private lateinit var findDonorAdapter: FindDonorAdapter
    private lateinit var alertDialog: AlertDialog

    private var bloodGroup: String? = null
    private var division: String? = null
    private var district: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_donor)

        window.enterTransition = null
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_donor)

        createDialog()
        alertDialog.show()
        setUpToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_find_a_donor, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setUpToolbar() {

        binding.toolbar.title = Util.FIND_A_DONOR
        binding.toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.refresh -> refreshActivity()
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(it)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun refreshActivity() {
        Log.d(TAG, "Refresh")
        binding.apply {
            invalidateAll()
            alertDialog.show()
            donorInformationList.clear()
            if (emptyListMessage.visibility == View.VISIBLE)
                emptyListMessage.visibility = View.GONE
            setRecyclerView()
        }
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

    private fun setRecyclerView() {
        findDonorAdapter = FindDonorAdapter(this, donorInformationList)
        binding.recyclerView.adapter = findDonorAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setItemViewCacheSize(1000)
    }

    private fun fetchData() {
        runProgress()
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        database
            .collection(Util.BLOOD_DONOR_DATABASE)
            .document(district!!)
            .collection(bloodGroup!!)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSize: ${it.documents.size}")
                if (it.documents.size > 0) {
                    it.documents.forEach { documentSnapshot ->
                        val information =
                            documentSnapshot?.toObject(BloodDonorInformationUtils::class.java)
                        Log.d(TAG, "Data: $information")
                        donorInformationList.add(information!!)
                        emptyChecker()
                        stopProgress()
                    }
                } else {
                    emptyChecker()
                    stopProgress()
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed data loading: ${it.message}")
                stopProgress()
            }
    }



    // Opening alert dialog to get division, district and blood group
    private fun createDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater
            .from(this)
            .inflate(R.layout.view_find_donor_dialog, null)

        setDivisionSpinner(dialogView)
        setBloodGroupSpinner(dialogView)

        setUpDialogView(dialogView)

        builder.setView(dialogView)

        alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog
            .window
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        alertDialog.show()
    }

    // Setting up dialog blood group spinner
    private fun setBloodGroupSpinner(dialogView: View?) {
        val bloodGroupsList = resources.getStringArray(R.array.bloodGroup)

        val spinnerAdapter = FindDonorSpinnerAdapter(this, bloodGroupsList)

        dialogView?.bloodGroup?.adapter = spinnerAdapter

        dialogView?.bloodGroup?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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

    // Setting up dialog division spinner
    private fun setDivisionSpinner(dialogView: View?) {
        val divisionsList = resources.getStringArray(R.array.divisions)

        val spinnerAdapter = FindDonorSpinnerAdapter(this, divisionsList)

        dialogView?.division?.adapter = spinnerAdapter

        dialogView?.division?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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

                setDistrictSpinner(
                    dialogView,
                    resources.getIdentifier(
                        division,
                        "array",
                        packageName
                    )
                )
            }
        }
    }

    // Setting up dialog district spinner
    private fun setDistrictSpinner(dialogView: View?, identifier: Int) {
        val districtList = resources.getStringArray(identifier)

        val spinnerAdapter = FindDonorSpinnerAdapter(this, districtList)

        dialogView?.district?.adapter = spinnerAdapter

        dialogView?.district?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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

    private fun setUpDialogView(dialogView: View?) {
        dialogView?.search?.setOnClickListener {
            fetchData()
            alertDialog.dismiss()
        }
        dialogView?.cancel?.setOnClickListener {
            onBackPressed()
        }
    }

    // Checking the list is empty or not
    private fun emptyChecker() {
        if (donorInformationList.size == 0)
            binding.emptyListMessage.visibility = View.VISIBLE
        else {
            binding.emptyListMessage.visibility = View.GONE
            setRecyclerView()
        }
    }

    private fun stopProgress() {
        binding.progress.visibility = View.GONE
    }

    private fun runProgress() {

        val animation = AnimationUtils.loadAnimation(this, R.anim.heart_beat)
        binding.progressHeart.startAnimation(animation)
        binding.progress.visibility = View.VISIBLE
    }
}
