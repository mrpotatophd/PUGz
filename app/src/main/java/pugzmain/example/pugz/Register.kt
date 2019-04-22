package pugzmain.example.pugz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import com.example.pugz.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        loginBtn.setOnClickListener {
            val intent = Intent(this, Login:: class.java)
            startActivity(intent)
        }

        registerBtn.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if(email != "" && password != "" && firstNameText.text.toString() != "" && lastNameText.text.toString() != "") {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener

                        // else if successful
                        Log.d("Register", "Successfully created user with uid: ${it.result.user.uid}")
                        saveUserToDatabase()
                    }
            }
            else
            {
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

                if(firstNameText.text.toString() == "")
                {
                    firstNameText.setBackgroundResource(R.drawable.rounded_text_red)
                } else {
                    firstNameText.setBackgroundResource(R.drawable.rounded_text)
                }

                if(lastNameText.text.toString() == "") {
                    lastNameText.setBackgroundResource(R.drawable.rounded_text_red)
                } else {
                    lastNameText.setBackgroundResource(R.drawable.rounded_text)
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
                val intent = Intent(this, Portal:: class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.addGame -> {
                println("Add Game pressed")
                val intent = Intent(this, AddGames:: class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.profile -> {
                println("profile pressed")
                val intent = Intent(this, Profile:: class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false

    }

    private fun saveUserToDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("users")

        /*val user = User(
            uid,
            firstNameText.text.toString(),
            lastNameText.text.toString(),
            emailText.text.toString(),
            0
        )*/

        ref.child(uid!!).child("email").setValue(emailText.text.toString())
            .addOnSuccessListener {
                ref.child(uid!!).child("fname").setValue(firstNameText.text.toString())
                    .addOnSuccessListener {
                        ref.child(uid!!).child("lname").setValue(lastNameText.text.toString())
                            .addOnSuccessListener {
                                ref.child(uid!!).child("num_games").setValue("0")
                                    .addOnSuccessListener {
                                        ref.child(uid!!).child("uid").setValue(uid!!)
                                            .addOnSuccessListener {
                                                Log.d("Register", "User saved to database")
                                                val intent = Intent(this, Login:: class.java)
                                                startActivity(intent)
                                            }
                                            .addOnFailureListener {
                                                Log.d("Register", "Failed to save uid to database $uid")
                                            }
                                    }
                                    .addOnFailureListener {
                                        Log.d("Register", "Failed to save num_games to database $uid")
                                    }
                            }
                            .addOnFailureListener {
                                Log.d("Register", "Failed to save lname to database $uid")
                            }
                    }
                    .addOnFailureListener {
                        Log.d("Register", "Failed to save fname to database $uid")
                    }
            }
            .addOnFailureListener {
                Log.d("Register", "Failed to save email to database $uid")
            }
    }

    //class User(val uid: String, val fName: String, val lName:String, val email:String, val num_games:Int)
}
