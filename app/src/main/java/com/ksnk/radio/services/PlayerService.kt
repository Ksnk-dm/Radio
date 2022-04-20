package com.ksnk.radio.services

import android.R
import android.app.Notification
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.IBinder
import androidx.annotation.Nullable
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.ksnk.radio.entity.RadioWave
import com.ksnk.radio.ui.player.PlayerActivity
import com.squareup.picasso.Picasso


class PlayerService : Service() {
    private lateinit var playerBinder: IBinder
    private var mPlayer: ExoPlayer? = null
    private lateinit var playerNotificationManger: PlayerNotificationManager
    private var radioWave: RadioWave? = null
    private var bitMapPoster: Bitmap? = null


    @Nullable
    override fun onBind(p0: Intent?): IBinder {
        return playerBinder
    }

    override fun onCreate() {
        super.onCreate()
        playerBinder = PlayerBinder()
        initPlayer()
       // initNotification()
    }

    private fun initPlayer() {
        mPlayer = ExoPlayer.Builder(this).build()
        mPlayer?.prepare()
        mPlayer?.play()
    }

    override fun onDestroy() {
        mPlayer?.release()
        clearSharedPrefsVar()
    }

    fun getPlayer(): ExoPlayer? {
        return mPlayer
    }

    inner class PlayerBinder : Binder() {
        fun getService(): PlayerService? {
            return this@PlayerService
        }
    }

    public fun initNotification() {
        playerNotificationManger = PlayerNotificationManager.Builder(
            this, 151,
            this.resources.getString(R.string.copy))
            .setChannelNameResourceId(R.string.copy)
            .setChannelImportance(IMPORTANCE_DEFAULT)
            .setMediaDescriptionAdapter(object : MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return radioWave?.name.toString()
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val i = Intent(this@PlayerService, PlayerActivity::class.java)
                    i.putExtra(getString(com.ksnk.radio.R.string.get_serializable_extra), radioWave)
                    return PendingIntent.getActivity(
                        this@PlayerService, 0, i,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return radioWave?.fmFrequency
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: BitmapCallback): Bitmap? {
                    Picasso.get().load(radioWave?.image).into(object : com.squareup.picasso.Target {
                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            bitMapPoster = BitmapFactory.decodeResource(
                                resources,
                                R.drawable.ic_media_play)
                        }

                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
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
                    clearSharedPrefsVar()
                }

                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    if (ongoing) {
                        startForeground(notificationId, notification)
                    } else {
                        stopForeground(false)
                        clearSharedPrefsVar()
                    }
                }
            })
            .build()

        playerNotificationManger.setPlayer(mPlayer)
        playerNotificationManger.setSmallIcon(R.drawable.ic_media_play)
        playerNotificationManger.setUseNextAction(false)
        playerNotificationManger.setUsePreviousAction(true)
        playerNotificationManger.setUseNextActionInCompactView(true)
        playerNotificationManger.setUsePreviousActionInCompactView(false)
        playerNotificationManger.setUseChronometer(true)
    }

    private fun clearSharedPrefsVar() {
        val settings: SharedPreferences =
            getSharedPreferences("base", MODE_PRIVATE)
        val editor = settings.edit()
        editor.putString("name", "")
        editor.apply()
    }

    fun setRadioWave(radioWave: RadioWave) {
        this.radioWave = radioWave
    }
}