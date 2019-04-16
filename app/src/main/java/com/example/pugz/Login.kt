package com.example.pugz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if(FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, Profile :: class.java)
            startActivity(intent)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        signUpBtn.setOnClickListener {
            val intent = Intent(this, Register :: class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if(email != "" && password != "") {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener

                        // else if successful
                        Log.d("Login", "Successfully login user with uid: ${it.result.user.uid}")
                        val intent = Intent(this, Portal::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        emailText.setBackgroundResource(R.drawable.rounded_text_red)
                        passwordText.setBackgroundResource(R.drawable.rounded_text_red)
                    }
            } else {
                if(email == "")
                {
                    emailText.setBackgroundResource(R.drawable.rounded_text_red)
                } else {
                    emailText.setBackgroundResource(R.drawable.rounded_text)
                }

                if(password == "")
                {
                    passwordText.setBackgroundResource(R.drawable.rounded_text_red)
                } else {
                    passwordText.setBackgroundResource(R.drawable.rounded_text)
                }
            }
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            /*R.id.more -> {
                println("more pressed")
                return@OnNavigationItemSelectedListener true
            }*/
            R.id.home -> {
                println("home pressed")
                val intent = Intent(this, Portal :: class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.addGame -> {
                println("Add Game pressed")
                val intent = Intent(this, AddGames :: class.java)
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
