package com.ksnk.radio

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class PlayerService : Service() {
    private lateinit var playerBinder: IBinder
    private var mPlayer: ExoPlayer? = null

    @Nullable
    override fun onBind(p0: Intent?): IBinder {
        return playerBinder
    }

    override fun onCreate() {
        super.onCreate()
        playerBinder = PlayerBinder()
        initPlayer()
        Log.d("service", "start")
    }

    private fun initPlayer() {
        mPlayer = ExoPlayer.Builder(this).build()
        val mediaItem: MediaItem = MediaItem.fromUri("https://online.hitfm.ua/HitFM_HD")
        mPlayer?.setMediaItem(mediaItem)
        mPlayer?.prepare()
        mPlayer?.play()
    }

    override fun onDestroy() {
        mPlayer?.release()
    }

    fun setItems(mediaItem: MediaItem) {
      //  mPlayer?.clearMediaItems()
        mPlayer?.setMediaItem(mediaItem)

    }

    fun getPlayer(): ExoPlayer? {
        return mPlayer
    }

   inner class PlayerBinder : Binder() {
        fun getService(): PlayerService? {
            return this@PlayerService
        }
    }
}