package finalyear.project.patientinfobank.View.Doctor.Profile

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.common.base.Strings.isNullOrEmpty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.UserCategory.UserCategoryUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.Utils.ValidityChecker
import finalyear.project.patientinfobank.View.Login.Login
import finalyear.project.patientinfobank.databinding.FragmentDoctorProfileBinding
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_user_category.*
import maes.tech.intentanim.CustomIntent
import java.lang.Exception


class DoctorProfile : Fragment() {
    private val TAG = "DoctorProfile"

    private lateinit var binding: FragmentDoctorProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var email: String
    private lateinit var name: String
    private lateinit var userCategoryUtils: UserCategoryUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDoctorProfileBinding.inflate(inflater, container, false)

        setUpToolbar()

        firebaseAuth = FirebaseAuth.getInstance()
        userCategoryUtils = UserCategoryUtils()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (isNullOrEmpty(userCategoryUtils.phoneNumber))
            retrieveData()
    }

    // Method to setup toolbar
    private fun setUpToolbar() {

        binding.toolbar.title = Util.PROFILE
        binding.toolbar.setTitleTextColor(Color.WHITE)
        (activity as AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.changeProfilePicture -> changeProfilePicture()
                R.id.changeName-> changeName()
                R.id.changeContact-> changeContact()
                R.id.signOut-> signOut()
            }
            return@setOnMenuItemClickListener super.onOptionsItemSelected(it)
        }
    }


    // Creating option menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_profile, menu)
        Log.d(TAG, "OptionMenu")
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Method to change profile picture
    private fun changeProfilePicture() {

        Log.d(TAG, "Change profile picture")
    }

    // Method to change name
    private fun changeName() {
        Log.d(TAG, "Change name")
        if (binding.contact.visibility == View.INVISIBLE)
            closeContactEditOption()

        binding.nameEditLayout.visibility = View.VISIBLE
        binding.saveName.visibility = View.VISIBLE
        binding.closeNameEdit.visibility = View.VISIBLE
        binding.name.visibility = View.INVISIBLE

        name = firebaseAuth.currentUser?.displayName.toString()
        binding.nameEditText.setText(name)
        binding.nameEditText.requestFocus()
        binding.nameEditText.setSelection(name.length)

        binding.saveName.setOnClickListener {
            val newName = binding.nameEditText.text.toString()
            if (isNullOrEmpty(newName)) {
                binding.nameEditText.error = Util.EMPTY_ERROR_MESSAGE
                binding.nameEditText.requestFocus()
            } else {
                name = newName.trim()
                userCategoryUtils.name = name
                val updateUserProfileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()

                updateUserProfile(updateUserProfileChangeRequest)
                updateName()
            }
        }

        binding.closeNameEdit.setOnClickListener {
            closeNameEditOption()
        }
    }

    private fun closeNameEditOption() {
        binding.nameEditLayout.visibility = View.GONE
        binding.saveName.visibility = View.GONE
        binding.closeNameEdit.visibility = View.GONE
        binding.name.visibility = View.VISIBLE
    }


    // Updating user profile
    private fun updateUserProfile(updateUserProfileChangeRequest: UserProfileChangeRequest) {

        runProgress()

        firebaseAuth
            .currentUser
            ?.updateProfile(updateUserProfileChangeRequest)
            ?.addOnSuccessListener {
                Toast.makeText(
                    context,
                    Util.UPDATE_SUCCESSFUL_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
                binding.name.text = name
                if (binding.name.visibility == View.INVISIBLE) {
                    closeNameEditOption()
                }

                binding.progress.visibility = View.GONE
                binding.progressHeart.visibility = View.GONE
            }
            ?.addOnFailureListener {
                Log.d(TAG, "Failed changing user profile: ${it.message}")
                Toast.makeText(
                    context,
                    Util.OPERATION_FAILED_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
                if (binding.name.visibility == View.INVISIBLE) {
                    closeNameEditOption()
                }

                stopProgress()
            }
    }

    // Method to change contact
    private fun changeContact() {
        Log.d(TAG, "Change contact")
        if (binding.name.visibility == View.INVISIBLE) {
            closeNameEditOption()
        }
        binding.contactEditText.visibility = View.VISIBLE
        binding.saveContact.visibility = View.VISIBLE
        binding.closeContactEdit.visibility = View.VISIBLE
        binding.contact.visibility = View.INVISIBLE

        binding.contactEditText.setText(userCategoryUtils.phoneNumber)
        binding.contactEditText.requestFocus()
        binding.contactEditText.setSelection(binding.contactEditText.text.length)

        binding.saveContact.setOnClickListener {
            if (checkContactValidity()) {
                Log.d(TAG, "SaveContact: " + binding.contactEditText.text.toString())
                userCategoryUtils.phoneNumber = binding.contactEditText.text.toString()
                binding.contact.text = userCategoryUtils.phoneNumber
                updateContact()
            }
        }

        binding.closeContactEdit.setOnClickListener {
            closeContactEditOption()
        }
    }

    // Updating contact into database
    private fun updateContact() {

        runProgress()

        val database = FirebaseFirestore.getInstance()

        database
            .collection(Util.USER_CATEGORY_DATABASE)
            .document(email)
            .update(
                Util.USER_CATEGORY_PHONE_NUMBER,
                userCategoryUtils.phoneNumber
            )
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    Util.UPDATE_SUCCESSFUL_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
                stopProgress()
                closeContactEditOption()
            }
            .addOnFailureListener{
                Log.d(TAG, "UpdateUserCategory: ${it.message}")
                Toast.makeText(
                    context,
                    Util.OPERATION_FAILED_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
                stopProgress()
            }
    }


    // Updating name into database
    private fun updateName() {

        runProgress()

        val database = FirebaseFirestore.getInstance()

        database
            .collection(Util.USER_CATEGORY_DATABASE)
            .document(email)
            .update(
                Util.USER_CATEGORY_NAME,
                userCategoryUtils.name
            )
            .addOnSuccessListener {
                Log.d(TAG, "UpdateUserCategory: Success")
                Toast.makeText(
                    context,
                    Util.UPDATE_SUCCESSFUL_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
                binding.name.text = name
                stopProgress()
                closeNameEditOption()
                closeContactEditOption()
            }
            .addOnFailureListener{
                Log.d(TAG, "UpdateUserCategory: ${it.message}")
                Toast.makeText(
                    context,
                    Util.OPERATION_FAILED_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
                stopProgress()
            }
    }


    // Checking contact validity
    private fun checkContactValidity(): Boolean {

        val phoneNumber = binding.contactEditText.text.toString()
        Log.d(TAG, "CheckValidity: $phoneNumber")
        if (isNullOrEmpty(phoneNumber)) {
            binding.contactEditText.requestFocus()
            binding.contactEditText.error = Util.EMPTY_ERROR_MESSAGE
            return false
        }

        // Checking the phone number is valid or not
        val validityChecker = ValidityChecker()

        if (!validityChecker.isValidPhoneNumber(phoneNumber)) {
            binding.contactEditText.requestFocus()
            binding.contactEditText.error = Util.INVALID_PHONE_NUMBER_ERROR_MESSAGE
            binding.contactEditText.setSelection(phoneNumber.length)
            return false
        }

        return true

    }

    private fun closeContactEditOption() {
        binding.contactEditText.visibility = View.GONE
        binding.saveContact.visibility = View.GONE
        binding.closeContactEdit.visibility = View.GONE
        binding.contact.visibility = View.VISIBLE
    }

    // Degree Layout setting
    private fun setAddDegreeLayout() {
        binding.addDegreeButton.setOnClickListener {
            val degree = binding.doctorDegree.text.toString()
            if (degree == null
                || degree == "")
                binding.doctorDegree.error = Util.EMPTY_ERROR_MESSAGE
            else {
                userCategoryUtils.doctorDegreeList.add(degree)
                updateDegreeList()
            }
        }
    }

    // Updating DegreeList into database
    private fun updateDegreeList() {

        runProgress()

        val database = FirebaseFirestore.getInstance()

        database
            .collection(Util.USER_CATEGORY_DATABASE)
            .document(email)
            .update(
                Util.USER_CATEGORY_DOCTOR_DEGREE_LIST,
                userCategoryUtils.doctorDegreeList
            )
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    Util.UPDATE_SUCCESSFUL_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
                stopProgress()
                binding.doctorDegree.setText(Util.EMPTY_VALUE)
            }
            .addOnFailureListener{
                Log.d(TAG, "UpdateUserCategory: ${it.message}")
                Toast.makeText(
                    context,
                    Util.OPERATION_FAILED_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
                stopProgress()
            }
    }

    // Method to sign out
    private fun signOut() {
        Log.d(TAG, "Sign out")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        val googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }
        if (googleSignInClient != null) {
            googleSignInClient.signOut()
            firebaseAuth.signOut()

            val intent = Intent(context, Login::class.java)
            startActivity(intent)
            CustomIntent.customType(context, "left-to-right")
            activity?.finish()
        }
    }


    // Fetching data from database
    private fun retrieveData() {
        runProgress()

        name = firebaseAuth.currentUser?.displayName.toString()
        email = firebaseAuth.currentUser?.email.toString()

        if (!isNullOrEmpty(email)) {
            val firebaseFirestore = FirebaseFirestore.getInstance()
            firebaseFirestore
                .collection(Util.USER_CATEGORY_DATABASE)
                .document(email)
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "user: $userCategoryUtils")
                    if (it != null)
                        userCategoryUtils = it.toObject(UserCategoryUtils::class.java)!!
                    setViewsData()

                    stopProgress()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        Util.DATA_NOT_FOUND_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()
                    stopProgress()
                }
        }

    }

    // Setting data into views
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
                ArrayAdapter(it, R.layout.view_degrees_list_profile, R.id.degreeViewId,
                    it1
                )
            } }
            binding.degreesList.adapter = arrayAdapter

            setAddDegreeLayout()

        } catch (e: Exception) {
            Log.d(TAG, "SetViewData: ${e.message}")
        }
    }


    private fun stopProgress() {
        binding.progress.visibility = View.GONE
    }
    private fun runProgress() {

        val animation = AnimationUtils.loadAnimation(context, R.anim.heart_beat)
        binding.progressHeart.startAnimation(animation)

        binding.progress.visibility = View.VISIBLE
    }

}
