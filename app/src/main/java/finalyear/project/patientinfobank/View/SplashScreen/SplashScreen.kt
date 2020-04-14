package finalyear.project.patientinfobank.View.SplashScreen

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Pair
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.Doctor.DoctorActivity
import finalyear.project.patientinfobank.View.Login.Login
import finalyear.project.patientinfobank.View.Login.UserCategory
import finalyear.project.patientinfobank.View.Patient.PatientActivity
import kotterknife.bindView
import maes.tech.intentanim.CustomIntent
import java.lang.Exception

class SplashScreen : AppCompatActivity() {

    private val imageView: ImageView by bindView(R.id.logoHeartId)
    private val logoLayout: ConstraintLayout by bindView(R.id.logoLayout)
    private lateinit var animation: Animation
    private lateinit var firebaseAuth: FirebaseAuth
    private val TAG = "SplashScreen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.exitTransition  = null
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash_screen)

        firebaseAuth = FirebaseAuth.getInstance()


        animation = AnimationUtils.loadAnimation(this, R.anim.heart_beat)
        imageView.startAnimation(animation)


    }

    override fun onResume() {
        super.onResume()
        var time = 8000
        if (firebaseAuth.currentUser != null) {

            time = 3000
        }
        val handler: Handler = Handler()
        handler.postDelayed({
            try {
                if (firebaseAuth.currentUser != null) {

                    Log.d(TAG, "CurrentUser: ${firebaseAuth.currentUser!!.email}")
                    checkUserCategory(firebaseAuth.currentUser!!)
                } else {
                    openLoginActivity()
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message)
            }
        }, time.toLong())
    }

    private fun checkUserCategory(currentUser: FirebaseUser) {
        val email = currentUser.email
        val database = FirebaseFirestore.getInstance()

        if (email != null) {
            database.collection(Util.USER_CATEGORY_DATABASE)
                .document(email)
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "UserCategory: ${it.data?.get(Util.USER_CATEGORY_IS_DOCTOR)}")

                    var intent: Intent

                    if (it.data == null) {
                        intent = Intent(applicationContext, UserCategory::class.java)
                    } else {
                        val isdoctor: Boolean = it.data!!.get(Util.USER_CATEGORY_IS_DOCTOR) as Boolean
                        if (isdoctor) {
                            intent = Intent(applicationContext, DoctorActivity::class.java)
                        } else {
                            intent = Intent(applicationContext, PatientActivity::class.java)
                        }
                    }

                    startActivity(intent)
                    CustomIntent.customType(this, "right-to-left")
                    finish()
                }
                .addOnFailureListener {
                    openLoginActivity()
                }
        }
    }

    private fun openLoginActivity() {
        try {
            val intent = Intent(this, Login::class.java)

            val pair = Pair<View, String>(
                logoLayout,
                resources.getString(R.string.logoTransition)
            )

            val options = ActivityOptions.makeSceneTransitionAnimation(this, pair)
            startActivity(intent, options.toBundle())
            CustomIntent.customType(this, "fadein-to-fadeout")
            finish()
        } catch (e: Exception) {
            Log.d(TAG, "Splash: ${e.message}")
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            CustomIntent.customType(this, "fadein-to-fadeout")
            finish()
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause:")
        finish()
        super.onPause()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDistroy:")
        super.onDestroy()
    }
}
