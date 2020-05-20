package com.sahu.quizapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sahu.quizapp.Fragments.ProfileFragment
import com.sahu.quizapp.R
import com.sahu.quizapp.utils.UserDetails
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.search_user_view.view.*
import org.w3c.dom.Text
import java.util.function.BooleanSupplier

class UserAdapter(private var mContext:Context,
                  private var mUser:List<UserDetails>,
                  private var isfragment:Boolean = false)
    :RecyclerView.Adapter<UserAdapter.ViewHolder>(){
    private var firebaseuser : FirebaseUser ?= FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.search_user_view, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        holder.username.text = user.getUsername()
        holder.fullname.text = user.getName()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.defautl_profile_image).into(holder.userProfileImage)
        checkfollowingstatus(user.getUserId(), holder.followbtn)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val pref = mContext.getSharedPreferences("Pref", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.getUserId())
            pref.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.frame, ProfileFragment()).commit()
        })

        holder.followbtn.setOnClickListener {
            if(holder.followbtn.text.toString() == "Follow"){
                firebaseuser?.uid.let {it1->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it1.toString())
                        .child("Following").child(user.getUserId())
                        .setValue(true).addOnCompleteListener {task->
                            if(task.isSuccessful){
                                firebaseuser?.uid.let {it1->
                                    FirebaseDatabase.getInstance().reference.child("Follow").child(user.getUserId())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener {task->
                                            if(task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            }
            else{
                firebaseuser?.uid.let {it1->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it1.toString())
                        .child("Following").child(user.getUserId())
                        .removeValue().addOnCompleteListener {task->
                            if(task.isSuccessful){
                                firebaseuser?.uid.let {it1->
                                    FirebaseDatabase.getInstance().reference.child("Follow").child(user.getUserId())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener {task->
                                            if(task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }
    }
    class ViewHolder(@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){
        var fullname:TextView = itemView.findViewById(R.id.fullname_search)
        var username:TextView = itemView.findViewById(R.id.username_search)
        var userProfileImage : CircleImageView = itemView.findViewById(R.id.search_image)
        var followbtn : Button = itemView.findViewById(R.id.follow_search)
    }

    private fun checkfollowingstatus(userId: String, followbtn: Button) {
        val followingRef = firebaseuser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference.child("Follow").child(it1.toString())
                .child("Following")
        }
        followingRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.child(userId).exists()){
                    followbtn.text = "Following"
                }
                else
                {
                    followbtn.text = "Follow"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}