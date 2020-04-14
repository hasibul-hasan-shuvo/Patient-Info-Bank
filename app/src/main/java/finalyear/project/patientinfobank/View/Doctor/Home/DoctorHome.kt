package finalyear.project.patientinfobank.View.Doctor.Home

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Pair
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.Doctor.Prescription.PatientPrescription
import finalyear.project.patientinfobank.databinding.FragmentDoctorHomeBinding
import maes.tech.intentanim.CustomIntent


class DoctorHome : Fragment() {

    private val TAG: String = "DoctorHome"
    private lateinit var binding: FragmentDoctorHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoctorHomeBinding.inflate(inflater, container, false)

        binding.searchButton.setOnClickListener {
            if (checkValidity()) {
                Log.d(TAG, "OnSearch:")

                var intent = Intent(context, PatientPrescription::class.java)

                intent.putExtra(Util.SEARCH_PATIENT_ID, binding.searchId.text.toString())

                val pair = Pair<View, String>(
                    binding.patientIdSearchLayout,
                    resources.getString(R.string.searchBarTransition)
                )

                val options = ActivityOptions.makeSceneTransitionAnimation(activity, pair)

                val inputMethodManager = context
                    ?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    binding.searchId.windowToken,
                    0
                )

                activity?.window?.exitTransition  = null
                startActivity(intent, options.toBundle())
                CustomIntent.customType(context, "fadein-to-fadeout")
            }
        }

        return binding.root
    }

    private fun checkValidity(): Boolean {
        if (binding.searchId.text.toString() == "" ||
                binding.searchId.text.toString() == null) {
            binding.searchId.error = Util.EMPTY_ERROR_MESSAGE
            return false
        }
        return true
    }

}
