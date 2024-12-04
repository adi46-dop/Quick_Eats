package com.example.quickeats

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.quickeats.databinding.ActivitySignInBinding
import com.example.quickeats.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignInActivity : AppCompatActivity() {
    private var _binding : ActivitySignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var email : String
    private lateinit var name : String
    private lateinit var password : String
    private lateinit var googleSignClient : GoogleSignInClient
    private lateinit var mAuth : FirebaseAuth
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("906637558272-01mda47elmsv81lqlt03angvv9td0dnd.apps.googleusercontent.com").requestEmail().build()

        mAuth =FirebaseAuth.getInstance()
        database = Firebase.database.reference
        googleSignClient = GoogleSignIn.getClient(this,googleSignInOptions)

        binding.btnGoogle.setOnClickListener {
            val signIntent = googleSignClient.signInIntent
            launcher.launch(signIntent)
        }

        binding.btnCreateAccount.setOnClickListener{
            name = binding.edtName.text.toString().trim()
            email = binding.edtEmailAddressSign.text.toString().trim()
            password = binding.edtPasswordSign.text.toString().trim()

            if(name.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please enter all the details",Toast.LENGTH_SHORT).show()
            }else {
                createAccount(email,password)
            }
        }

        binding.txtAlreadyAccount.setOnClickListener {
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)

        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if (result.resultCode == Activity.RESULT_OK){

            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            if(task.isSuccessful){
                val account: GoogleSignInAccount? =task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken,null)

                mAuth.signInWithCredential(credential).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Toast.makeText(this,"SignIn Successfully",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }else {
                        Toast.makeText(this,"SignIn Failed",Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener(this) {
                    exception ->
                    Toast.makeText(this,exception.message,Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(this,"SignIn failed",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createAccount(email :String,password :String) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this,"Account Created Successfully",Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }else {
                Toast.makeText(this,"Account Creation Failed",Toast.LENGTH_SHORT).show()
                Log.d("Account","createAccount",task.exception)
            }

        }
    }

    private fun saveUserData() {
        name = binding.edtName.text.toString().trim()
        email = binding.edtEmailAddressSign.text.toString().trim()
        password = binding.edtPasswordSign.text.toString().trim()

        val user = UserModel(name,email,password)

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("user").child(userId).setValue(user)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}