package com.ksnk.radio

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull

class MainActivity : AppCompatActivity() {
    private var mExoPlayer: ExoPlayer? = null
    private var mPlayerService: PlayerService? = null

    private lateinit var database: DatabaseReference
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: MainRecyclerViewAdapter
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var nameInterface: NameInterface

    private var items: MutableList<RadioWave> = mutableListOf<RadioWave>()
    lateinit var settings: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        settings = getSharedPreferences("base", MODE_PRIVATE)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
        floatingActionButton = findViewById(R.id.floatingActionButtonMain)
        floatingActionButton.setOnClickListener {
            if (mExoPlayer?.isPlaying == true) {
                mPlayerService?.getPlayer()?.pause()
                floatingActionButton.setImageResource(R.drawable.ic__18620_play_icon)
            } else {
                mPlayerService?.getPlayer()?.play()
                floatingActionButton.setImageResource(R.drawable.ic_pause_button_svgrepo_com)
            }
        }

        database =
            FirebaseDatabase.getInstance("https://radio-b9295-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("RadioWaves")

        var radioWave: RadioWave = RadioWave("test", "test", "test", "test")

      //  database.child("wave10").setValue(radioWave)


        initDb()
        startPlayerService()

    }

    override fun onResume() {
        super.onResume()
        if (mExoPlayer?.isPlaying == true) {
            floatingActionButton.visibility = View.VISIBLE
            floatingActionButton.setImageResource(R.drawable.ic_pause_button_svgrepo_com)
            mAdapter.notifyDataSetChanged()
        } else {
            floatingActionButton.setImageResource(R.drawable.ic__18620_play_icon)
        }

    }

    private fun initDb() {
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

    private fun initRecycler() {
        mRecyclerView = findViewById(R.id.main_recycler_view)
        mGridLayoutManager = GridLayoutManager(this, 1)
        mRecyclerView.layoutManager = mGridLayoutManager
        mAdapter = MainRecyclerViewAdapter(items, this, settings)
        mRecyclerView.adapter = mAdapter
        nameInterface = mAdapter.getInterface()
    }

    private var myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            Log.d("ServiceConnection", "connected")
            mPlayerService = (binder as PlayerService.PlayerBinder).getService()
            mExoPlayer = mPlayerService?.getPlayer()
            if (mExoPlayer?.isPlaying == true) {
                floatingActionButton.visibility = View.VISIBLE
                floatingActionButton.setImageResource(R.drawable.ic_pause_button_svgrepo_com)
            }

        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.d("ServiceConnection", "disconnected")
            mPlayerService = null
            mExoPlayer = null
            floatingActionButton.visibility = View.GONE
            val editor = settings.edit()
            editor.putString("name", "")

        }
    }

    private fun startPlayerService() {
        val intent = Intent(this, PlayerService::class.java)
        bindService(intent, myConnection, BIND_AUTO_CREATE)
        startService(intent)
    }
}