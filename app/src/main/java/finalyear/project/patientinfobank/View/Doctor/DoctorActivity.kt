package finalyear.project.patientinfobank.View.Doctor

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import finalyear.project.patientinfobank.Adapter.BottomNavigation.BottomNavigationAdapter
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.Doctor.Home.DoctorHome
import finalyear.project.patientinfobank.View.Doctor.Profile.DoctorProfile
import finalyear.project.patientinfobank.databinding.ActivityDoctorBinding


class DoctorActivity : AppCompatActivity() {

    private val TAG = "DoctorActivity"

    private lateinit var binding: ActivityDoctorBinding
    private lateinit var bottomNavigationAdapter: BottomNavigationAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor)

        setUpNavigationView()
    }

    // Setting up bottom navigation view
    private fun setUpNavigationView() {
        binding.navigationView.setItemSelected(R.id.home)

        bottomNavigationAdapter = BottomNavigationAdapter(supportFragmentManager)

        bottomNavigationAdapter.addFragment(DoctorHome())
        bottomNavigationAdapter.addFragment(DoctorProfile())

        binding.viewPager.adapter = this.bottomNavigationAdapter

        binding.viewPager.currentItem = Util.DOCTOR_HOME

        // NavigationView item selection listener
        binding.navigationView.setOnItemSelectedListener {
            when(it) {
                R.id.home -> {Log.d(TAG, "NavigationView: Home is clicked")

                    binding.viewPager.currentItem = Util.DOCTOR_HOME
                }
                R.id.profile -> {Log.d(TAG, "NavigationView: Profile is clicked")

                    binding.viewPager.currentItem = Util.DOCTOR_PROFILE
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
                if (position == Util.DOCTOR_PROFILE)
                    selectedId = R.id.profile
                binding.navigationView.setItemSelected(selectedId)
            }
        })
    }
}
