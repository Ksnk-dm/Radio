package com.ksnk.radio.ui.main

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.gauravk.audiovisualizer.visualizer.BarVisualizer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import com.ksnk.radio.PreferenceHelper
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.listeners.ChangeInformationListener
import com.ksnk.radio.services.PlayerService
import com.ksnk.radio.ui.favoriteFragment.FavoriteFragment
import com.ksnk.radio.ui.listFragment.ListFragment
import com.ksnk.radio.ui.listFragment.adapter.ListFragmentRecyclerViewAdapter
import com.ksnk.radio.ui.playerFragment.PlayerFragment
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import javax.inject.Inject
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), ChangeInformationListener {
    private var mExoPlayer: ExoPlayer? = null
    private var mPlayerService: PlayerService? = null
    private lateinit var mPlayerView: PlayerControlView

    private lateinit var database: DatabaseReference
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mAdapter: ListFragmentRecyclerViewAdapter
    private lateinit var floatingActionButton: FloatingActionButton

    private lateinit var bottomNavView: BottomNavigationView
    private var fragmentView: FragmentContainerView? = null

    private var items: MutableList<RadioWave> = mutableListOf<RadioWave>()
    lateinit var settings: SharedPreferences
    private lateinit var mPosterImageView: ImageView
    private lateinit var mNameTextView: TextView
    private lateinit var mFmFrequencyTextView: TextView

    private lateinit var radioWave: RadioWave
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var mVisualizer: BarVisualizer
    private var audioSessionId by Delegates.notNull<Int>()
    private lateinit var motionLayout: MotionLayout
    private lateinit var favoriteImageButton: ImageButton
    private lateinit var playImageView: ImageView

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var preferencesHelper: PreferenceHelper

    lateinit var titleTextView: TextView
    lateinit var posterImageView: ImageView
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
var radioWave:RadioWave = intent.getSerializableExtra("media") as RadioWave
            Log.d("radiowave", radioWave.toString())
            titleTextView.text=radioWave.name

            Picasso.get()
                .load(radioWave.image)
                .into(posterImageView)
            preferencesHelper.setIdPlayMedia(radioWave.id!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]


        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("rec"))

        initPermission()
        initSharedPrefs()
        init()
        initDb()
        startPlayerService()
        // userViewModel.createRadioWave()
        mPlayerView = findViewById(R.id.playerView)
var id:Int=preferencesHelper.getIdPlayMedia()
        radioWave=viewModel.getRadioWaveForId(id)
        titleTextView.text=radioWave.name

        Picasso.get()
            .load(radioWave.image)
            .into(posterImageView)

        mExoPlayer?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if(isPlaying){
                    playImageView.setImageResource(R.drawable.ic_baseline_pause_24)
                } else {
                    playImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
        })
    }


    private fun initSharedPrefs() {
        settings = getSharedPreferences(getString(R.string.get_shared_prefs_init), MODE_PRIVATE)
    }

    @SuppressLint("ClickableViewAccessibility")
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
        mPosterImageView = findViewById(R.id.imageViewPoster)
        mNameTextView = findViewById(R.id.nameTextView)
        mFmFrequencyTextView = findViewById(R.id.fmFrequencyTextView)
        mVisualizer = findViewById(R.id.bar)
        lottieAnimationView = findViewById(R.id.favAnimationView)
        favoriteImageButton = findViewById(R.id.favoriteImageButton)
        motionLayout = findViewById(R.id.motion_layout)
        var id = viewModel.getRadioWaveForId(preferencesHelper.getIdPlayMedia())
        titleTextView = findViewById(R.id.title_textView)
        titleTextView.text = id.name
        val transitionListener = object : MotionLayout.TransitionListener {

            override fun onTransitionStarted(p0: MotionLayout?, startId: Int, endId: Int) {

            }

            override fun onTransitionChange(
                p0: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                //nothing to do
            }

            override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
                Log.d("transss", "complete")
                Picasso.get()
                    .load(mPlayerService?.getRadioWave()?.image)
                    .into(mPosterImageView)
                mPlayerView.player = mPlayerService?.getPlayer()
                mNameTextView.text = mPlayerService?.getRadioWave()?.name
                mFmFrequencyTextView.text = mPlayerService?.getRadioWave()?.fmFrequency
                if (mPlayerService?.getRadioWave()?.favorite == true) {
                    favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_24)
                } else {
                    favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }
                if (mPlayerService == null) return
                audioSessionId = mExoPlayer!!.audioSessionId


                try {
                    mVisualizer.setAudioSessionId(audioSessionId)
                } catch (e: Exception) {
                    mVisualizer.release()
                    mVisualizer.setAudioSessionId(audioSessionId)
                }

                favoriteImageButton.setOnClickListener {
                    radioWave = mPlayerService?.getRadioWave()!!
                    if (radioWave.favorite == false) {
                        radioWave.favorite = true
                        viewModel.updateRadioWave(radioWave)
                        favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_24)
                        lottieAnimationView.visibility = View.VISIBLE
                        lottieAnimationView.playAnimation()
                    } else {
                        radioWave.favorite = false
                        viewModel.updateRadioWave(radioWave)
                        favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    }
                    lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {
                            lottieAnimationView.visibility = View.INVISIBLE
                        }

                        override fun onAnimationCancel(animation: Animator) {
                        }

                        override fun onAnimationRepeat(animation: Animator) {
                        }
                    })
                }

            }

            override fun onTransitionTrigger(
                p0: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {

            }

        }
        motionLayout.addTransitionListener(transitionListener)
        posterImageView=findViewById(R.id.main_imageView)
        playImageView=findViewById(R.id.play_imageView)

        playImageView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if(mExoPlayer!!.isPlaying){
                    mExoPlayer!!.pause()
                } else {
                    mExoPlayer!!.play()
                }
            }
            false


}


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

    private var myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            mPlayerService = (binder as PlayerService.PlayerBinder).getService()
            mExoPlayer = mPlayerService?.getPlayer()
            mPlayerService?.getRadioWave()?.id?.let { preferencesHelper.setIdPlayMedia(it) }
            if(mExoPlayer!!.isPlaying){
                playImageView.setImageResource(R.drawable.ic_baseline_pause_24)
            } else {
                playImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
            mPlayerService?.getPlayer()?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if(isPlaying){
                        playImageView.setImageResource(R.drawable.ic_baseline_pause_24)
                    } else {
                        playImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }
            })


        }

        override fun onServiceDisconnected(className: ComponentName) {
            mPlayerService = null
            mExoPlayer = null
        }
    }

    private fun startPlayerService() {
        val intent = Intent(this, PlayerService::class.java)
        bindService(intent, myConnection, BIND_AUTO_CREATE)
        startService(intent)
        Log.d("startserv", "startServ")

    }

    override fun onBackPressed() {
        // super.onBackPressed()
    }

    override fun changeInform(title: String) {
        titleTextView.text = title
    }

}