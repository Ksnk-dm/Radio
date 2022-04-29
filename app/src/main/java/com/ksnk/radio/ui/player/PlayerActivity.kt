package com.ksnk.radio.ui.player


import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gauravk.audiovisualizer.visualizer.BarVisualizer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerControlView
import com.ksnk.radio.services.PlayerService
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.RadioWave
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates

class PlayerActivity : AppCompatActivity() {
    private var mExoPlayer: ExoPlayer? = null
    private lateinit var mPlayerView: PlayerControlView
    private lateinit var mVisualizer: BarVisualizer

    private lateinit var mPosterImageView: ImageView
    private lateinit var mNameTextView: TextView
    private lateinit var mFmFrequencyTextView: TextView
    private lateinit var radioWave: RadioWave
    private lateinit var backImageButton: ImageButton

    private lateinit var radioWaveList: List<RadioWave>

    private var audioSessionId by Delegates.notNull<Int>()

    private var mPlayerService: PlayerService? = null
    private lateinit var settings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        init()
        initSharedPrefs()
        saveNameInSharedPrefs()
        setParam()
        startPlayerService()
    }

    private fun setParam() {
        Picasso.get()
            .load(radioWave.image)
            .into(mPosterImageView)
        mNameTextView.text = radioWave.name
        mFmFrequencyTextView.text = radioWave.fmFrequency
    }

    private fun saveNameInSharedPrefs() {
        radioWave =
            (intent.getSerializableExtra(getString(R.string.get_serializable_extra)) as RadioWave?)!!
                //    radioWaveList=intent.getSerializableExtra("list") as ArrayList<RadioWave>
        editor.putString(getString(R.string.get_name_shared_prefs_variable), radioWave.name)
        editor.apply()
    }

    private fun initSharedPrefs() {
        settings =
            getSharedPreferences(getString(R.string.get_shared_prefs_init), MODE_PRIVATE)
        editor = settings.edit()
    }

    private fun init() {
        mPlayerView = findViewById(R.id.playerView)
       // mVisualizer = findViewById(R.id.bar)
        mPosterImageView = findViewById(R.id.imageViewPoster)
        mNameTextView = findViewById(R.id.nameTextView)
        mFmFrequencyTextView = findViewById(R.id.fmFrequencyTextView)
        backImageButton=findViewById(R.id.backImageButton)
        backImageButton.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        mVisualizer.release()
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private var myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            mPlayerService = (binder as PlayerService.PlayerBinder).getService()
            mExoPlayer = mPlayerService?.getPlayer()
            mPlayerView.player = mExoPlayer
            val mediaItem: MediaItem = MediaItem.fromUri(radioWave.url.toString())

            Log.d("fffff",mediaItem.mediaMetadata.artist.toString() )
            mExoPlayer?.clearMediaItems()
            mExoPlayer?.setMediaItem(mediaItem)
            audioSessionId = mExoPlayer!!.audioSessionId
            visualiserCheck()
            mPlayerService?.setRadioWave(radioWave)
            mPlayerService?.initNotification()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mPlayerService = null
            mExoPlayer = null
        }
    }

    private fun visualiserCheck() {
//        try {
//            mVisualizer.setAudioSessionId(audioSessionId)
//        } catch (e: IllegalStateException) {
//            e.stackTrace
//            Log.d("error", e.toString())
//        }
    }

    private fun startPlayerService() {
        val intent = Intent(this, PlayerService::class.java)
        bindService(intent, myConnection, BIND_AUTO_CREATE)
        startService(intent)
    }
}