package com.ksnk.radio

import android.R
import android.app.Notification
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom


class PlayerService : Service() {
    private lateinit var playerBinder: IBinder
    private var mPlayer: ExoPlayer? = null
    private lateinit var playerNotificationManger: PlayerNotificationManager
    private var radioWave: RadioWave?=null
    private var bitMapPoster: Bitmap?=null

    fun setRadioWave(radioWave: RadioWave) {
        this.radioWave = radioWave
    }

    @Nullable
    override fun onBind(p0: Intent?): IBinder {
        return playerBinder
    }

    override fun onCreate() {
        super.onCreate()
        playerBinder = PlayerBinder()
        initPlayer()
        Log.d("service", "start")
        playerNotificationManger = PlayerNotificationManager.Builder(
            this, 151,
            this.resources.getString(R.string.copy)
        )
            .setChannelNameResourceId(R.string.copy)
            .setChannelImportance(IMPORTANCE_HIGH)
            .setMediaDescriptionAdapter(object : MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return radioWave?.name.toString()
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    return null
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return radioWave?.fmFrequency + " FM"
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: BitmapCallback
                ): Bitmap? {
                    Picasso.get().load(radioWave?.image).into(object : com.squareup.picasso.Target {
                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            TODO("not implemented")
                        }

                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            // loaded bitmap is here (bitmap)
                            bitMapPoster = bitmap!!
                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                    })



                    return bitMapPoster
                }
            }).setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    stopSelf()
                }

                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    if (ongoing) {
                        // Here Audio is playing, so we need to make sure the service will not get destroyed by calling startForeground.
                        startForeground(notificationId, notification)
                    } else {
                        //Here audio has stopped playing, so we can make notification dismissible on swipe.
                        stopForeground(false)
                    }
                }
            })
            .build()

        playerNotificationManger.setPlayer(mPlayer)
        playerNotificationManger.setSmallIcon(R.drawable.ic_media_play)
        playerNotificationManger.setUseNextAction(false)
        playerNotificationManger.setUsePreviousAction(false)
        playerNotificationManger.setUseNextActionInCompactView(false)
        playerNotificationManger.setUsePreviousActionInCompactView(false)
        playerNotificationManger.setUseChronometer(true)


    }

    private fun initPlayer() {
        mPlayer = ExoPlayer.Builder(this).build()
//        val mediaItem: MediaItem = MediaItem.fromUri("https://online.hitfm.ua/HitFM_HD")
//        mPlayer?.setMediaItem(mediaItem)
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