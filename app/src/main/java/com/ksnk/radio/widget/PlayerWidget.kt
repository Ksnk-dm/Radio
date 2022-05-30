package com.ksnk.radio.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.ksnk.radio.R
import com.ksnk.radio.services.PlayerService
import com.ksnk.radio.ui.main.MainActivity


/**
 * Implementation of App Widget functionality.
 */
class PlayerWidget : AppWidgetProvider() {
    private val onclick = "onclick"
    private var mExoPlayer: ExoPlayer? = null
    private var mPlayerService: PlayerService? = null


    override fun onReceive(context: Context?, intent: Intent?) {

        super.onReceive(context, intent)

        if ("com.ksnk.radio.ACTION_OPEN".equals(intent?.action.toString())) {
            Log.d("inteeent", intent?.action.toString())
        }

    }



    fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent? {
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(
            context, 0, intent,
                     PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        //    startPlayerService(context!!)
        // There may be multiple widgets active, so update all of them
//        for (appWidgetId in appWidgetIds) {
//            //updateAppWidget(context, appWidgetManager, appWidgetId)
//            val views = RemoteViews(context.packageName, R.layout.player_widget)
//            val intent = Intent(context, PlayerWidget::class.java)
//            intent.action = "com.ksnk.radio.ACTION_OPEN"
//            views.setOnClickPendingIntent(
//                R.id.widgetLinearLayout,
//                getPendingSelfIntent(context, "com.ksnk.radio.ACTION_OPEN")
//            )
//            appWidgetManager.updateAppWidget(appWidgetId, views)

        }


    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created

    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}




