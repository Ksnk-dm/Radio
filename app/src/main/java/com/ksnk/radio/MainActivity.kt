package com.ksnk.radio

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: MainRecyclerViewAdapter

    private var items: MutableList<RadioWave> = mutableListOf<RadioWave>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)
            ActivityCompat.requestPermissions(this, permissions, 0)
        }


            database =
            FirebaseDatabase.getInstance("https://radio-b9295-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("RadioWaves")

var radioWave: RadioWave = RadioWave("test","test", "test", "test")

//database.child("wave2").setValue(radioWave)


        initDb()

    }

    private fun initDb(){
        val valueEventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(@NonNull @NotNull snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val radioWave: RadioWave? = dataSnapshot.getValue(RadioWave::class.java)
                    items.add(radioWave!!)
                }
                initRecycler()
            }

            override fun onCancelled(@NonNull @NotNull error: DatabaseError) {}
        }
        database.addValueEventListener(valueEventListener)

    }

    private fun initRecycler(){
        mRecyclerView=findViewById(R.id.main_recycler_view)
        mGridLayoutManager = GridLayoutManager(this, 1)
        mRecyclerView.layoutManager = mGridLayoutManager
        mAdapter = MainRecyclerViewAdapter( items, this)
        mRecyclerView.adapter = mAdapter
    }
}