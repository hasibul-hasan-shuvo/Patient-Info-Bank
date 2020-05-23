package finalyear.project.patientinfobank.View.Patient.Home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.appcompat.view.menu.MenuBuilder
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
import kotlin.math.min


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

    private lateinit var badgeView: TextView

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPatientHomeBinding.inflate(inflater, container, false)

        getEmail()

        setUpToolbar()
        setUpDatabase()
        fetchPrescriptionList()
        fetchDoctorList()


        Log.d(TAG, "Created")

        return binding.root
    }

    private fun getEmail() {
        email = FirebaseAuth
            .getInstance()
            .currentUser
            ?.email
            ?.split(
                Regex("@")
            )!![0]
    }

    override fun onResume() {
        super.onResume()
        setUpSharedPreference()

        setNotificationBadge(
            sharedPreferences.getInt(
                email + Util.NOTIFICATION_COUNTER,
                0
            )
        )
    }


    /** notifications section starts **/
    private fun setUpSharedPreference() {
        sharedPreferences = context?.getSharedPreferences(
            Util.SHARED_PREFERENCE_PATH,
            Context.MODE_PRIVATE
        )!!

        sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->

            Log.d(TAG, "SharedPreferencecs: $key")
            if (key == email+ Util.NOTIFICATION_COUNTER) {
                setNotificationBadge(
                    sharedPreferences.getInt(
                        key,
                        0
                    )
                )
            }
        }
    }

    private fun setNotificationBadge(counter: Int) {
        Log.d(TAG, "Counter: $counter")
        if (counter == 0) {
            badgeView.visibility = View.GONE
        } else {
            Log.d(TAG, "Counter: $counter")
            if (badgeView.visibility == View.GONE)
                badgeView.visibility = View.VISIBLE
            badgeView.text = min(counter, 99).toString()
        }
    }

    private fun setUpToolbar() {

        Log.d(TAG, "Toolbar")
        binding.toolbar.title = Util.PATIENT_HOME_TITLE
        binding.toolbar.setTitleTextColor(Color.WHITE)
        binding.toolbar.inflateMenu(R.menu.menu_patient_home)
        val menu = binding.toolbar.menu

        val menuItem = menu.findItem(R.id.notifications)

        val actionView = menuItem.actionView

        actionView.setOnClickListener {
            openNotifications()
        }
        badgeView = actionView.findViewById(R.id.badge)

    }

    private fun openNotifications() {
        badgeView.visibility = View.GONE

        sharedPreferences
            .edit()
            .putInt(email + Util.NOTIFICATION_COUNTER, 0)
            .apply()

//        Log.d(TAG, "Notifications")

        val intent = Intent(context, NotificationList::class.java)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.left_to_right, R.anim.righttoleft)
    }

    /** notifications section ends **/

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

                prescriptionList = it.toObjects(PrescriptionUtils:: class.java)
                        as ArrayList<PrescriptionUtils>

                setUpPrescriptionList()
                stopPrescriptionProgressBar()
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
                doctorsList = it.toObjects(UserCategoryUtils ::class.java)
                        as ArrayList<UserCategoryUtils>
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
