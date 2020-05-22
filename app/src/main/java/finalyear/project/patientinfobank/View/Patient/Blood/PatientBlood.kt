package finalyear.project.patientinfobank.View.Patient.Blood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.databinding.FragmentPatientBloodBinding
import maes.tech.intentanim.CustomIntent

class PatientBlood : Fragment(), View.OnClickListener{

    private lateinit var binding: FragmentPatientBloodBinding
    private val TAG = "PatientBlood"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentPatientBloodBinding.inflate(
            inflater,
            container,
            false
        )

        Log.d(TAG, "Created")
        binding.beDonor.setOnClickListener(this)
        binding.findDonor.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        if (v != null) {
            var intent: Intent? = null
            when (v.id) {
                R.id.beDonor->{
                    Log.d(TAG, "Be donor:")
                    intent = Intent(context, BeADonor::class.java)
                }
                R.id.findDonor-> {
                    Log.d(TAG, "find donor:")
                    intent = Intent(context, FindDonor::class.java)
                }
            }
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft)
        }
    }


}
