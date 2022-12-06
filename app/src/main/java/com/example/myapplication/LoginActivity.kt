package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth;
    private lateinit var emailTextView: EditText
    private lateinit var password: EditText

    private lateinit var signin: Button
    private lateinit var signup: Button

    private lateinit var email: String
    private lateinit var passInput: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = Firebase.auth

//
        emailTextView = findViewById(R.id.email)
        password = findViewById(R.id.password)
        signin = findViewById(R.id.signin)
        signup = findViewById(R.id.signup)


        signin.setOnClickListener {
            email = emailTextView.text.toString()
            passInput = password.text.toString()
            signInUser(email,passInput)
        }
        signup.setOnClickListener {
            email = emailTextView.text.toString()
            passInput = password.text.toString()
            signUpUser(email,passInput)
        }
    }


    fun signInUser(email:String,password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SIGN IN SUCCESSFUL", "signInWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "SUCCESS",
                        Toast.LENGTH_SHORT).show()
                    val newSession = Intent(this@LoginActivity, MapsActivity::class.java)
                    startActivity(newSession)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SIGN UP FAILED", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
    fun signUpUser(email:String,password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SUCCESS", "createUserWithEmail:success")
                    val user = auth.currentUser
                    val newSession = Intent(this@LoginActivity, MapsActivity::class.java)
                    startActivity(newSession)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("FAILED", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "-!!!!!!!!!SIGN UP FAILED-!!!!!.",
                        Toast.LENGTH_SHORT).show()
                }

            }

    }
}