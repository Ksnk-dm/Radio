package com.ksnk.radio.services

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import javax.inject.Inject


class TimerService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private val TAG = "BroadcastService"

    val COUNTDOWN_BR = "com.ksnk.radio.countdown_br"
    var bi = Intent(COUNTDOWN_BR)

    var cdt: CountDownTimer? = null


    override fun onCreate() {
        super.onCreate()
        // playerService = PlayerService().PlayerBinder().getService()!!
        Log.i(TAG, "Starting timer...")
        cdt = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000)
                bi.putExtra("countdown", millisUntilFinished)
                sendBroadcast(bi)
            }

            override fun onFinish() {
                Log.i(TAG, "Timer finished")
                stopService(Intent(this@TimerService, PlayerService::class.java))
                stopService(Intent(this@TimerService, TimerService::class.java))
            }
        }
        (cdt as CountDownTimer).start()
    }

    override fun onDestroy() {
        cdt!!.cancel()
        Log.i(TAG, "Timer cancelled")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}