package pugzmain.example.pugz

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.graphics.Color
import android.widget.TextView
import com.example.pugz.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_games.*
import java.text.SimpleDateFormat
import java.util.*

class AddGames : AppCompatActivity() {

    var textview_date: TextView? = null;
    var textview_time: TextView? = null;
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_games)

        textview_date = this.dateTextView
        textview_date!!.text = "--/--/----"
        textview_time = this.timeTextView
        textview_time!!.text = "--:--"

        val dateSetListener = object : DatePickerDialog.OnDateSetListener{
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        textview_date!!.setOnClickListener {
            DatePickerDialog(this@AddGames,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        
        textview_time!!.setOnClickListener { 
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                textview_time!!.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        CreateGameBtn.setOnClickListener {
            if(FirebaseAuth.getInstance().currentUser == null) {
                val intent = Intent(this, Login:: class.java)
                startActivity(intent)
            }
            else {

                var sportOK = false
                var locationOK = false
                var timeOK = false
                var dateOK = false
                var buildingOK = false
                var roomOK = false

                if (sportSpinner.selectedItemPosition != 0) {
                    sportOK = true;
                    sportSpinner.setBackgroundResource(R.drawable.rounded_text)
                } else {
                    sportSpinner.setBackgroundResource(R.drawable.rounded_text_red)
                }

                if (locationSpinner.selectedItemPosition != 0) {
                    locationOK = true;
                    locationSpinner.setBackgroundResource(R.drawable.rounded_text)
                } else {
                    locationSpinner.setBackgroundResource(R.drawable.rounded_text_red)
                }

                //timeTextView
                if (textview_time!!.text != "--/--/----") {
                    timeOK = true
                    textview_time!!.setBackgroundResource(R.drawable.rounded_text)
                } else {
                    textview_time!!.setBackgroundResource(R.drawable.rounded_text_red)
                }

                //dateTextView
                if (textview_date!!.text != "--:--") {
                    dateOK = true
                    textview_date!!.setBackgroundResource(R.drawable.rounded_text)
                } else {
                    textview_date!!.setBackgroundResource(R.drawable.rounded_text_red)
                }

                //buildingTextView
                if (this.buildingText.text.toString() != "") {
                    buildingOK = true
                    this.buildingText.setBackgroundResource(R.drawable.rounded_text)
                } else {
                    this.buildingText.setBackgroundResource(R.drawable.rounded_text_red)
                }

                //roomTextView
                if (this.roomText.text.toString() != "") {
                    roomOK = true
                    this.roomText.setBackgroundResource(R.drawable.rounded_text)
                } else {
                    this.roomText.setBackgroundResource(R.drawable.rounded_text_red)
                }

                if (sportOK && locationOK && timeOK && dateOK && buildingOK && roomOK) {
                    addGameToDatabase();
                }
            }
        }

        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
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

    private fun updateDateInView() {
        val myFormat = "MM-dd-yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.time)
    }

    private fun addGameToDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().reference

        val dateData = this.dateTextView.text.toString()
        val timeData = this.timeTextView.text.toString()
        val gameUid = uid + "_" + dateData + "_" + timeData

        /*val game = Game(
            gameUid,
            uid,
            this.sportSpinner.selectedItem.toString(),
            this.locationSpinner.selectedItem.toString(),
            this.buildingText.text.toString(),
            this.dateTextView.text.toString(),
            this.timeTextView.text.toString(),
            this.roomText.text.toString()
        )*/

        ref.child("games").child(gameUid).child("building").setValue(this.buildingText.text.toString())
            .addOnSuccessListener {
                ref.child("games").child(gameUid).child("date").setValue(this.dateTextView.text.toString())
                    .addOnSuccessListener {
                        ref.child("games").child(gameUid).child("gameUid").setValue(gameUid)
                            .addOnSuccessListener {
                                ref.child("games").child(gameUid).child("location").setValue(this.locationSpinner.selectedItem.toString())
                                    .addOnSuccessListener {
                                        ref.child("games").child(gameUid).child("num_players").setValue("1")
                                            .addOnSuccessListener {
                                                ref.child("games").child(gameUid).child("players").child(uid).setValue("Player")
                                                    .addOnSuccessListener {
                                                        ref.child("games").child(gameUid).child("room").setValue(this.roomText.text.toString())
                                                            .addOnSuccessListener {
                                                                ref.child("games").child(gameUid).child("sport").setValue(this.sportSpinner.selectedItem.toString())
                                                                    .addOnSuccessListener {
                                                                        ref.child("games").child(gameUid).child("time").setValue(this.timeTextView.text.toString())
                                                                            .addOnSuccessListener {
                                                                                ref.child("games").child(gameUid).child("userUid").setValue(uid)
                                                                                    .addOnSuccessListener {
                                                                                        Log.d("AddGame", "Game saved to database")

                                                                                        refToUser.child("joined_games").child(gameUid).setValue("Game")
                                                                                        refToUser.child("num_games").setValue(num_games!! + 1)

                                                                                        val intent = Intent(this, Portal:: class.java)
                                                                                        startActivity(intent)
                                                                                    }
                                                                                    .addOnFailureListener {
                                                                                        Log.d("Register", "Failed to save userUid to database $gameUid")
                                                                                    }
                                                                            }
                                                                            .addOnFailureListener {
                                                                                Log.d("Register", "Failed to save time to database $gameUid")
                                                                            }
                                                                    }
                                                                    .addOnFailureListener {
                                                                        Log.d("Register", "Failed to save sport to database $gameUid")
                                                                    }
                                                            }
                                                            .addOnFailureListener {
                                                                Log.d("Register", "Failed to save room to database $gameUid")
                                                            }
                                                    }
                                                    .addOnFailureListener {
                                                        Log.d("Register", "Failed to save players to database $gameUid")
                                                    }
                                            }
                                            .addOnFailureListener {
                                                Log.d("Register", "Failed to save num_players to database $gameUid")
                                            }
                                    }
                                    .addOnFailureListener {
                                        Log.d("Register", "Failed to save location to database $gameUid")
                                    }
                            }
                            .addOnFailureListener {
                                Log.d("Register", "Failed to save gameUid to database $gameUid")
                            }
                    }
                    .addOnFailureListener {
                        Log.d("Register", "Failed to save date to database $gameUid")
                    }
            }
            .addOnFailureListener {
                Log.d("Register", "Failed to save building to database $gameUid")
            }
    }

    //class Game(val gameUid: String, val userUid:String, val sport: String, val location:String, val building:String, val date: String, val time:String, val room:String)
}
