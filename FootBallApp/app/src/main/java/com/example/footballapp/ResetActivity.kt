package com.example.footballapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

class ResetActivity : AppCompatActivity() {

    private val TAG = "ForgotPassword"
    lateinit var forget: EditText
    lateinit var progressBAR : ProgressBar
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)
        supportActionBar?.hide() // hide action bar

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        forget = findViewById(R.id.et_forget)
    }

    fun toRegisterUserActivityFromForget(view: View){
        val intent = Intent(this,LogInActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun resetPassword(view: View){
        // input validation
        if(forget.text.toString().isNullOrEmpty()){
            forget.error = "Email is required!"
            forget.text.clear()
            forget.requestFocus()
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(forget.text.toString()).matches()){
            forget.error = "Please provide a valid email!"
            forget.text.clear()
            forget.requestFocus()
        }


        if(forget.text.toString().isNullOrEmpty() || forget.text.toString().isNullOrEmpty()){ // user presses login button
            // do nothing here onCLick()
        }else{
            progressBAR.visibility = View.VISIBLE

            mAuth.sendPasswordResetEmail(forget.text.toString()).addOnCompleteListener { task ->

                if(task.isSuccessful) {
                    Log.d(TAG, "Password Reset success")
                    showDialog("Forgot Password", "Check your email to reset password")
                }else {
                    Log.d(TAG, "Password Reset failure")
                    Log.e(TAG, "Password Reset failure: ${task.exception?.message.toString()} ")
                    showErrorDialog("Password Reset Fail",task.exception?.message.toString())
                    //showDialog("Forgot Password", "Try Again, Reset Failure")
                    progressBAR.visibility = View.INVISIBLE
                }
            }
        }
    }

    // utility--------------------------------------------------------------------------------------------
    private fun showDialog(title: String, descr:String) {
        // Create an alertdialog builder object,
        // then set attributes that you want the dialog to have
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(descr)

        builder.setPositiveButton("OK"){ dialog, which ->
            // code to run when YES is pressed
        }

        // create the dialog and show it
        val dialog = builder.create()
        dialog.show()
    }

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