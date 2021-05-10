package com.example.footballapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.res.Configuration
import android.view.View
import androidx.lifecycle.ViewModelProvider

class ArticleActivity : AppCompatActivity() {

    private val TAG = "ArticleActivity"

    lateinit var viewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        supportActionBar?.hide()

        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainContainer, ArticleListFragment())

        // add the fragments to backstack so that the user may go back the previous state with back button
        transaction.addToBackStack(null).commit()

    }

    fun toMainActivity2(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}