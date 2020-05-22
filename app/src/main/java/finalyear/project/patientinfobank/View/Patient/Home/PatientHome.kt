package finalyear.project.patientinfobank.View.Patient.Home

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import finalyear.project.patientinfobank.Adapter.PatientHome.DoctorListSliderAdapter
import finalyear.project.patientinfobank.Adapter.PatientHome.PrescriptionSliderAdapter

import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Prescription.PrescriptionUtils
import finalyear.project.patientinfobank.Utils.UserCategory.UserCategoryUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.CommonInterfaces.ItemView
import finalyear.project.patientinfobank.View.Patient.Notification.NotificationList
import finalyear.project.patientinfobank.databinding.FragmentPatientHomeBinding
import kotlin.math.abs


class PatientHome : Fragment(), ItemView{

    private val TAG = "PatientHome"

    private lateinit var binding: FragmentPatientHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var prescriptionList = arrayListOf<PrescriptionUtils>()
    private var doctorsList = arrayListOf<UserCategoryUtils>()

    private lateinit var prescriptionSliderAdapter: PrescriptionSliderAdapter
    private lateinit var doctorListSliderAdapter: DoctorListSliderAdapter
    private lateinit var patientId: String

    private lateinit var textView: TextView


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

        setUpToolbar()
        setUpDatabase()
        fetchPrescriptionList()
        fetchDoctorList()


        return binding.root
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

    private fun setUpPrescriptionList() {
        prescriptionList.sort()
        prescriptionSliderAdapter =  PrescriptionSliderAdapter(context!!, prescriptionList, this)
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

    private fun fetchPrescriptionList() {
        runPrescriptionProgressBar()
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
                stopPrescriptionProgressBar()
                Log.d(TAG, prescriptionList.toString())
            }
            .addOnFailureListener {
                Log.d(TAG, "FailedPrescriptionList: ${it.message}")
            }

    }
    private fun fetchDoctorList() {

        runDoctorProgressBar()
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
                stopDoctorProgressBar()
            }
            .addOnFailureListener {
                Log.d(TAG, "FailedDoctorList: ${it.message}")
            }

    }


    private fun runPrescriptionProgressBar() {

        binding.prescriptionProgressBar.visibility = View.VISIBLE
    }
    private fun stopPrescriptionProgressBar() {
        binding.prescriptionProgressBar.visibility = View.GONE
    }
    private fun runDoctorProgressBar() {
        binding.doctorProgressBar.visibility = View.VISIBLE
    }
    private fun stopDoctorProgressBar() {
        binding.doctorProgressBar.visibility = View.GONE
    }

    private fun setUpDatabase() {
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        patientId = firebaseAuth.currentUser?.email.toString().split("@")[0]
    }


    /** notifications section starts **/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_patient_home, menu)

        val menuItem = menu.findItem(R.id.notifications)

        val actionView = menuItem.actionView
        textView = actionView.findViewById(R.id.badge)
        actionView.setOnClickListener {
            openNotifications()
        }

        Log.d(TAG, "OptionMenu")
        super.onCreateOptionsMenu(menu, inflater)
    }
    private fun setUpToolbar() {

        binding.toolbar.title = Util.PATIENT_HOME_TITLE
        binding.toolbar.setTitleTextColor(Color.WHITE)
        binding.toolbar.inflateMenu(R.menu.menu_patient_home)
        val menu = binding.toolbar.menu

        val menuItem = menu.findItem(R.id.notifications)

        val actionView = menuItem.actionView

        actionView.setOnClickListener {
            textView = actionView.findViewById(R.id.badge)
            openNotifications()
        }

        (activity as AppCompatActivity)?.setSupportActionBar(binding.toolbar)
    }

    private fun openNotifications() {
        textView.visibility = View.GONE
        Log.d(TAG, "Notifications")

        val intent = Intent(context, NotificationList::class.java)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.left_to_right, R.anim.righttoleft)
    }

    /** notifications section ends **/




    override fun onItemClick(position: Int) {
        Log.d(TAG, "Clicked: ${prescriptionList[position]}")
        var builder = AlertDialog.Builder(context!!)

        builder.setTitle(Util.DELETE_MESSAGE)

        val alertDialog = builder.create()

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(Color.RED)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(Color.GREEN)
        }

        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            Util.YES
        ) { dialog, which ->
            Log.d(TAG, "Yes")
            deletePrescription(position)
        }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            Util.CANCEL
        ) { dialog, which -> Log.d(TAG, "Cancel") }

        alertDialog.show()
    }

    private fun deletePrescription(position: Int) {
        firestore
            .collection(Util.PATIENT_PRESCRIPTION_DATABASE)
            .document(patientId)
            .collection(Util.PRESCRIPTION_DATABASE)
            .document(prescriptionList[position].key.trim())
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    Util.PRESCRIPTION_DELETE_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()

                prescriptionList.removeAt(position)
                setUpPrescriptionList()
            }
            .addOnFailureListener {

                Log.d(TAG, "Error: ${it.message}")
                Toast.makeText(
                    context,
                    Util.OPERATION_FAILED_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
