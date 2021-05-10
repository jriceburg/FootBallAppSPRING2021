package com.example.footballapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserActivity : AppCompatActivity() {

    private val TAG = "UserActivity"

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseDB: FirebaseDatabase
    private lateinit var userRef: DatabaseReference

    lateinit var lastName: EditText
    lateinit var firstName: EditText
    lateinit var age: EditText
    lateinit var team: EditText
    var email = " "

    lateinit var user: UserSignUpData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        supportActionBar?.hide()

        firstName = findViewById(R.id.et_first_name_update)
        lastName = findViewById(R.id.et_last_name_update)
        age = findViewById(R.id.et_age_update)
        team = findViewById(R.id.et_team_update)


        // set up realtime database
        firebaseDB = FirebaseDatabase.getInstance()
        userRef = firebaseDB.getReference("Users")

        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        userRef.child(userID).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData: UserSignUpData? = dataSnapshot.getValue(UserSignUpData::class.java)
                Log.d(TAG, "first name is: ${userData?.firstNameData}")
                Log.d(TAG, "last name is: ${userData?.lastNameData}")
                user = userData!!
                firstName.setText(userData.firstNameData)
                lastName.setText(userData.lastNameData)
                //age.setText(userData.age)
                //team.setText(userData.team)
                email = userData.emailData!!

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

    }


    fun updateSave(view: View) {
        if ((firstName.text.isNullOrEmpty()) || (lastName.text.isNullOrEmpty()) || (age.text.isNullOrEmpty()) || (team.text.isNullOrEmpty())) {

        } else {

            Toast.makeText(this, "age: ${age.text} team: ${team.text}", Toast.LENGTH_SHORT).show()


            val uID = FirebaseAuth.getInstance().currentUser?.uid
            val dbRef = FirebaseDatabase.getInstance().getReference("Users")

            val updateMap = mapOf(
                "age" to age.text.toString(),
                "emailData" to email,
                "firstNameData" to firstName.text.toString(),
                "lastNameData" to lastName.text.toString(),
                "team" to team.text.toString()
            )

            dbRef.child(uID!!).updateChildren(updateMap).addOnSuccessListener {
                Log.d(TAG, "User data added to DB user: $uID")
                Toast.makeText(this, "Db update good", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this, "User data write failed.", Toast.LENGTH_SHORT).show()
            }


        }
    }


    fun toMainActivity(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

}