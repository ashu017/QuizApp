package com.sahu.quizapp

import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var logo_image:ImageView
    lateinit var logo_name:TextView
    lateinit var slogan_name:TextView
    lateinit var username:TextInputLayout
    lateinit var password : TextInputLayout
    private lateinit var btnforgot_password:Button
    lateinit var btnlogin:Button
    lateinit var btnsign_up:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        logo_image = findViewById(R.id.logo_image)
        logo_name = findViewById(R.id.logo_name)
        slogan_name = findViewById(R.id.slogan_name)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        btnforgot_password = findViewById(R.id.btnforgot_password)
        btnlogin = findViewById(R.id.btnlogin)
        btnsign_up = findViewById(R.id.btnsign_up)

        fun validateUserName():Boolean {
            val noWhiteSpace = Regex("\\A\\w{4,20}\\z")
            val st = username.editText?.text.toString()
            if (st.isEmpty()) {
                username.error = "Field cannot be empty"
                return false
            }
            /*else if(st.length >=15 )
            {
                username.error = "Invalid Username"
                return false
            }
            else if(!st.matches(noWhiteSpace))
            {
                username.error = "Invalid Username"
                return false
            }*/
            else {
                username.error = null
                username.isErrorEnabled = false
                return true
            }
        }

        fun validatePassword():Boolean{
            val st = password.editText?.text.toString()
            if (st.isEmpty()) {
                password.error = "Field cannot be empty"
                return false
            }
            else {
                password.error = null
                password.isErrorEnabled = false
                return true
            }
        }

        fun isUser(){
            val emailentered = username.editText?.text.toString()
            val passwordentered = password.editText?.text.toString()
            val progressdialog = ProgressDialog(this@LoginActivity)
            progressdialog.setTitle("Logging In")
            progressdialog.setMessage("Patience is the key to success")
            progressdialog.setCanceledOnTouchOutside(false)
            progressdialog.show()
            val mAuth = FirebaseAuth.getInstance()
            val currentUser = mAuth.currentUser
            mAuth.signInWithEmailAndPassword(emailentered, passwordentered)
                .addOnCompleteListener { task->
                    if(task.isSuccessful)
                    {
                        progressdialog.dismiss()
                        username.error = null
                        username.isErrorEnabled = false
                        Toast.makeText(this, "Logged in successFully", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        username.error = "Signup first"
                        Toast.makeText(this, "Please sugnup first" , Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressdialog.dismiss()
                    }
                }
        }
        btnsign_up.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            val p1:Pair<View, String> = Pair(logo_image, "logo_image")
            val p2:Pair<View, String> = Pair(logo_name, "logo_text")
            val p3:Pair<View, String> = Pair(slogan_name, "logo_desc")
            val p4:Pair<View, String> = Pair(btnlogin, "button_tran")
            val options = ActivityOptions.makeSceneTransitionAnimation(this, p1, p2, p3, p4)
            startActivity(intent,options.toBundle())
        }
        btnlogin.setOnClickListener {
            val f1 = validateUserName()
            val f2 = validatePassword()
            if(!(!f1 || !f2))
            {
                isUser()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val mAuth = FirebaseAuth.getInstance()
        if(mAuth.currentUser!=null)
        {
            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
