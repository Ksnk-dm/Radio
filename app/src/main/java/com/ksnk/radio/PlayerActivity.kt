package com.ksnk.radio


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.gauravk.audiovisualizer.visualizer.BarVisualizer
import com.gauravk.audiovisualizer.visualizer.BlastVisualizer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.squareup.picasso.Picasso

class PlayerActivity : AppCompatActivity() {
    private lateinit var mExoPlayer: ExoPlayer
    private lateinit var mPlayerView: PlayerView
    private lateinit var mVisualizer: BarVisualizer

    private lateinit var mPosterImageView: ImageView
    private lateinit var mNameTextView: TextView
    private lateinit var mFmFrequencyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val radioWave: RadioWave? = intent.getSerializableExtra("items") as RadioWave?


        mPlayerView = findViewById(R.id.playerView)
        mVisualizer = findViewById(R.id.bar)
        mPosterImageView = findViewById(R.id.imageViewPoster)
        mNameTextView = findViewById(R.id.nameTextView)
        mFmFrequencyTextView = findViewById(R.id.fmFrequencyTextView)
        mExoPlayer = ExoPlayer.Builder(this).build()


        Picasso.get()
            .load(radioWave?.image)
            .into(mPosterImageView)
        mNameTextView.text=radioWave?.name
        mFmFrequencyTextView.text=radioWave?.fmFrequency


        var audioSessionId = mExoPlayer.audioSessionId

        mVisualizer.setAudioSessionId(audioSessionId)
        mPlayerView.player = mExoPlayer
        var mediaItem: MediaItem = MediaItem.fromUri(radioWave?.url.toString())

        mExoPlayer.setMediaItem(mediaItem)

        mExoPlayer.prepare()
        //mExoPlayer.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        mExoPlayer.clearMediaItems()
    }
}