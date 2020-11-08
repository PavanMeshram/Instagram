package com.example.instagram

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.instagram.Fragments.HomeFragment
import com.example.instagram.Fragments.NotificationsFragment
import com.example.instagram.Fragments.ProfileFragment
import com.example.instagram.Fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    //private lateinit var textView: TextView
    //internal var selectedFragment: Fragment? = null

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    moveToFragment(HomeFragment())
                    //textView.setText("Home")
                    return@OnNavigationItemSelectedListener true
                    //selectedFragment = HomeFragment()
                }
                R.id.nav_search -> {
                    moveToFragment(SearchFragment())
                    //textView.setText("Search")
                    return@OnNavigationItemSelectedListener true
                    //selectedFragment = SearchFragment()
                }
                R.id.nav_add_post -> {
                    //item.isChecked = false
                    //startActivity(Intent(this, AddPostActivity::class.java))
                    //textView.setText("Add Post")
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_notifications -> {
                    moveToFragment(NotificationsFragment())
                    //textView.setText("Notification")
                    return@OnNavigationItemSelectedListener true
                    //selectedFragment = NotificationsFragment()
                }
                R.id.nav_profile -> {
                    moveToFragment(ProfileFragment())
                    //textView.setText("Profile")
                    return@OnNavigationItemSelectedListener true
                    //selectedFragment = ProfileFragment()
                }
            }

            /*if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    selectedFragment!!
                ).commit()
            }*/
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        //textView = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        moveToFragment(HomeFragment())
        /*supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            HomeFragment()
        ).commit()*/
    }

    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }
}