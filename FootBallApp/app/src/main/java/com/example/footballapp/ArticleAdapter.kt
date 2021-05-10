package com.example.footballapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView

class ArticleAdapter(val context: Context, private val articles : ArrayList<ArticleData>):RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
    // val context: Context,
    //var viewModel = ViewModelProvider(parentLayout).get(ArticleViewModel::class.java)

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        val articleTitle = itemView.findViewById<TextView>(R.id.tv_article_title)
        val articleURL = itemView.findViewById<TextView>(R.id.tv_article_url)

        init {
            itemView.setOnClickListener { v->
                val selectedItem = adapterPosition
                val bundle = Bundle()

                val myMessage = articles[selectedItem].url
                bundle.putString("URL", myMessage)
                WebViewFragment().arguments = bundle

                val activity: AppCompatActivity = v?.context as AppCompatActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, WebViewFragment()).commit()
                //.addToBackStack(null)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        // create the new views, inflate the recycler  view by adding row items
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_row_item,parent,false)


        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentItem = articles[position]

        holder.articleTitle.text = currentItem.title
        //holder.articleTitle.setOnClickListener {
        //}
        holder.articleURL.text = "Read more here..... "
    }

    override fun getItemCount(): Int {
        return articles.size
    }
}