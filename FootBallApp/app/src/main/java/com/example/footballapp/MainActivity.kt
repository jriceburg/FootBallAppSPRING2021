package com.example.footballapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    lateinit var nameDashBoard : TextView

    private lateinit var mAuth: FirebaseAuth
    //private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var firebaseDB: FirebaseDatabase
    private lateinit var userRef: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide() // hide action bar
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        nameDashBoard = findViewById(R.id.dashBoardName)

        // set up realtime database
        firebaseDB = FirebaseDatabase.getInstance()
        userRef = firebaseDB.getReference("Users")

        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        userRef.child(userID).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData: UserSignUpData? = dataSnapshot.getValue(UserSignUpData::class.java)
                Log.d(TAG, "username is: ${userData?.firstNameData}")
                nameDashBoard.text = "Welcome ${userData?.firstNameData}"
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })






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

    fun toArticle(view: View) {
        val intentArticle = Intent(this, ArticleActivity::class.java)
        startActivity(intentArticle)
    }

    fun toUserActivity(view: View) {
        val intentArticle = Intent(this, UserActivity::class.java)
        startActivity(intentArticle)
    }


    private fun startLoginUser() {
        val intentRegister = Intent(this, LogInActivity::class.java)
        startActivity(intentRegister)
        finish()
        // security related, we want the user to not have access to main activity
        // without login/sign up so main activity does not go to the call bak stack
    }

}