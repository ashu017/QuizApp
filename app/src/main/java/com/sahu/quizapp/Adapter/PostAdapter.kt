package com.sahu.quizapp.Adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sahu.quizapp.DashboardActivity
import com.sahu.quizapp.R
import com.sahu.quizapp.utils.Posts
import com.sahu.quizapp.utils.UserDetails
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.single_post.view.*
import org.w3c.dom.Text

class PostAdapter(private val mContext:Context, private val mPost : List<Posts>) : RecyclerView.Adapter<PostAdapter.viewholder>() {

    private var firebaseuser : FirebaseUser ?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.single_post, parent, false)
        return viewholder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        firebaseuser = FirebaseAuth.getInstance().currentUser!!
        val post = mPost[position]
        Picasso.get().load(post.getPostImage()).into(holder.postImage)
        if(post.getDescription()==""){
            holder.description.visibility = View.GONE
        }
        else{
            holder.description.visibility = View.VISIBLE
            holder.description.text = post.getDescription()
        }
        publisherinfo(holder.profileimage, holder.username, holder.publisher, post.getPublisher())
        numberoflikes(holder.likes, post.getPostId())
        isLiked(post.getPostId(), holder.likebutton)
        holder.likebutton.setOnClickListener {
            if(holder.likebutton.tag == "Like"){
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post.getPostId()).child(firebaseuser!!.uid)
                    .setValue(true)
            }
            else{
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post.getPostId()).child(firebaseuser!!.uid)
                    .removeValue()
                val intent = Intent(mContext, DashboardActivity::class.java)
                mContext.startActivity(intent)
            }
        }
    }

    private fun numberoflikes(likes: TextView, postId: String) {
        val likesref = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)
        likesref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    likes.text = p0.childrenCount.toString() + "Likes"
                }
            }

        })
    }

    private fun isLiked(postId: String, likebutton: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likesref = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)
        likesref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (firebaseUser != null) {
                    if(p0.child(firebaseUser.uid).exists()){
                        likebutton.setImageResource(R.drawable.ic_liked)
                        likebutton.tag = "Liked"
                    }
                    else{
                        likebutton.setImageResource(R.drawable.ic_not_liked)
                        likebutton.tag = "Like"
                    }
                }
            }

        })
    }


    class viewholder(@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){
        var profileimage : CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
        var likebutton : ImageView = itemView.findViewById(R.id.post_image_like_btn)
        var postImage : ImageView = itemView.findViewById(R.id.post_image_home)
        var commentbutton : ImageView = itemView.findViewById(R.id.post_image_comment_btn)
        var savebutton : ImageView = itemView.findViewById(R.id.post_save_comment_btn)
        var username : TextView = itemView.findViewById(R.id.user_name_search)
        var likes : TextView = itemView.findViewById(R.id.likes)
        var comments : TextView = itemView.findViewById(R.id.comments)
        var description : TextView = itemView.findViewById(R.id.description)
        var publisher : TextView = itemView.findViewById(R.id.publisher)
    }

    private fun publisherinfo(profileimage: CircleImageView, username: TextView, publisher: TextView, publisher1: String) {
        val userref = FirebaseDatabase.getInstance().reference.child("Users").child(publisher1)
        userref.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue<UserDetails>(UserDetails::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.defautl_profile_image).into(profileimage)
                    username.text = user.getUsername()
                    publisher.text = user.getName()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}