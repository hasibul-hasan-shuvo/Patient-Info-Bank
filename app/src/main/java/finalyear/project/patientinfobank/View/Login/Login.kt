package finalyear.project.patientinfobank.View.Login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.Doctor.DoctorActivity
import finalyear.project.patientinfobank.View.Patient.PatientActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotterknife.bindView
import maes.tech.intentanim.CustomIntent

class Login : AppCompatActivity() {
    private val loginButton: CardView by bindView(R.id.logInButtonId)
    private val progress: LinearLayout by bindView(R.id.progress)
    private val heartLogo: ImageView by bindView(R.id.logoHeartId)
    private val TAG: String = "Login"
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setLogoAnimation()

        firebaseAuth = FirebaseAuth.getInstance()
        googleSignInClientCreate()

        loginButton.setOnClickListener {
            signIn()
        }
    }

    // Logo animation setting
    private fun setLogoAnimation() {

        window?.enterTransition  = null
        val animation = AnimationUtils.loadAnimation(this, R.anim.heart_beat)
        heartLogo.startAnimation(animation)

    }

    // Google sign in window is creating
    private fun googleSignInClientCreate() {

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Util.RC_SIGN_IN)
    }

    // Receiving google account
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Util.RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "Google sign in failed "+ e.message)
                Toast.makeText(
                    this,
                    Util.SIGN_IN_FAILED_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()

                progress.visibility = View.GONE
                progressHeart.visibility = View.GONE
            }
        }
    }

    // Signing in firebase
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val animation = AnimationUtils.loadAnimation(this, R.anim.heart_beat)
        progressHeart.startAnimation(animation)

        progress.visibility = View.VISIBLE

        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)

        firebaseAuth.signInWithCredential(authCredential)
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    Toast.makeText(
                        this,
                        Util.SIGN_IN_SUCCESSFUL_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()

                    FirebaseAuth.getInstance().currentUser?.let { updateUI(it) }

                    progress.visibility = View.VISIBLE

                } else {

                    Log.d(TAG, "Firebase sign in failed "+ (task.exception?.message ?: null))
                    Toast.makeText(
                        this,
                        Util.SIGN_IN_FAILED_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()

                    progress.visibility = View.GONE
                }
            }

    }


    // Opening new window based on login email category
    private fun updateUI(currentUser: FirebaseUser) {
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
                    Log.d(TAG, "Sign in failed "+ it.message)
                    Toast.makeText(
                        this,
                        Util.SIGN_IN_FAILED_MESSAGE,
                        Toast.LENGTH_SHORT
                    ).show()

                    progress.visibility = View.GONE
                }
        }
    }
}
