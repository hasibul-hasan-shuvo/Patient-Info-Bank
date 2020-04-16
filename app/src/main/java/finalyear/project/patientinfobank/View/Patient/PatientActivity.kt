package finalyear.project.patientinfobank.View.Patient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import finalyear.project.patientinfobank.Adapter.BottomNavigation.BottomNavigationAdapter
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.Doctor.Home.DoctorHome
import finalyear.project.patientinfobank.View.Doctor.Profile.DoctorProfile
import finalyear.project.patientinfobank.View.Login.Login
import finalyear.project.patientinfobank.View.Patient.Blood.PatientBlood
import finalyear.project.patientinfobank.View.Patient.Home.PatientHome
import finalyear.project.patientinfobank.View.Patient.Profile.PatientProfile
import finalyear.project.patientinfobank.View.Patient.Reminders.PatientReminders
import finalyear.project.patientinfobank.databinding.ActivityPatientBinding
import maes.tech.intentanim.CustomIntent

class PatientActivity : AppCompatActivity() {


    private val TAG = "PatientActivity"

    private lateinit var bottomNavigationAdapter: BottomNavigationAdapter
    private lateinit var binding:ActivityPatientBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient)
        setUpNavigationView()
    }


    // Setting up bottom navigation view
    private fun setUpNavigationView() {
        binding.navigationView.setItemSelected(R.id.home)

        bottomNavigationAdapter = BottomNavigationAdapter(supportFragmentManager)

        bottomNavigationAdapter.addFragment(PatientHome())
        bottomNavigationAdapter.addFragment(PatientReminders())
        bottomNavigationAdapter.addFragment(PatientBlood())
        bottomNavigationAdapter.addFragment(PatientProfile())

        binding.viewPager.adapter = this.bottomNavigationAdapter

        binding.viewPager.currentItem = Util.PATIENT_HOME

        // NavigationView item selection listener
        binding.navigationView.setOnItemSelectedListener {
            when (it) {
                R.id.home -> {
                    Log.d(TAG, "NavigationView: Home is clicked")

                    binding.viewPager.currentItem = Util.PATIENT_HOME
                }
                R.id.reminders -> {
                    Log.d(TAG, "NavigationView: reminders is clicked")

                    binding.viewPager.currentItem = Util.PATIENT_REMINDERS
                }
                R.id.blood -> {
                    Log.d(TAG, "NavigationView: blood is clicked")

                    binding.viewPager.currentItem = Util.PATIENT_BLOOD
                }
                R.id.profile -> {
                    Log.d(TAG, "NavigationView: Profile is clicked")

                    binding.viewPager.currentItem = Util.PATIENT_PROFILE
                }
            }
        }

        // Setting viewpager swiping
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                var selectedId = R.id.home
                if (position == Util.PATIENT_REMINDERS)
                    selectedId = R.id.reminders
                else if (position == Util.PATIENT_BLOOD)
                    selectedId = R.id.blood
                else if (position == Util.PATIENT_PROFILE)
                    selectedId = R.id.profile
                binding.navigationView.setItemSelected(selectedId)
            }
        })
    }
}
