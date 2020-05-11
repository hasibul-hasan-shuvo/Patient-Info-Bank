package finalyear.project.patientinfobank.View.Patient.Home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import finalyear.project.patientinfobank.Adapter.PatientHome.DoctorListSliderAdapter
import finalyear.project.patientinfobank.Adapter.PatientHome.PrescriptionSliderAdapter

import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Prescription.PrescriptionUtils
import finalyear.project.patientinfobank.Utils.UserCategory.UserCategoryUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.databinding.FragmentPatientHomeBinding
import java.util.*
import kotlin.math.abs


class PatientHome : Fragment() {

    private val TAG = "PatientHome"

    private lateinit var binding: FragmentPatientHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var prescriptionList = arrayListOf<PrescriptionUtils>()
    private var doctorsList = arrayListOf<UserCategoryUtils>()

    private lateinit var prescriptionSliderAdapter: PrescriptionSliderAdapter
    private lateinit var doctorListSliderAdapter: DoctorListSliderAdapter
    private lateinit var patientId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPatientHomeBinding.inflate(inflater, container, false)


        runProgess()
        setUpToolbar()
        setUpDatabase()

        fetchPrescriptionList()
        fetchDoctorList()

        return binding.root
    }

    private fun fetchDoctorList() {

        doctorsList.clear()
        firestore
            .collection(Util.PATIENT_PRESCRIPTION_DATABASE)
            .document(patientId)
            .collection(Util.DOCTOR_LIST_DATABASE)
            .get()
            .addOnSuccessListener {
                it?.forEach { document->
                    Log.d(TAG, document.data.toString())
                    doctorsList.add(document.toObject(UserCategoryUtils ::class.java))
                }
                setUpDoctorsList()
                stopProgress()
            }
            .addOnFailureListener {
                Log.d(TAG, "FailedDoctorList: ${it.message}")
                stopProgress()
            }
    }


    private fun setUpDoctorsList() {

        doctorsList.reverse()
        doctorListSliderAdapter =  DoctorListSliderAdapter(context!!, doctorsList)
        binding.doctorsList.adapter = doctorListSliderAdapter
        binding.doctorsList.clipToPadding = false
        binding.doctorsList.clipChildren = false
        binding.doctorsList.offscreenPageLimit = 3
        binding.doctorsList.getChildAt(0)
            .overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        binding.doctorsList.setPadding(135, 0, 135, 0)

        var compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        binding.doctorsList.setPageTransformer(compositePageTransformer)
    }

    private fun fetchPrescriptionList() {
        prescriptionList.clear()
        Log.d(TAG, patientId)
        firestore
            .collection(Util.PATIENT_PRESCRIPTION_DATABASE)
            .document(patientId)
            .collection(Util.PRESCRIPTION_DATABASE)
            .get()
            .addOnSuccessListener {
                it?.forEach { document->
                    Log.d(TAG, document.data.toString())
                    prescriptionList.add(document.toObject(PrescriptionUtils:: class.java))
                }

                setUpPrescriptionList()
                Log.d(TAG, prescriptionList.toString())
            }
            .addOnFailureListener {
                Log.d(TAG, "FailedPrescriptionList: ${it.message}")
                stopProgress()
            }
    }

    private fun setUpPrescriptionList() {
        prescriptionList.sort()
        prescriptionSliderAdapter =  PrescriptionSliderAdapter(context!!, prescriptionList)
        binding.prescriptionList.adapter = prescriptionSliderAdapter
        binding.prescriptionList.clipToPadding = false
        binding.prescriptionList.clipChildren = false
        binding.prescriptionList.offscreenPageLimit = 3
        binding.prescriptionList.getChildAt(0)
            .overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        binding.prescriptionList.setPadding(135, 0, 135, 0)

        var compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        binding.prescriptionList.setPageTransformer(compositePageTransformer)
    }


    private fun setUpDatabase() {
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        patientId = firebaseAuth.currentUser?.email.toString().split("@")[0]
    }


    /** Notification section starts **/

    // Creating option menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_patient_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    private fun setUpToolbar() {

        binding.toolbar.title = Util.PATIENT_HOME_TITLE
        binding.toolbar.setTitleTextColor(Color.WHITE)
        (activity as AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.notifications -> openNotifications()
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(it)
        }
    }

    private fun openNotifications() {
        Log.d(TAG, "Notifications")
    }

    /** notifications section ends **/



    /** ProgressBar implementation start **/
    private fun stopProgress() {
        binding.progress.visibility = View.GONE
    }

    private fun runProgess() {

        val animation = AnimationUtils.loadAnimation(context, R.anim.heart_beat)
        binding.progressHeart.startAnimation(animation)

        binding.progress.visibility = View.VISIBLE
    }

    /** ProgressBar implementation end **/
}
