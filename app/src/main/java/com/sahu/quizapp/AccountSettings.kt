package com.sahu.quizapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.sahu.quizapp.utils.UserDetails
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.search_user_view.*
import java.util.HashMap

class AccountSettings : AppCompatActivity() {

    lateinit var firebaseUser: FirebaseUser
    private var checker:String ?= null
    lateinit var myUrl:String
    lateinit var imageUri:Uri
    lateinit var storageProfilePicRef : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        btnlogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettings, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        add_icon_acc_settings.setOnClickListener{
            checker = "clicked"
            CropImage.activity().setAspectRatio(1, 1)
                .start(this@AccountSettings)

        }
        acc_settings_check.setOnClickListener{
            if(checker == "clicked"){
                val f1 = validateName(full_name_acc_settings.editText?.text.toString())
                val f2 = validateUserName(username_acc_settings.editText?.text.toString())
                val f3 = validateBio(bio_acc_settings.editText?.text.toString())
                val f4 = validatePhoneno(phoneno_acc_settings.editText?.text.toString())
                var f5 = true
                if(imageUri == null){
                    f5 = false
                }
                if(!f5){
                    Toast.makeText(this@AccountSettings, "Please, select an Image first", Toast.LENGTH_SHORT).show()
                }
                if(f1 && f2 && f3 && f4 && f5) {
                    val progressdialog = ProgressDialog(this)
                    progressdialog.setTitle("Account Settings")
                    progressdialog.setMessage("Please wait, your profile is being updated")
                    progressdialog.show()
                    val fileref = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")
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
                                val ref = FirebaseDatabase.getInstance().reference.child("Users")
                                val userMap = HashMap<String, Any?>()
                                userMap["Username"] = username_acc_settings.editText?.text.toString().toLowerCase()
                                userMap["PhoneNo"] = phoneno_acc_settings.editText?.text.toString()
                                userMap["Name"] = full_name_acc_settings.editText?.text.toString().toLowerCase()
                                userMap["Bio"] = bio_acc_settings.editText?.text.toString()
                                userMap["Image"] = myUrl
                                ref.child(firebaseUser.uid).updateChildren(userMap)
                                Toast.makeText(this@AccountSettings, "Account updated successfully", Toast.LENGTH_SHORT).show()
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
            else{
                val f1 = validateName(full_name_acc_settings.editText?.text.toString())
                val f2 = validateUserName(username_acc_settings.editText?.text.toString())
                val f3 = validateBio(bio_acc_settings.editText?.text.toString())
                val f4 = validatePhoneno(phoneno_acc_settings.editText?.text.toString())
                if(f1 && f2 && f3 && f4) {
                    val useref = FirebaseDatabase.getInstance().reference.child("Users")
                    val userMap = HashMap<String, Any?>()
                    userMap["Username"] = username_acc_settings.editText?.text.toString().toLowerCase()
                    userMap["PhoneNo"] = phoneno_acc_settings.editText?.text.toString()
                    userMap["Name"] = full_name_acc_settings.editText?.text.toString().toLowerCase()
                    userMap["Bio"] = bio_acc_settings.editText?.text.toString()
                    useref.child(firebaseUser.uid).updateChildren(userMap)
                    Toast.makeText(this@AccountSettings, "Account updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        userinfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            acc_setting_pro_image.setImageURI(imageUri)
        }
    }

    fun validateName(st:String):Boolean {
        if (st.isEmpty()) {
            full_name_acc_settings.error = "Field cannot be empty";
            return false;
        }
        else {
            full_name_acc_settings.error = null
            full_name_acc_settings.isErrorEnabled = false
            return true
        }
    }
    fun validateUserName(st:String):Boolean {
        val noWhiteSpace = Regex("\\A\\w{4,20}\\z")
        if (st.isEmpty()) {
            username_acc_settings.error = "Field cannot be empty"
            return false
        }
        else if(st.length >=15 )
        {
            username_acc_settings.error = "User Name too long"
            return false
        }
        else if(!st.matches(noWhiteSpace))
        {
            username_acc_settings.error = "White Spaces Not Allowed"
            return false
        }
        else {
            username_acc_settings.error = null
            username_acc_settings.isErrorEnabled = false
            return true
        }
    }
    fun validateBio(st:String):Boolean {
        if (st.isEmpty()) {
            bio_acc_settings.error = "Field cannot be empty";
            return false;
        }
        else {
            bio_acc_settings.error = null
            bio_acc_settings.isErrorEnabled = false
            return true
        }
    }

    fun validatePhoneno(st:String):Boolean{
        if (st.isEmpty()) {
            phoneno_acc_settings.error = "Field cannot be empty"
            return false
        }
        else if(st.length!=10)
        {
            phoneno_acc_settings.error = "Phone Number should be of 10 digits"
            return false
        }
        else {
            phoneno_acc_settings.error = null
            phoneno_acc_settings.isErrorEnabled = false
            return true
        }
    }

    private fun userinfo(){
        val userref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
        userref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue<UserDetails>(UserDetails::class.java)
                    if (user != null) {
                        Picasso.get().load(user.getImage()).placeholder(R.drawable.defautl_profile_image).into(
                            acc_setting_pro_image
                        )
                        username_acc_settings.editText?.setText(user.getUsername())
                        //username_acc_settings.isHintAnimationEnabled = false
                        full_name_acc_settings.editText?.setText(user.getName())
                        phoneno_acc_settings.editText?.setText(user.getPhoneNo())
                        bio_acc_settings.editText?.setText(user.getBio())
                    }
                }
                else{
                    Toast.makeText(this@AccountSettings, "User doesnot exist", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

}
