package com.example.pugz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        loginBtn.setOnClickListener {
            val intent = Intent(this, Login :: class.java)
            startActivity(intent)
        }

        registerBtn.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // else if successful
                    Log.d("Register", "Successfully created user with uid: ${it.result.user.uid}")
                    saveUserToDatabase()
                }
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

    private fun saveUserToDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().reference

        val user = User(uid, firstNameText.text.toString(), lastNameText.text.toString(), emailText.text.toString())

        ref.child("users").child(uid).setValue(user)
            .addOnSuccessListener {
                Log.d("Register", "User saved to database")
                val intent = Intent(this, Login :: class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("Register", "Failed to save data to database $uid")
            }
    }

    class User(val uid: String, val fName: String, val lName:String, val email:String)
}
