package com.example.footballapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


class ArticleListFragment : Fragment() {

    private val TAG = "ArticleListFragment"

    lateinit var recyclerView: RecyclerView
    private val dataArray = ArrayList<ArticleData>()
    lateinit var articleAdapter: ArticleAdapter

    lateinit var firebaseDB: FirebaseDatabase
    lateinit var articleRef: DatabaseReference

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
        val data = ArticleData("Sports Article","https://www.youtube.com/watch?v=A95oQi66JIk")
        val data2 = ArticleData("Sports Article two","https://www.youtube.com/watch?v=A95oQi66JIk")

        //dataArray.add(data)
        //dataArray.add(data2)
        //dataArray.add(data)
        //dataArray.add(data2)
        //dataArray.add(data)
        //dataArray.add(data2)
        val root = inflater.inflate(R.layout.fragment_article_list, container, false)

        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL,false)

        articleAdapter = ArticleAdapter(requireContext(),dataArray)
        //recyclerView.adapter = customAdapter

        //--------------- READ DATA ---------------------------------------------------------------

        // set up realtime database
        firebaseDB = FirebaseDatabase.getInstance()
        articleRef = firebaseDB.getReference("Article")

        articleRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists()) {
                        val children = dataSnapshot.children
                        children.forEach { it ->
                            val data = it.getValue(ArticleData::class.java)
                            Log.d(TAG, "Data: ${data?.title}, ${data?.url}")
                            dataArray.add(data!!)
                        }
                    }

                    Log.d(TAG, "Data: ${dataArray.size}")
                    //tipsAdapter.notifyDataSetChanged()
                    recyclerView.adapter = articleAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })



        return root
    }

}