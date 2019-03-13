package com.example.pugz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*

class Profile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if(FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this, Login :: class.java)
            startActivity(intent)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        logoutButton.setOnClickListener {
            val loggedInUser = FirebaseAuth.getInstance()

            loggedInUser.signOut()
            val intent = Intent(this, Login :: class.java)
            startActivity(intent)
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.more -> {
                println("more pressed")
                return@OnNavigationItemSelectedListener true
            }
            R.id.home -> {
                println("home pressed")
                val intent = Intent(this, Portal :: class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.profile -> {
                println("profile pressed")
                val intent = Intent(this, Profile :: class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false

    }
}
