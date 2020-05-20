package com.sahu.quizapp

import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class SignupActivity : AppCompatActivity() {

    lateinit var signup_image: ImageView
    lateinit var signup_text: TextView
    lateinit var signup2_text: TextView
    lateinit var reg_btn: Button
    lateinit var reg_login_btn: Button
    lateinit var reg_name: TextInputLayout
    lateinit var reg_username: TextInputLayout
    lateinit var reg_email: TextInputLayout
    lateinit var reg_phoneNo: TextInputLayout
    lateinit var reg_password: TextInputLayout

    lateinit var rootNode: FirebaseDatabase
    lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        signup_image = findViewById(R.id.signup_image)
        signup_text = findViewById(R.id.signup_text)
        signup2_text = findViewById(R.id.signup2_text)
        reg_btn = findViewById(R.id.reg_btn)
        reg_login_btn = findViewById(R.id.reg_login_btn)
        reg_name = findViewById(R.id.reg_name)
        reg_username = findViewById(R.id.reg_username)
        reg_email = findViewById(R.id.reg_email)
        reg_phoneNo = findViewById(R.id.reg_phoneNo)
        reg_password = findViewById(R.id.reg_password)

        fun validateName(st:String):Boolean {
            if (st.isEmpty()) {
                reg_name.error = "Field cannot be empty";
                return false;
            }
            else {
                reg_name.error = null
                reg_name.isErrorEnabled = false
                return true
            }
        }
        fun validateUserName(st:String):Boolean {
            val noWhiteSpace = Regex("\\A\\w{4,20}\\z")
            if (st.isEmpty()) {
                reg_username.error = "Field cannot be empty"
                return false
            }
            else if(st.length >=15 )
            {
                reg_username.error = "User Name too long"
                return false
            }
            else if(!st.matches(noWhiteSpace))
            {
                reg_username.error = "White Spaces Not Allowed"
                return false
            }
            else {
                reg_username.error = null
                reg_username.isErrorEnabled = false
                return true
            }
        }
        fun validateEmail():Boolean{
            val st = reg_email.editText?.text.toString()
            val pattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
            if(st.isEmpty())
            {
                reg_email.error = "Field Cannot be Empty"
                return false
            }

            else if(!st.matches(pattern))
            {
                reg_email.error = "Email address not valid"
                return false
            }
            else
            {
                reg_email.error = null
                reg_email.isErrorEnabled = false
                return true
            }
        }
        fun validatePhoneno(st:String):Boolean{
            if (st.isEmpty()) {
                reg_phoneNo.error = "Field cannot be empty"
                return false
            }
            else if(st.length!=10)
            {
                reg_phoneNo.error = "Phone Number should be of 10 digits"
                return false
            }
            else {
                reg_phoneNo.error = null
                reg_phoneNo.isErrorEnabled = false
                return true
            }
        }
        fun validatePassword(st:String):Boolean{
            val pattern  = Regex("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$")
            if (st.isEmpty()) {
                reg_password.error = "Field cannot be empty"
                return false
            }
            else if(!st.matches(pattern))
            {
                reg_password.error = "Password is too weak"
                return false
            }
            else {
                reg_password.error = null
                reg_password.isErrorEnabled = false
                return true
            }
        }




        reg_login_btn.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            val p1: Pair<View, String> = Pair(signup_image, "logo_image")
            val p2: Pair<View, String> = Pair(signup_text, "logo_text")
            val p3: Pair<View, String> = Pair(signup2_text, "logo_desc")
            val p4: Pair<View, String> = Pair(reg_btn, "button_tran")
            val options = ActivityOptions.makeSceneTransitionAnimation(this, p1, p2, p3, p4)
            startActivity(intent, options.toBundle())
            finish()
        }
        reg_btn.setOnClickListener {
            rootNode = FirebaseDatabase.getInstance()
            reference = rootNode.getReference("Users")
            val name = reg_name.editText?.text.toString()
            val username = reg_username.editText?.text.toString()
            val email = reg_email.editText?.text.toString()
            val phoneNo = reg_phoneNo.editText?.text.toString()
            val password = reg_password.editText?.text.toString()
            val f1 = validateName(name)
            val f2 = validateUserName(username)
            val f3 = true
            val f4 = validatePhoneno(phoneNo)
            val f5 = validatePassword(password)
            if(!f1 || !f2 || !f3 || !f4 || !f5)
            {
                Toast.makeText(this, "Some Error Occured", Toast.LENGTH_SHORT).show()
            }
            else {
                val progressdialog = ProgressDialog(this@SignupActivity)
                progressdialog.setTitle("Signing Up")
                progressdialog.setMessage("Patience is the key to success")
                progressdialog.setCanceledOnTouchOutside(false)
                progressdialog.show()
                val mAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task->
                        if(task.isSuccessful){
                            saveUserInfo(name, username, phoneNo, email, progressdialog)
                        }
                        else{
                            val message = task.exception.toString()
                            Toast.makeText(this, "Error : $message" , Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressdialog.dismiss()
                        }

                    }
            }
        }

    }

    private fun saveUserInfo(name: String, username: String, phoneNo: String, email: String, progressDialog:ProgressDialog)
    {
        val currentUserId = FirebaseAuth.getInstance().uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any?>()
        userMap["UserId"] = currentUserId
        userMap["Name"] = name.toLowerCase()
        userMap["Username"] = username.toLowerCase()
        userMap["PhoneNo"] = phoneNo
        userMap["Email"] = email
        userMap["Bio"] = "Hey there!"
        if (currentUserId != null) {
            userRef.child(currentUserId).setValue(userMap)
                .addOnCompleteListener {task->
                    if(task.isSuccessful)
                    {
                        progressDialog.dismiss()
                        FirebaseDatabase.getInstance().reference.child("Follow").
                            child(currentUserId).child("Following").child(currentUserId)
                            .setValue(true)
                        Toast.makeText(
                            this,
                            "Account Created SuccessFully",
                            Toast.LENGTH_LONG).show()
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this, "Some Error Occured", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }

        }
    }

}

