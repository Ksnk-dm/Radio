package com.ksnk.radio.ui.main

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
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
import com.airbnb.lottie.LottieAnimationView
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import com.ksnk.radio.helper.PreferenceHelper
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.services.PlayerService
import com.ksnk.radio.ui.favoriteFragment.FavoriteFragment
import com.ksnk.radio.ui.listFragment.ListFragment
import com.ksnk.radio.ui.settingFragment.SettingFragment
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.NullPointerException
import javax.inject.Inject
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {
    private var mExoPlayer: ExoPlayer? = null
    private var mPlayerService: PlayerService? = null
    private lateinit var mPlayerView: PlayerControlView

    private lateinit var database: DatabaseReference
    private lateinit var bottomNavView: BottomNavigationView
    private var fragmentView: FragmentContainerView? = null

    private var items: MutableList<RadioWave> = mutableListOf<RadioWave>()
    private lateinit var mPosterImageView: CircleImageView
    private lateinit var mNameTextView: TextView
    private lateinit var mFmFrequencyTextView: TextView

    private lateinit var radioWave: RadioWave
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var mVisualizer: CircleLineVisualizer
    private var audioSessionId by Delegates.notNull<Int>()
    private lateinit var motionLayout: MotionLayout
    private lateinit var favoriteImageButton: ImageButton
    private lateinit var playImageView: ImageView
    private lateinit var animNetLottieAnimationView: LottieAnimationView

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var viewModel: MainViewModel

    @Inject
    lateinit var preferencesHelper: PreferenceHelper
    lateinit var titleTextView: TextView
    lateinit var posterImageView: ImageView
    private lateinit var fragment: Fragment
    private var firstStartStatus: Boolean = true

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val radioWave: RadioWave = intent.getSerializableExtra("media") as RadioWave
            titleTextView.text = radioWave.name
            Picasso.get()
                .load(radioWave.image)
                .into(posterImageView)
            preferencesHelper.setIdPlayMedia(radioWave.id!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        setMediaInfoInMiniPlayer()
    }


    private fun checkFirstStartStatus() {
        firstStartStatus = preferencesHelper.getFirstStart()
        if (firstStartStatus) {
            initDb()
        } else {
            startPlayerService()
        }
    }

    private fun initBroadcastManager() {
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("rec"))
    }

    private fun favoriteStatusFalse() {
        radioWave.favorite = true
        viewModel.updateRadioWave(radioWave)
        favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_24)
        lottieAnimationView.visibility = View.VISIBLE
        lottieAnimationView.playAnimation()
    }

    private fun favoriteStatusTrue() {
        radioWave.favorite = false
        viewModel.updateRadioWave(radioWave)
        favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
    }

    private fun initRadioWaveFromService() {
        radioWave = mPlayerService?.getRadioWave()!!
        if (radioWave.favorite == false) {
            favoriteStatusFalse()
        } else {
            favoriteStatusTrue()
        }
    }

    private var lottieAnimationListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {

        }

        override fun onAnimationEnd(animation: Animator) {
            lottieAnimationView.visibility = View.INVISIBLE
        }

        override fun onAnimationCancel(animation: Animator) {
        }

        override fun onAnimationRepeat(animation: Animator) {
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        favoriteImageButton.setOnClickListener {
            initRadioWaveFromService()
        }
        lottieAnimationView.addAnimatorListener(lottieAnimationListener)
        bottomNavView.setOnItemSelectedListener(bottomNavViewOnItemSelectListener)
        playImageView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                checkStatusClickPlayInMiniPlayer()
            }
            false
        }
    }

    private fun checkStatusClickPlayInMiniPlayer() {
        if (mExoPlayer!!.isPlaying) {
            mExoPlayer!!.pause()
        } else {
            mExoPlayer!!.play()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initPermission()
        checkFirstStartStatus()
        initBroadcastManager()
        setMediaInfoInMiniPlayer()
        setListeners()
    }

    private fun setMediaInfoInMiniPlayer() {
        val id: Int = preferencesHelper.getIdPlayMedia()
        val radioWave: RadioWave = viewModel.getRadioWaveForId(id)
        if (radioWave != null) {
            titleTextView.text = radioWave.name
            Picasso.get()
                .load(radioWave.image)
                .into(posterImageView)
            preferencesHelper.setIdPlayMedia(radioWave.id)
        }
    }


    private fun createListFragment() {
        fragment = ListFragment().newInstance()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun createSettingFragment() {
        fragment = SettingFragment().newInstance()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun createFavFragment() {
        fragment = FavoriteFragment().newInstance()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private var bottomNavViewOnItemSelectListener = NavigationBarView.OnItemSelectedListener {
        when (it.itemId) {
            R.id.listFragmentItem -> {
                createListFragment()
            }
            R.id.favoriteFragmentItem -> {
                createFavFragment()
            }
            R.id.settingFragmentItem -> {
                createSettingFragment()
            }
        }
        return@OnItemSelectedListener true
    }

    private val transitionListener = object : MotionLayout.TransitionListener {
        override fun onTransitionStarted(p0: MotionLayout?, startId: Int, endId: Int) {}

        override fun onTransitionChange(
            p0: MotionLayout?,
            startId: Int,
            endId: Int,
            progress: Float
        ) {
        }

        override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
            setParamMediaIfScrollMiniPlayer()
            checkButtonPlayInMiniPlayer()
            if (mPlayerService == null) return
            setMediaSessionAndVisual()
        }

        override fun onTransitionTrigger(
            p0: MotionLayout?,
            triggerId: Int,
            positive: Boolean,
            progress: Float
        ) {
        }

    }

    private fun checkButtonPlayInMiniPlayer() {
        if (mPlayerService?.getRadioWave()?.favorite == true) {
            favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }

    private fun setParamMediaIfScrollMiniPlayer() {
        val id: Int = preferencesHelper.getIdPlayMedia()
        radioWave = viewModel.getRadioWaveForId(id)
        Picasso.get()
            .load(mPlayerService?.getRadioWave()?.image)
            .resize(150, 150)
            .into(mPosterImageView)
        mPlayerView.player = mPlayerService?.getPlayer()
        mNameTextView.text = mPlayerService?.getRadioWave()?.name
        mFmFrequencyTextView.text = mPlayerService?.getRadioWave()?.fmFrequency
    }

    private fun setMediaSessionAndVisual() {
        audioSessionId = mExoPlayer!!.audioSessionId
        try {
            mVisualizer.setAudioSessionId(audioSessionId)
        } catch (e: Exception) {
            mVisualizer.release()
            mVisualizer.setAudioSessionId(audioSessionId)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        mPlayerView = findViewById(R.id.playerView)
        bottomNavView = findViewById(R.id.bottomNavViewMain)
        fragmentView = findViewById(R.id.fragmentContainerView)
        mPosterImageView = findViewById(R.id.imageViewPoster)
        mNameTextView = findViewById(R.id.nameTextView)
        mFmFrequencyTextView = findViewById(R.id.fmFrequencyTextView)
        mVisualizer = findViewById(R.id.blob)
        lottieAnimationView = findViewById(R.id.favAnimationView)
        favoriteImageButton = findViewById(R.id.favoriteImageButton)
        motionLayout = findViewById(R.id.motion_layout)
        titleTextView = findViewById(R.id.title_textView)
        motionLayout.addTransitionListener(transitionListener)
        posterImageView = findViewById(R.id.main_imageView)
        playImageView = findViewById(R.id.play_imageView)
        animNetLottieAnimationView = findViewById(R.id.netAnim)

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


    fun initDb() {
        database =
            FirebaseDatabase.getInstance(getString(R.string.firebase_url))
                .getReference(getString(R.string.firebase_ref))

        val valueEventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(@NonNull @NotNull snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val radioWave: RadioWave? = dataSnapshot.getValue(RadioWave::class.java)
                    items.add(radioWave!!)
                }
                viewModel.createListRadioWave(items)
                startPlayerService()
                createListFragment()
                preferencesHelper.setFirstStart(false)
                preferencesHelper.setIdPlayMedia(items[2].id)
            }

            override fun onCancelled(@NonNull @NotNull error: DatabaseError) {}
        }
        database.addValueEventListener(valueEventListener)

    }


    fun updateDb() {
        database =
            FirebaseDatabase.getInstance(getString(R.string.firebase_url))
                .getReference(getString(R.string.firebase_ref))
        val valueEventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(@NonNull @NotNull snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val radioWave: RadioWave? = dataSnapshot.getValue(RadioWave::class.java)
                    items.add(radioWave!!)
                }
                viewModel.createListRadioWave(items)
                //    startPlayerService()
                //     createListFragment()
                preferencesHelper.setFirstStart(false)

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
            val id = preferencesHelper.getIdPlayMedia()
            val url: String?

            try {
                url = viewModel.getRadioWaveForId(id).url
                val mediaItem: MediaItem =
                    MediaItem.fromUri(url!!)
                mPlayerService?.getPlayer()?.setMediaItem(mediaItem)
                mPlayerService?.setRadioWave(viewModel.getRadioWaveForId(id))
            } catch (e: NullPointerException) {
                e.stackTrace
            }
            isPlayingMedia(mExoPlayer!!.isPlaying)
            mPlayerService?.getPlayer()?.addListener(playerListener)


        }


        override fun onServiceDisconnected(className: ComponentName) {
            mPlayerService = null
            mExoPlayer = null
        }
    }

    private var playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            isPlayingMedia(isPlaying)
        }

        override fun onPlayerError(error: PlaybackException) {
            when (error.errorCode) {
                ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> {
                    animNetLottieAnimationView.visibility = View.VISIBLE
                }
            }
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
            animNetLottieAnimationView.visibility = View.INVISIBLE
        }
    }


    private fun isPlayingMedia(isPlaying: Boolean) {
        if (isPlaying) {
            playImageView.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            playImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24)
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