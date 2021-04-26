package com.example.footballapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ArticleListFragment : Fragment() {

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val data = ArticleData("Sports Article","https://www.theguardian.com/football/2021/apr/26/premier-league-and-carabao-cup-10-talking-points-from-the-weekend")
        val data2 = ArticleData("Sports Article two","https://www.theguardian.com/us/sport")
        val dataArray = ArrayList<ArticleData>()
        dataArray.add(data)
        dataArray.add(data2)
        dataArray.add(data)
        dataArray.add(data2)
        dataArray.add(data)
        dataArray.add(data2)
        val root = inflater.inflate(R.layout.fragment_article_list, container, false)

        recyclerView = root.findViewById(R.id.recyclerView)
        val customAdapter = ArticleAdapter(dataArray)
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL,false)


        return root
    }


}