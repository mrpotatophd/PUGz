package com.example.pugz

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_portal.*
import java.util.*

class GameItemAdapter(context: Context, gameList: MutableList<Portal.GameItem>) : BaseAdapter() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var gameList = gameList
    private var rowListener: ItemRowListener = context as ItemRowListener
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val gameUid: String = gameList.get(position).gameUid as String
        val sport: String = gameList.get(position).sport as String
        val location: String = gameList.get(position).location as String
        val building: String = gameList.get(position).building as String
        val date: String = gameList.get(position).date as String
        val time: String = gameList.get(position).time as String
        val room: String = gameList.get(position).room as String
        val num_players: Int = gameList.get(position).num_players as Int
        val view: View
        val vh: ListRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.row_items, parent, false)
            vh = ListRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }
        vh.sport.text = sport
        vh.num_player.text = num_players.toString()
        vh.date.text = date
        vh.time.text = time
        vh.location.text = location
        vh.building.text = building
        vh.room.text = room
        vh.joinBtn.setOnClickListener {
            rowListener.joinGame(gameUid, num_players)
        }

        return view
    }
    override fun getItem(index: Int): Any {
        return gameList.get(index)
    }
    override fun getItemId(index: Int): Long {
        return index.toLong()
    }
    override fun getCount(): Int {
        return gameList.size
    }
    private class ListRowHolder(row: View) {
        val sport: TextView = row!!.findViewById<TextView>(R.id.sportPortalView) as TextView
        val num_player: TextView = row!!.findViewById<TextView>(R.id.numPlayerPortalView) as TextView
        val date: TextView = row!!.findViewById<TextView>(R.id.datePortalView) as TextView
        val time: TextView = row!!.findViewById<TextView>(R.id.timePortalView) as TextView
        val location: TextView = row!!.findViewById<TextView>(R.id.locationPortalView) as TextView
        val building: TextView = row!!.findViewById<TextView>(R.id.buildingPortalView) as TextView
        val room: TextView = row!!.findViewById<TextView>(R.id.roomPortalView) as TextView
        val joinBtn: Button = row!!.findViewById<Button>(R.id.joinBtn) as Button
    }
}

interface ItemRowListener {
    fun joinGame(itemObjectId: String, num_players: Int)
}

var gameList: MutableList<Portal.GameItem>? = null
lateinit var  adapter: GameItemAdapter
private var listViewItems: ListView? = null
lateinit var mDatabase: DatabaseReference
var num_games: Int? = null
lateinit var refToUser: DatabaseReference

class Portal : AppCompatActivity(), ItemRowListener {

    override fun joinGame(itemObjectId: String, num_players: Int) {
        //adding uid to the game database
        val itemReference = FirebaseDatabase.getInstance().getReference("games").child(itemObjectId)

        val nextRef = itemReference.child("players").child("player" + (num_players + 1))
        val uid = FirebaseAuth.getInstance().currentUser!!.uid.toString()

        nextRef.setValue(uid)
        itemReference.child("num_players").setValue(num_players + 1)

        //adding gameUid to user database
        refToUser.child("joined_games").child("game" + (num_games!! + 1)).setValue(itemObjectId)
        refToUser.child("num_games").setValue(num_games!! + 1)

        val intent = Intent(this, Portal :: class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portal)

        listViewItems = findViewById<View>(R.id.items_list) as ListView

        mDatabase = FirebaseDatabase.getInstance().getReference("games")
        gameList = mutableListOf<GameItem>()
        adapter = GameItemAdapter(this, gameList!!)
        listViewItems!!.setAdapter(adapter)
        mDatabase.orderByKey().addListenerForSingleValueEvent(itemListener)

        refToUser = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid.toString())
        refToUser.orderByKey().addListenerForSingleValueEvent(userListener)

        CreateGame.setOnClickListener {
            val intent = Intent(this, AddGames :: class.java)
            startActivity(intent)
        }

        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    val userListener: ValueEventListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            num_games = p0.child("num_games").value.toString().toInt()
        }

    }

    var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            addDataToList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()

        //check if the collection has any to do items or not
        while (items.hasNext()) {
            //get current item
            val currentItem = items.next()
            val gameItem = GameItem.create()

            //get current data in a map
            val map = currentItem.getValue() as HashMap<String, Any>
            //key will return Firebase ID
            gameItem.gameUid = map.get("gameUid").toString()
            gameItem.userUid = map.get("userUid").toString()
            gameItem.sport = map.get("sport").toString()
            gameItem.location = map.get("location").toString()
            gameItem.building = map.get("building").toString()
            gameItem.date = map.get("date").toString()
            gameItem.time = map.get("time").toString()
            gameItem.num_players = map.get("num_players").toString().toInt()
            gameItem.room = map.get("room").toString()

            Log.w("Adding game to List", gameItem.gameUid)
            gameList!!.add(gameItem);
        }
        //alert adapter that has changed
        adapter.notifyDataSetChanged()
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

    class GameItem {
        companion object Factory {
            fun create(): GameItem = GameItem()
        }

        var gameUid: String? = null
        var userUid:String? = null
        var sport: String? = null
        var location:String? = null
        var building:String? = null
        var date: String? = null
        var time:String? = null
        var num_players: Int? = null
        var room: String? = null
    }
}
