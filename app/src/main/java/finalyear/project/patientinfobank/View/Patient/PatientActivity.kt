package finalyear.project.patientinfobank.View.Patient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.View.Login.Login
import maes.tech.intentanim.CustomIntent

class PatientActivity : AppCompatActivity() {

    private val TAG = "PatientActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)
        signOut()
    }
    private fun signOut() {
        Log.d(TAG, "Sign out")
        val firebaseAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        val googleSignInClient =  GoogleSignIn.getClient(this, gso)
        if (googleSignInClient != null) {
            googleSignInClient.signOut()
            firebaseAuth.signOut()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            CustomIntent.customType(this, "left-to-right")
            finish()
        }
    }
}
