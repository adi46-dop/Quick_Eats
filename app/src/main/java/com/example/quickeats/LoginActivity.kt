package com.example.quickeats

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.quickeats.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val googleSignInOptions = GoogleSignInOptions.Builder()
            .requestIdToken("906637558272-01mda47elmsv81lqlt03angvv9td0dnd.apps.googleusercontent.com")
            .requestEmail().build()

        val googleSignClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.btnLogin.setOnClickListener {
            email = binding.edtEmailAddressLogin.text.toString().trim()
            password = binding.edtPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter all credentials", Toast.LENGTH_SHORT).show()
            } else {
                startLogin(email, password)
            }
        }
        binding.txtCreateAccount.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignClient.signInIntent
            launcher.launch(signInIntent)
        }
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode == Activity.RESULT_OK){
            val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account: GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
                mAuth.signInWithCredential(credential).addOnCompleteListener {
                    Toast.makeText(this,"SignIn Successfully",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }.addOnFailureListener(this) {exception->
                    Toast.makeText(this,exception.message,Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"SignIn Failed",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun startLogin(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                val user = mAuth.currentUser
                updateUi(user)
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener(this) { exception ->
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()

        }
    }

    private fun updateUi(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}