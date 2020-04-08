package finalyear.project.patientinfobank.View.Login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.Binds
import finalyear.project.patientinfobank.R
import finalyear.project.patientinfobank.Utils.Util
import finalyear.project.patientinfobank.View.MainActivity.MainActivity
import kotterknife.bindView
import maes.tech.intentanim.CustomIntent

class Login : AppCompatActivity() {
    private val loginButton: CardView by bindView(R.id.logInButtonId)
    private val TAG: String = "Login"
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()
        googleSignInClientCreate()

        loginButton.setOnClickListener {
            signIn()
        }
    }

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
                    "Sign in failed",
                    Toast.LENGTH_SHORT
                ).show()
                // ...
            }
        }
    }

    // Signing in firebase
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {

        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)

        firebaseAuth.signInWithCredential(authCredential)
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    Toast.makeText(
                        this,
                        "Successfully signed in",
                        Toast.LENGTH_SHORT
                    ).show()

                    updateUI()

                } else {

                    Log.d(TAG, "Firebase sign in failed "+ (task.exception?.message ?: null))
                    Toast.makeText(
                        this,
                        "Sign in failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    // Opening main activity
    private fun updateUI() {
        val intent = Intent(this, MainActivity:: class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        CustomIntent.customType(this, "left-to-right")
    }
}
