package com.ksnk.radio.services


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


import android.support.v4.media.session.MediaSessionCompat
import android.util.Log

import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.ui.player.PlayerActivity
import com.squareup.picasso.Picasso
import javax.inject.Inject


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

    fun initNotification() {
        playerNotificationManger = PlayerNotificationManager.Builder(
            this, 151,
            this.resources.getString(R.string.app_name)
        )
            .setChannelNameResourceId(R.string.app_name)
            .setChannelImportance(IMPORTANCE_DEFAULT)
            .setMediaDescriptionAdapter(object : MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return radioWave?.name.toString()
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val i = Intent(this@PlayerService, PlayerActivity::class.java)
                    i.putExtra(getString(R.string.get_serializable_extra), radioWave)
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
                    callback: BitmapCallback
                ): Bitmap? {
                    Picasso.get().load(radioWave?.image).into(object : com.squareup.picasso.Target {
                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            bitMapPoster = BitmapFactory.decodeResource(
                                resources,
                                R.drawable.ic_baseline_music_note_24
                            )
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
            }).build()
        playerNotificationManger.setPlayer(mPlayer)
        playerNotificationManger.setSmallIcon(R.drawable.ic_play_icon)
        playerNotificationManger.setUseNextAction(false)
        playerNotificationManger.setUsePreviousAction(true)
        playerNotificationManger.setUsePlayPauseActions(true)
        playerNotificationManger.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        playerNotificationManger.setUseNextActionInCompactView(true)
        playerNotificationManger.setUsePreviousActionInCompactView(false)
        playerNotificationManger.setUseChronometer(true)


        var mediaSession: MediaSessionCompat = MediaSessionCompat(this, "MediaSessionManager")
        playerNotificationManger.setMediaSessionToken(mediaSession.sessionToken)
        var sessionConnector = MediaSessionConnector(mediaSession)
        sessionConnector.setPlayer(mPlayer)

    }

    private fun clearSharedPrefsVar() {
        val settings: SharedPreferences =
            getSharedPreferences(getString(R.string.get_shared_prefs_init), MODE_PRIVATE)
        val editor = settings.edit()
        editor.putString(getString(R.string.get_name_shared_prefs_variable), "")
        editor.apply()
    }

    fun setRadioWave(radioWave: RadioWave) {
        this.radioWave = radioWave
        var i =Intent("rec")
        i.putExtra("media", radioWave)
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }

    fun getRadioWave(): RadioWave? {
        return radioWave
    }
}