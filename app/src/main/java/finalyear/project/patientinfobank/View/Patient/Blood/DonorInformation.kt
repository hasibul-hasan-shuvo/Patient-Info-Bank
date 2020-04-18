package finalyear.project.patientinfobank.View.Patient.Blood

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.BloodDonor.BloodDonorInformationUtils
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.Utils.UtilFunctions
import finalyear.project.patientinfobank.databinding.ActivityDonorInformationBinding
import java.util.*

class DonorInformation : AppCompatActivity(), View.OnClickListener, View.OnLongClickListener {

    private val TAG = "DonorInformation"

    private lateinit var binding: ActivityDonorInformationBinding
    private lateinit var donorInformationUtils: BloodDonorInformationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_information)

        window.enterTransition = null
        binding = DataBindingUtil.setContentView(this, R.layout.activity_donor_information)
        donorInformationUtils = intent.getSerializableExtra(Util.DONOR_INFORMATION) as BloodDonorInformationUtils
        setUpToolbar()
        setViews()
        setViewListeners()
    }

    // Setting onClick and onLongClick listener
    private fun setViewListeners() {
        binding.emailIcon.setOnClickListener(this)
        binding.email.setOnClickListener(this)
        binding.phoneNumberIcon.setOnClickListener(this)
        binding.phoneNumber.setOnClickListener(this)
        binding.emailIcon.setOnLongClickListener(this)
        binding.email.setOnLongClickListener(this)
        binding.phoneNumberIcon.setOnLongClickListener(this)
        binding.phoneNumber.setOnLongClickListener(this)
    }

    // Setting data into views
    private fun setViews() {
        binding.bloodGroup.text = donorInformationUtils.bloodGroup
        binding.name.text = donorInformationUtils.fullName
        binding.division.text = donorInformationUtils.division
        binding.district.text = donorInformationUtils.district
        binding.location.text = donorInformationUtils.address
        binding.email.text = donorInformationUtils.email
        binding.phoneNumber.text = donorInformationUtils.phoneNumber

        val age = UtilFunctions
            .getAge(donorInformationUtils.birthDate).toString()

        binding.age.text = age
    }


    private fun setUpToolbar() {

        binding.toolbar.title = donorInformationUtils.fullName
        binding.toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onSupportNavigateUp(): Boolean {
        Log.d(TAG, "Back pressed")
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.email, R.id.emailIcon -> openMail()
            R.id.phoneNumber, R.id.phoneNumberIcon -> makeCall()
        }
    }

    // Making a call to clicked number
    private fun makeCall() {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:${donorInformationUtils.phoneNumber}")

        Log.d(TAG, "Calling...")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                Util.REQUEST_CALL
            )
            return
        } else {
            startActivity(callIntent)
        }
    }

    // Taking call permission from device
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Util.REQUEST_CALL) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall()
            } else {
                Toast.makeText(
                    this,
                    Util.PERMISSION_DENIED,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Opening email activity
    private fun openMail() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:${donorInformationUtils.email}"))
        val chooser = Intent.createChooser(intent, Util.SEND_MAIL)
        startActivity(chooser)
    }

    // Copying data on long click into clip board
    override fun onLongClick(v: View?): Boolean {
        var clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var clipData: ClipData? = null
        when(v?.id) {
            R.id.email, R.id.emailIcon -> {
                clipData = ClipData.newPlainText(
                    Util.CONTACT_CLIP_BOARD_LABEL,
                    donorInformationUtils.email
                )
                clipboardManager.setPrimaryClip(clipData)

                Toast.makeText(
                    this,
                    Util.MAIL_COPIED,
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }
            R.id.phoneNumber, R.id.phoneNumberIcon -> {
                clipData = ClipData.newPlainText(
                    Util.MAIL_CLIP_BOARD_LABEL,
                    donorInformationUtils.phoneNumber
                )
                clipboardManager.setPrimaryClip(clipData)

                Toast.makeText(
                    this,
                    Util.CONTACT_COPIED,
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }
        }
        return false
    }

}
