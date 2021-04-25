package com.example.footballapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var firebaseDB: FirebaseDatabase
    private lateinit var userRef: DatabaseReference

    private val TAG = "SignUp"

    lateinit var signUpFirstName: EditText
    lateinit var signUpLastName: EditText
    lateinit var signUpEmail: EditText
    lateinit var signUpPassword: EditText
    lateinit var progressBAr: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        signUpFirstName = findViewById(R.id.et_signUp_first_name)
        signUpLastName = findViewById(R.id.et_signUp_last_name)
        signUpEmail = findViewById(R.id.et_signUp_email)
        signUpPassword = findViewById(R.id.et_signUp_password)
        progressBAr = findViewById(R.id.pb_sign_up)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // set up realtime database
        firebaseDB = FirebaseDatabase.getInstance()
        userRef = firebaseDB.getReference("Users")

    }
    fun toLoginUserActivity(view: View){
        val intent = Intent(this,LogInActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun registerUser(view: View) {
        // input validation
        if (signUpFirstName.text.toString().isNullOrEmpty()) {
            signUpFirstName.error = "First name is required!"
            signUpFirstName.text.clear()
            signUpFirstName.requestFocus()
            signUpFirstName.hideKeyboard()
        }

        if (signUpLastName.text.toString().isEmpty()) {
            signUpLastName.error = "Last name is required!"
            signUpLastName.text.clear()
            signUpLastName.requestFocus()
            signUpLastName.hideKeyboard()
        }

        if (signUpEmail.text.toString().isEmpty()) {
            signUpEmail.error = "Email is required!"
            signUpEmail.text.clear()
            signUpEmail.requestFocus()
            signUpEmail.hideKeyboard()
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(signUpEmail.text.toString()).matches()) {
            signUpEmail.error = "Please provide a valid email!"
            signUpEmail.text.clear()
            signUpEmail.requestFocus()
            signUpEmail.hideKeyboard()
        }

        if (signUpPassword.text.toString().isEmpty()) {
            signUpPassword.error = "Password is required!"
            signUpPassword.text.clear()
            signUpPassword.requestFocus()
            signUpPassword.hideKeyboard()
        }
        // firebase does not accept a password of length less than 6 characters
        val minPasswordLength = 6
        if (signUpPassword.text.length < minPasswordLength) {
            signUpPassword.error = "Minimum password length should be 6 characters"
            signUpPassword.text.clear()
            signUpPassword.requestFocus()
            signUpPassword.hideKeyboard()
        }


        if ((signUpFirstName.text.isNullOrEmpty() || signUpFirstName.text.isNullOrBlank()) || (signUpEmail.text.isNullOrEmpty() || signUpEmail.text.isNullOrBlank())
            || (signUpPassword.text.isNullOrEmpty() || signUpPassword.text.isNullOrBlank()) || (signUpLastName.text.isNullOrEmpty() || signUpLastName.text.isNullOrBlank())
        ) {
            // do nothing
        } else {

            progressBAr.visibility = View.VISIBLE
            // sign up segment
            mAuth.createUserWithEmailAndPassword(
                signUpEmail.text.toString(),
                signUpPassword.text.toString()
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // get new user
                    val user = FirebaseAuth.getInstance().currentUser

                    Log.d(
                        TAG,
                        "Registered User With Email success: ${user?.displayName.toString()}"
                    )
                    Log.d(TAG, "creation time: ${user?.metadata?.creationTimestamp}")
                    Log.d(TAG, "last sign in : ${user?.metadata?.lastSignInTimestamp}")

                    //Checking for User (New/Old) // createUserWithEmailAndPassword exception should handle this
                    if (user?.metadata?.creationTimestamp == user?.metadata?.lastSignInTimestamp) {
                        //This is a New User
                        Toast.makeText(this, "Welcome New User!", Toast.LENGTH_SHORT).show()

                        // add new user data to realtime DB using auth Uid
                        val profileData = UserSignUpData(
                            signUpFirstName.text.toString(),
                            signUpLastName.text.toString(),
                            signUpEmail.text.toString()
                        )
                        //val uID = FirebaseAuth.getInstance().currentUser?.uid
                        val uID = mAuth.currentUser!!.uid

                        userRef.child(uID).setValue(profileData).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User data added to DB user: $uID")
                            } else {
                                Log.e(TAG, "User data write is not successful:${task.exception} ")
                                Toast.makeText(
                                    this, "User data write failed.",
                                    Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                        // user proceeds to demographic activity
                        progressBAr.visibility = View.INVISIBLE
                        // send verification email for nex time sign in
                        //user?.sendEmailVerification()
                        // Since the user signed in, the user can go back to main activity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    /**
                    else {
                    //This is a returning user
                    Toast.makeText(this, "An Account with Email $email already exists. \nLog in or Reset password", Toast.LENGTH_SHORT).show()
                    pb_sign_up_circle.visibility = View.INVISIBLE
                    // redirect to login page
                    val intent = Intent(this, LoginUser::class.java)
                    startActivity(intent)
                    finish()
                    }
                     */
                } else {
                    Log.e(TAG, "Sign up is not successful:${task.exception?.message.toString()} ")
                    showErrorDialog("Registration Failed", task.exception?.message.toString())
                    //Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    progressBAr.visibility = View.INVISIBLE
                }

            }

        }

    }


    //utility---------------------------------------------------------------------------------------
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