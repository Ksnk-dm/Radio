package com.ksnk.radio.services


import android.app.Notification
import android.app.NotificationManager.IMPORTANCE_NONE
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.media.session.MediaSessionCompat
import android.widget.RemoteViews
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.ksnk.radio.PlayerWidget
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.ui.main.MainActivity
import com.squareup.picasso.Picasso


class PlayerService() : Service(), Parcelable {

    private lateinit var playerBinder: IBinder
    private var mPlayer: ExoPlayer? = null
    private lateinit var playerNotificationManger: PlayerNotificationManager
    private var radioWave: RadioWave? = null
    private var bitMapPoster: Bitmap? = null
    private val mediaSessionTag = "MediaSessionManager"
    private var trackTitle = ""
    var remoteViews: RemoteViews? = null
    var thisWidget: ComponentName? =null
    var appWidgetManager:AppWidgetManager? = null

    constructor(parcel: Parcel) : this() {
        playerBinder = parcel.readStrongBinder()
        bitMapPoster = parcel.readParcelable(Bitmap::class.java.classLoader)
    }

    @Nullable
    override fun onBind(p0: Intent?): IBinder {
        return playerBinder
    }


    override fun onCreate() {
        super.onCreate()
        playerBinder = PlayerBinder()
        initPlayer()
        appWidgetManager = AppWidgetManager.getInstance(applicationContext)
         remoteViews= RemoteViews(applicationContext.packageName, R.layout.player_widget)
        thisWidget = ComponentName(applicationContext, PlayerWidget::class.java)

    }

    private fun initPlayer() {
        mPlayer = ExoPlayer.Builder(this).setUseLazyPreparation(false)
            .setHandleAudioBecomingNoisy(true)
            .setPauseAtEndOfMediaItems(false).build()
        mPlayer!!.addListener(playerListener)
    }

    override fun onDestroy() {
        mPlayer?.release()
        //  clearSharedPrefsVar()
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
            .setChannelImportance(IMPORTANCE_NONE)
            .setMediaDescriptionAdapter(object : MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return radioWave?.name.toString()
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val i = Intent(this@PlayerService, MainActivity::class.java)
                    i.putExtra(getString(R.string.get_serializable_extra), radioWave)
                    return PendingIntent.getActivity(
                        this@PlayerService, 0, i,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return trackTitle
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
        val mediaSession = MediaSessionCompat(this, mediaSessionTag)
        playerNotificationManger.setMediaSessionToken(mediaSession.sessionToken)
        val sessionConnector = MediaSessionConnector(mediaSession)
        sessionConnector.setPlayer(mPlayer)
    }

    fun setRadioWave(radioWave: RadioWave) {
        this.radioWave = radioWave
        val i = Intent(getString(R.string.intent_filter_notification))
        i.putExtra(getString(R.string.serializable_extra), radioWave)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(i);
    }

    fun getRadioWave(): RadioWave? {
        return radioWave
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStrongBinder(playerBinder)
        parcel.writeParcelable(bitMapPoster, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlayerService> {
        override fun createFromParcel(parcel: Parcel): PlayerService {
            return PlayerService(parcel)
        }

        override fun newArray(size: Int): Array<PlayerService?> {
            return arrayOfNulls(size)
        }
    }

    private var playerListener = object : Player.Listener {
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            trackTitle = mediaMetadata.title.toString()
            remoteViews!!.setTextViewText(R.id.appwidget_text, trackTitle)
            appWidgetManager!!.updateAppWidget(thisWidget, remoteViews)
            }
    }
}