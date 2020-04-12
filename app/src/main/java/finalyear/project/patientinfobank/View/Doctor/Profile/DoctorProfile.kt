package finalyear.project.patientinfobank.View.Doctor.Profile

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.UserCategory.UserCategoryUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.databinding.FragmentDoctorProfileBinding
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception


class DoctorProfile : Fragment() {
    private val TAG = "DoctorProfile"

    private lateinit var binding: FragmentDoctorProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var email: String
    private lateinit var name: String
    private lateinit var userCategoryUtils: UserCategoryUtils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDoctorProfileBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        userCategoryUtils = UserCategoryUtils()

        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onResume() {
        super.onResume()

        if (userCategoryUtils.phoneNumber == "" ||
                userCategoryUtils.phoneNumber == null)
            retrieveData()
    }

    private fun retrieveData() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.heart_beat)
        binding.progressHeart.startAnimation(animation)

        binding.progress.visibility = View.VISIBLE
        binding.progressHeart.visibility = View.VISIBLE

        name = firebaseAuth.currentUser?.displayName.toString()
        email = firebaseAuth.currentUser?.email.toString()

        if (email != null) {
            val firebaseFirestore = FirebaseFirestore.getInstance()
            firebaseFirestore
                .collection(Util.USER_CATEGORY_DATABASE)
                .document(email)
                .get()
                .addOnSuccessListener {
                    if (it != null)
                    userCategoryUtils = it.toObject(UserCategoryUtils::class.java)!!
                    setViewsData()

                    binding.progress.visibility = View.GONE
                    binding.progressHeart.visibility = View.GONE
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        Util.DATA_NOT_FOUND_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

    }

    private fun setViewsData() {
        try {
            // Setting profile picture
            Picasso.get()
                .load(firebaseAuth.currentUser?.photoUrl)
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .into(binding.profilePicture)

            Log.d(TAG, "SetViewData: ${firebaseAuth.currentUser?.photoUrl}")

            // Setting name, email, contact
            binding.name.text = name
            binding.email.text = email
            binding.contact.text = userCategoryUtils.phoneNumber

            val degreesList = userCategoryUtils.doctorDegreeList
            val arrayAdapter = context?.let { degreesList?.let { it1 ->
                ArrayAdapter(it, R.layout.view_degrees_list, R.id.degreeViewId,
                    it1
                )
            } }
            binding.degreesList.adapter = arrayAdapter

        } catch (e: Exception) {
            Log.d(TAG, "SetViewData: ${e.message}")
        }
    }

}
