package com.example.footballapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LogInActivity : AppCompatActivity() {

    private lateinit var firebaseDB: FirebaseDatabase
    private lateinit var userRef: DatabaseReference

    lateinit var emailLogIn: EditText
    lateinit var passwordLogIn: EditText
    lateinit var progressBar: ProgressBar
    lateinit var logInButton : Button
    private lateinit var mAuth: FirebaseAuth


    private val TAG = "LogIn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        supportActionBar?.hide()

        progressBar = findViewById(R.id.pb_log_in)
        emailLogIn = findViewById(R.id.et_logIn_email)
        passwordLogIn = findViewById(R.id.et_logIn_password)
        logInButton = findViewById(R.id.btn_logIn)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
    }

    // start activity to reset password ----------------------------------------------------------
    fun toForgotPassword(view: View) {
        val intent = Intent(this, ResetActivity::class.java)
        startActivity(intent)
        finish()
    }

    // start activity to sign up user ------------------------------------------------------------
    fun toSignUp(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    //-------------------------- Sign in with EMAIL & PASSWORD -------------------------------------
    fun login(view: View) {

        // input validation
        if (emailLogIn.text.toString().isNullOrEmpty()) {
            emailLogIn.error = "Email is required!"
            emailLogIn.text.clear()
            emailLogIn.requestFocus()
            emailLogIn.hideKeyboard()
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailLogIn.text.toString()).matches()) {
            emailLogIn.error = "Please provide a valid email!"
            emailLogIn.text.clear()
            emailLogIn.requestFocus()
            emailLogIn.hideKeyboard()
        }

        if (passwordLogIn.text.toString().isEmpty()) {
            passwordLogIn.error = "Password is required!"
            passwordLogIn.text.clear()
            passwordLogIn.requestFocus()
            passwordLogIn.hideKeyboard()
        }
        // firebase does not accept a password of length less than 6 characters
        val minPasswordLength = 6
        if (passwordLogIn.text.length < minPasswordLength) {
            passwordLogIn.error = "Minimum password length should be 6 characters"
            passwordLogIn.text.clear()
            passwordLogIn.requestFocus()
            passwordLogIn.hideKeyboard()
        }

        Log.d(TAG, "EMAIL: $emailLogIn  PASSWORD: $passwordLogIn")

        if (emailLogIn.text.isNullOrEmpty() || emailLogIn.text.isNullOrEmpty()) { // user presses login button
            // do nothing here onCLick()
        } else if (view == logInButton) {
            progressBar.visibility = View.VISIBLE
            mAuth.signInWithEmailAndPassword(emailLogIn.text.toString(), passwordLogIn.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // get new user
                        val user = FirebaseAuth.getInstance().currentUser

                        if (user != null) {
                            if (user.isEmailVerified) {
                                progressBar.visibility = View.INVISIBLE
                                Log.d(TAG, "Sign In User With Email success: ${user.uid}")
                                //This is a returning user // not sure about this part
                                Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show()
                                // redirect to main activity
                                // Since the user signed in, the user can go back to main activity
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                // Make sure to call finish(), otherwise the user would be able to go back to the RegisterActivity, not good
                                finish()
                            } else {
                                user.sendEmailVerification().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "Email sent.")
                                    }
                                }
                                Toast.makeText(
                                    this,
                                    "Check your Email to verify your account and sign in again"
                                    ,
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressBar.visibility = View.INVISIBLE
                            }
                        }
                    } else {
                        Log.e(TAG, "Sign in is not successful:${task.exception} ")
                        showErrorDialog("Sign In Failed", task.exception?.message.toString())
                        //Toast.makeText(this, "Sign in failed.", Toast.LENGTH_SHORT).show();
                        progressBar.visibility = View.INVISIBLE
                    }
                }

        } //else if  {
        // do something
        //}

    }


    //utility--------------------------------------------------------------------------------------
    // hide keyboard
    private fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun showErrorDialog(title: String, message: String) {
        // Create an alertdialog builder object,
        // then set attributes that you want the dialog to have
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(R.drawable.ic_baseline_error_24)
        builder.setPositiveButton("OK") { dialog, which ->
            // code to run when YES is pressed
        }
        // create the dialog and show it
        val dialog = builder.create()
        dialog.show()
    }
}