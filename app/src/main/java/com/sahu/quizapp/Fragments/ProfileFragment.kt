package com.sahu.quizapp.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sahu.quizapp.AccountSettings

import com.sahu.quizapp.R
import com.sahu.quizapp.utils.UserDetails
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var profileId : String

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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("Pref", Context.MODE_PRIVATE)
        if(pref!=null){
            this.profileId = pref.getString("profileId","none").toString()
        }
        if(profileId == firebaseUser.uid){
            view.btn_edit_acc.text = "Edit Profile"
        }
        else if(profileId != firebaseUser.uid){
            checkflwandflwngbtnstatus()
        }

        view.btn_edit_acc.setOnClickListener {
            val getbtntext = view.btn_edit_acc.text.toString()
            when{
                getbtntext == "Edit Profile" ->{
                    val intent = Intent(context, AccountSettings::class.java)
                    startActivity(intent)
                }
                getbtntext == "Follow" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("Follow").child(it1.toString())
                            .child("Following").child(profileId).setValue(true)
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)
                            .child("Followers").child(it1.toString()).setValue(true)
                    }
                }
                getbtntext == "Following" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("Follow").child(it1.toString())
                            .child("Following").child(profileId).removeValue()
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)
                            .child("Followers").child(it1.toString()).removeValue()
                    }
                }
            }

        }
        getfollowers()
        getfollowing()
        userinfo()
        return view
    }

    private fun checkflwandflwngbtnstatus() {
        val followingRef =
            FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)
                .child("Following")
        followingRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    if(p0.child(profileId).exists()){
                        view?.btn_edit_acc?.text = "Following"
                    }
                    else{
                        view?.btn_edit_acc?.text = "Follow"
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getfollowers(){
        val followerRef = FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)
                .child("Followers")
        followerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    view?.total_followers?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getfollowing(){
        val followerRef = FirebaseDatabase.getInstance().reference.child("Follow").child(profileId)
                .child("Following")
        followerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    view?.total_following?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private fun userinfo(){
        val userref = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        userref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
//                if(context!=null){
//
//                }
                if(p0.exists()){
                    val user = p0.getValue<UserDetails>(UserDetails::class.java)
                    if (user != null) {
                        Picasso.get().load(user.getImage()).placeholder(R.drawable.defautl_profile_image).into(
                            view?.dp_profile_fragment
                        )
                        view?.full_name_prof_frag?.text = user.getName()
                        view?.bio_prof_frag?.text = user.getBio()

                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("Pref", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("Pref", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("Pref", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
