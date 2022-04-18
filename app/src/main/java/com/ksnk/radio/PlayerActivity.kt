package com.ksnk.radio


import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gauravk.audiovisualizer.visualizer.BarVisualizer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.squareup.picasso.Picasso

class PlayerActivity : AppCompatActivity() {
    private var mExoPlayer: ExoPlayer? = null
    private lateinit var mPlayerView: PlayerControlView
    private lateinit var mVisualizer: BarVisualizer

    private lateinit var mPosterImageView: ImageView
    private lateinit var mNameTextView: TextView
    private lateinit var mFmFrequencyTextView: TextView
    private lateinit var radioWave: RadioWave

    private var mPlayerService: PlayerService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        startPlayerService()
       radioWave= (intent.getSerializableExtra("items") as RadioWave?)!!

       // mExoPlayer?.setMediaItem(mediaItem)

        mPlayerView = findViewById(R.id.playerView)
        mVisualizer = findViewById(R.id.bar)
        mPosterImageView = findViewById(R.id.imageViewPoster)
        mNameTextView = findViewById(R.id.nameTextView)
        mFmFrequencyTextView = findViewById(R.id.fmFrequencyTextView)
        //    mExoPlayer = ExoPlayer.Builder(this).build()


        Picasso.get()
            .load(radioWave?.image)
            .into(mPosterImageView)
        mNameTextView.text = radioWave?.name
        mFmFrequencyTextView.text = radioWave?.fmFrequency





//     mExoPlayer?.setMediaItem(mediaItem)
//
//         mExoPlayer?.prepare()
//        mExoPlayer?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        //   mExoPlayer?.clearMediaItems()
    }

    private var myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            Log.d("ServiceConnection", "connected")
            mPlayerService = (binder as PlayerService.PlayerBinder).getService()
            mExoPlayer = mPlayerService?.getPlayer()
            mPlayerView.player = mExoPlayer
            val mediaItem: MediaItem = MediaItem.fromUri(radioWave.url)

            mExoPlayer?.setMediaItem(mediaItem)
            var audioSessionId = mExoPlayer?.audioSessionId
            //   mPlayerService?.setItems(mediaItem)
            mVisualizer.setAudioSessionId(audioSessionId!!)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.d("ServiceConnection", "disconnected")
            mPlayerService = null
            mExoPlayer = null
        }
    }

    private fun startPlayerService() {
        val intent = Intent(this, PlayerService::class.java)
        bindService(intent, myConnection, BIND_AUTO_CREATE)
        startService(intent)
    }
}