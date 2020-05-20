package com.sahu.quizapp.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sahu.quizapp.Adapter.PostAdapter
import com.sahu.quizapp.AddPostActivity
import com.sahu.quizapp.LoginActivity

import com.sahu.quizapp.R
import com.sahu.quizapp.utils.Posts
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var postAdapter : PostAdapter ?= null
    private var postList : MutableList<Posts> ?= null
    private var followinglist : MutableList<Posts> ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_dashboard, container, false)
        view.add_post_dashboard.setOnClickListener {
            val intent = Intent(context, AddPostActivity::class.java)
            startActivity(intent)
        }
        var recyclerView : RecyclerView ? = null
        recyclerView = view.findViewById(R.id.dashboard_recycler_view)
        val linearlayoutmanager = LinearLayoutManager(context)
        linearlayoutmanager.reverseLayout = true
        linearlayoutmanager.stackFromEnd = true
        recyclerView.layoutManager = linearlayoutmanager


        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Posts>) }
        recyclerView.adapter = postAdapter

        checkfollowing()

        return view
    }

    private fun checkfollowing() {
        followinglist = ArrayList()
        val followingref = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("Following")
        followingref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    (followinglist as ArrayList<String>).clear()
                    for (snapshot in p0.children){
                        snapshot.key?.let { (followinglist as ArrayList<String>).add(it) }
                    }
                    retrieveposts()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun retrieveposts() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                postList?.clear()
                for(snapshot in p0.children){
                    val post = snapshot.getValue(Posts::class.java)
                    for(id in (followinglist as ArrayList<String>)){
                        if(post!!.getPublisher() == id){
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
