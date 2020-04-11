package finalyear.project.patientinfobank.View.SplashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
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

class SplashScreen : AppCompatActivity() {

    private val imageView: ImageView by bindView(R.id.logoHeartId)
    private lateinit var animation: Animation
    private lateinit var firebaseAuth: FirebaseAuth
    private val TAG = "SplashScreen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val handler: Handler = Handler()
        handler.postDelayed({
            if (firebaseAuth.currentUser != null) {

                Log.d(TAG, "CurrentUser: ${firebaseAuth.currentUser!!.email}")
                checkUserCategory(firebaseAuth.currentUser!!)
            } else {
                var intent: Intent
                intent = Intent(applicationContext, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                CustomIntent.customType(this, "fadein-to-fadeout")
            }
        }, 8000)
    }

    private fun checkUserCategory(currentUser: FirebaseUser) {
        val email = currentUser.email
        val database = FirebaseFirestore.getInstance()

        if (email != null) {
            database.collection("UsersCategory")
                .document(email)
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "UserCategory: ${it.data?.get("doctor")}")

                    var intent: Intent

                    if (it.data == null) {
                        intent = Intent(applicationContext, UserCategory::class.java)
                    } else {
                        val isdoctor: Boolean = it.data!!.get("doctor") as Boolean
                        if (isdoctor) {
                            intent = Intent(applicationContext, DoctorActivity::class.java)
                        } else {
                            intent = Intent(applicationContext, PatientActivity::class.java)
                        }
                    }

                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    CustomIntent.customType(this, "right-to-left")
                }
                .addOnFailureListener {
                    val intent: Intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    CustomIntent.customType(this, "fadein-to-fadeout")
                }
        }
    }
}
