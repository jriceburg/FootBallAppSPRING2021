package com.example.footballapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var mAuth: FirebaseAuth
    //private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var firebaseDB: FirebaseDatabase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide() // hide action bar
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


    }

    fun logout(view: View) {

        // User chose the "logout" item, logout the user then
        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        mAuth.signOut()
        /**
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.e(TAG, "Google Sign out successful ")
                Toast.makeText(this, "Logout: Google", Toast.LENGTH_SHORT).show()
            } else {
                Log.e(TAG, "Google Sign out is not successful:${task.exception} ")
            }

        }
        */
        if (mAuth.currentUser == null) {
            startLoginUser()
        } else {
            Log.e(TAG, "Sign out is not successful ")
        }
    }

    fun toTwitter(view: View) {
        val intentTwitter = Intent(this, TwitterActivity::class.java)
        startActivity(intentTwitter)
    }


    private fun startLoginUser() {
        val intentRegister = Intent(this, LogInActivity::class.java)
        startActivity(intentRegister)
        finish()
        // security related, we want the user to not have access to main activity
        // without login/sign up so main activity does not go to the call bak stack
    }

}