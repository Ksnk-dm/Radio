package com.ksnk.radio.ui.main

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import com.ksnk.radio.services.PlayerService
import com.ksnk.radio.R
import com.ksnk.radio.entity.RadioWave
import com.ksnk.radio.ui.main.adapter.MainRecyclerViewAdapter

class MainActivity : AppCompatActivity() {
    private var mExoPlayer: ExoPlayer? = null
    private var mPlayerService: PlayerService? = null

    private lateinit var database: DatabaseReference
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: MainRecyclerViewAdapter
    private lateinit var floatingActionButton: FloatingActionButton

    private var items: MutableList<RadioWave> = mutableListOf<RadioWave>()
    lateinit var settings: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPermission()
        initSharedPrefs()
        init()
        checkFabStatus()
        initDb()
        startPlayerService()
    }

    private fun checkFabStatus() {
        floatingActionButton.setOnClickListener {
            if (mExoPlayer?.isPlaying == true) {
                mPlayerService?.getPlayer()?.pause()
                floatingActionButton.setImageResource(R.drawable.ic__18620_play_icon)
            } else {
                mPlayerService?.getPlayer()?.play()
                floatingActionButton.setImageResource(R.drawable.ic_pause_button_svgrepo_com)
            }
        }
    }

    private fun initSharedPrefs() {
        settings = getSharedPreferences(getString(R.string.get_shared_prefs_init), MODE_PRIVATE)
    }

    private fun init() {
        floatingActionButton = findViewById(R.id.floatingActionButtonMain)
        mRecyclerView = findViewById(R.id.main_recycler_view)
    }

    private fun initPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
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
        database =
            FirebaseDatabase.getInstance(getString(R.string.firebase_url))
                .getReference(getString(R.string.firebase_ref))
        // var radioWave: RadioWave = RadioWave("test", "test", "test", "test")
        //  database.child("wave10").setValue(radioWave)
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

        mGridLayoutManager = GridLayoutManager(this, 1)
        mRecyclerView.layoutManager = mGridLayoutManager
        mAdapter = MainRecyclerViewAdapter(items, this, settings)
        mRecyclerView.adapter = mAdapter
    }

    private var myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            mPlayerService = (binder as PlayerService.PlayerBinder).getService()
            mExoPlayer = mPlayerService?.getPlayer()
            if (mExoPlayer?.isPlaying == true) {
                floatingActionButton.visibility = View.VISIBLE
                floatingActionButton.setImageResource(R.drawable.ic_pause_button_svgrepo_com)
            }

        }

        override fun onServiceDisconnected(className: ComponentName) {
            mPlayerService = null
            mExoPlayer = null
            floatingActionButton.visibility = View.GONE
            val editor = settings.edit()
            editor.putString(getString(R.string.get_name_shared_prefs_variable), "")
            editor.apply()
        }
    }

    private fun startPlayerService() {
        val intent = Intent(this, PlayerService::class.java)
        bindService(intent, myConnection, BIND_AUTO_CREATE)
        startService(intent)
    }
}