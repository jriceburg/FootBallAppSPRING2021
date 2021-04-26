package com.example.footballapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.res.Configuration
import android.view.View
import androidx.lifecycle.ViewModelProvider

class ArticleActivity : AppCompatActivity() {

    lateinit var viewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        supportActionBar?.hide()

        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.mainContainer, ArticleListFragment())

        // add the fragments to backstack so that the user may go back the previous state with back button
        transaction.addToBackStack(null)

        // Commit the transaction to apply
        transaction.commit()


        // landscape
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.webContainer,WebViewFragment())
                .addToBackStack(null)
                .commit()

        }

        viewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)

        //viewModel = ViewModelProvider(requireActivity()).get(ArticleViewModel::class.java)

    }

    fun toMainActivity2(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}