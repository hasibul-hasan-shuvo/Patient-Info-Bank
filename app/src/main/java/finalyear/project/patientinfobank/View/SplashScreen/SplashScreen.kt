package finalyear.project.patientinfobank.View.SplashScreen

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.View.Login.Login
import finalyear.project.patientinfobank.View.MainActivity.MainActivity
import kotterknife.bindView
import maes.tech.intentanim.CustomIntent

class SplashScreen : AppCompatActivity() {

    private val imageView: ImageView by bindView(R.id.logoHeartId)
    private lateinit var animation: Animation
    private lateinit var firebaseAuth: FirebaseAuth

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
            val intent: Intent
            if (firebaseAuth.currentUser != null) {
                intent = Intent(applicationContext, MainActivity::class.java)
            } else {
                intent = Intent(applicationContext, Login::class.java)
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            CustomIntent.customType(this, "fadein-to-fadeout")
        }, 8000)
    }
}
