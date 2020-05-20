package com.sahu.quizapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_add_post.*
import java.util.HashMap

class AddPostActivity : AppCompatActivity() {

    lateinit var myUrl:String
    private var imageUri: Uri ?= null
    lateinit var storagePostPicRef : StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Post Pictures")
        add_post_check.setOnClickListener {
            uploadImage()
        }
        CropImage.activity().setAspectRatio(2, 1)
            .start(this@AddPostActivity)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            add_post_image.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {

        if(imageUri == null)
            Toast.makeText(this@AddPostActivity, "Please upload an Image first", Toast.LENGTH_SHORT).show()
        else if(TextUtils.isEmpty(add_post_edittxt.toString()))
            Toast.makeText(this@AddPostActivity, "Add some description please", Toast.LENGTH_SHORT).show()
        else{
            val progressdialog = ProgressDialog(this)
            progressdialog.setTitle("Adding Post")
            progressdialog.setMessage("Please wait, your post is being added")
            progressdialog.show()
            val fileref = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            var uploadTask: StorageTask<*>
            uploadTask = fileref.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw  it
                    }
                }
                return@Continuation fileref.downloadUrl
            }).addOnCompleteListener (
                OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()
                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postMap = HashMap<String, Any?>()
                        val postid = ref.push().key
                        postMap["PostId"] = postid
                        postMap["Description"] = add_post_edittxt?.text.toString()
                        postMap["Publisher"] = FirebaseAuth.getInstance().currentUser?.uid
                        postMap["PostImage"] = myUrl
                        if (postid != null) {
                            ref.child(postid).updateChildren(postMap)
                        }
                        Toast.makeText(this@AddPostActivity, "Post updated successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressdialog.dismiss()
                    }
                    else
                    {
                        progressdialog.dismiss()
                    }
                })
        }
    }

}
