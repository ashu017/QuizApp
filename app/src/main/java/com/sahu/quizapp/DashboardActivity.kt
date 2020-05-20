package com.sahu.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.sahu.quizapp.Fragments.DashboardFragment
import com.sahu.quizapp.Fragments.LeaderboardFragment
import com.sahu.quizapp.Fragments.ProfileFragment
import com.sahu.quizapp.Fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_dashboard.*
import me.ibrahimsn.lib.SmoothBottomBar

class DashboardActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.item0 -> {
                moveToFragment(DashboardFragment())
                supportActionBar?.show()
                supportActionBar?.title = "Dashboard"
                return@OnNavigationItemSelectedListener true
            }
            R.id.item1 -> {
                moveToFragment(LeaderboardFragment())
                supportActionBar?.show()
                supportActionBar?.title = "LeaderBoard"
                return@OnNavigationItemSelectedListener true
            }
            R.id.item2 -> {
                moveToFragment(SearchFragment())
                supportActionBar?.hide()
                return@OnNavigationItemSelectedListener true
            }
            R.id.item3 -> {
                moveToFragment(ProfileFragment())
                supportActionBar?.show()
                supportActionBar?.title = "Profile"
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        /*btnsignout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }*/
        /* bottomBar.onItemSelected = {
            status.text = "Item $it selected"
        }

        bottomBar.onItemReselected = {
            status.text = "Item $it re-selected"
        }*/
        setUpToolbar()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


        moveToFragment(DashboardFragment())
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)      //to set the home button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // to set the home button
    }

    private fun moveToFragment(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.frame, fragment)
        fragmentTrans.commit()
        supportActionBar?.title = "Dashboard"
    }
}


