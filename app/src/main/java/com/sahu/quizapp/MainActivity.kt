package com.sahu.quizapp

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var top_animation : Animation
    lateinit var bottom_animaation : Animation
    lateinit var imageView : ImageView
    lateinit var logo:TextView
    lateinit var slogan:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        top_animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.top_animation)
        bottom_animaation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.bottom_animation)
        imageView = findViewById(R.id.imageView)
        logo = findViewById(R.id.textView)
        slogan = findViewById(R.id.textView2)
        imageView.startAnimation(top_animation)
        logo.startAnimation(bottom_animaation)
        slogan.startAnimation(bottom_animaation)

        Handler().postDelayed({
            Handler().postDelayed({
                //imglogo.visibility = View.GONE
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                var pair1:Pair<View, String> = Pair(imageView, "load_image")
                var pair2:Pair<View, String> = Pair(logo, "logo_text")
                val options = ActivityOptions.makeSceneTransitionAnimation(this,pair1, pair2)
                startActivity(intent, options.toBundle())
                //startActivity(intent)
                finish()
            }, 1000)
        },2000)

    }
}
