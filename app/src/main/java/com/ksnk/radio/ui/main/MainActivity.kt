package com.ksnk.radio.ui.main

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.services.PlayerService
import com.ksnk.radio.ui.favoriteFragment.FavoriteFragment
import com.ksnk.radio.ui.listFragment.ListFragment
import com.ksnk.radio.ui.playerFragment.PlayerFragment
import com.ksnk.radio.ui.listFragment.adapter.ListFragmentRecyclerViewAdapter
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private var mExoPlayer: ExoPlayer? = null
    private var mPlayerService: PlayerService? = null

    private lateinit var database: DatabaseReference
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: ListFragmentRecyclerViewAdapter
    private lateinit var floatingActionButton: FloatingActionButton

    private lateinit var bottomNavView: BottomNavigationView
    private var fragmentView: FragmentContainerView? = null

    private var items: MutableList<RadioWave> = mutableListOf<RadioWave>()
    lateinit var settings: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]


        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initPermission()
        initSharedPrefs()
        init()
        checkFabStatus()
        initDb()
        startPlayerService()
        // userViewModel.createRadioWave()
    }

    private fun checkFabStatus() {
//        floatingActionButton.setOnClickListener {
//            if (mExoPlayer?.isPlaying == true) {
//                mPlayerService?.getPlayer()?.pause()
//                floatingActionButton.setImageResource(R.drawable.ic_play_icon)
//            } else {
//                mPlayerService?.getPlayer()?.play()
//                floatingActionButton.setImageResource(R.drawable.ic_pause_icon)
//            }
//        }
    }

    private fun initSharedPrefs() {
        settings = getSharedPreferences(getString(R.string.get_shared_prefs_init), MODE_PRIVATE)
    }

    private fun init() {
        bottomNavView = findViewById(R.id.bottomNavViewMain)
        fragmentView = findViewById(R.id.fragmentContainerView)
        var fragment: Fragment
        bottomNavView.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.item1 -> {
                    fragment = ListFragment().newInstance()
                    val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainerView, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                R.id.item2 -> {
                    fragment = PlayerFragment().newInstance()
                    val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainerView, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                R.id.item3 -> {
                    fragment = FavoriteFragment().newInstance()
                    val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainerView, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            return@setOnItemSelectedListener true
        }

        // floatingActionButton = findViewById(R.id.floatingActionButtonMain)
        //   mRecyclerView = findViewById(R.id.main_recycler_view)
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
//        if (mExoPlayer?.isPlaying == true) {
//            floatingActionButton.isEnabled = true
//            floatingActionButton.visibility = View.VISIBLE
//            floatingActionButton.setImageResource(R.drawable.ic_pause_icon)
//            mAdapter.notifyDataSetChanged()
//        } else {
//            floatingActionButton.setImageResource(R.drawable.ic_play_icon)
//        }
    }

    private fun initDb() {
        database =
            FirebaseDatabase.getInstance(getString(R.string.firebase_url))
                .getReference(getString(R.string.firebase_ref))
        //   var radioWave: RadioWave = RadioWave("test", "test", "test", "test")
        //   database.child("wave30").setValue(radioWave)
        val valueEventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(@NonNull @NotNull snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val radioWave: RadioWave? = dataSnapshot.getValue(RadioWave::class.java)
                    items.add(radioWave!!)
                }
                viewModel.createListRadioWave(items)
            }

            override fun onCancelled(@NonNull @NotNull error: DatabaseError) {}
        }
        database.addValueEventListener(valueEventListener)

    }

    private fun initRecycler() {
//        mGridLayoutManager = GridLayoutManager(this, 1)
//        mRecyclerView.layoutManager = mGridLayoutManager
//        mAdapter = MainRecyclerViewAdapter(items, this, settings)
//        mRecyclerView.adapter = mAdapter
    }

    private var myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            mPlayerService = (binder as PlayerService.PlayerBinder).getService()
            mExoPlayer = mPlayerService?.getPlayer()
//            if (mExoPlayer?.isPlaying == true) {
//                floatingActionButton.visibility = View.VISIBLE
//                floatingActionButton.isEnabled = true
//                floatingActionButton.setImageResource(R.drawable.ic_pause_icon)
//            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
//            mPlayerService = null
//            mExoPlayer = null
//            floatingActionButton.visibility = View.GONE
//            val editor = settings.edit()
//            editor.putString(getString(R.string.get_name_shared_prefs_variable), "")
//            editor.apply()
        }
    }

    private fun startPlayerService() {
        val intent = Intent(this, PlayerService::class.java)
        bindService(intent, myConnection, BIND_AUTO_CREATE)
        startService(intent)
    }

    override fun onBackPressed() {
       // super.onBackPressed()
    }
}