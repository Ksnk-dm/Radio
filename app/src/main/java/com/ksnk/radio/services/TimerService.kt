package com.ksnk.radio.services

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import com.ksnk.radio.R


class TimerService : Service() {
    var intentFilter ="count_down"
    var bi = Intent(intentFilter)
    var cdt: CountDownTimer? = null
    var minutes: String? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        cdt!!.cancel()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        minutes = intent?.getStringExtra(getString(R.string.serializable_extra_min))
        cdt = object : CountDownTimer(minutes?.toLong()!! * 60000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                bi.putExtra(getString(R.string.serializable_extra_long), millisUntilFinished)
                sendBroadcast(bi)
            }

            override fun onFinish() {
                stopService(Intent(this@TimerService, PlayerService::class.java))
                stopService(Intent(this@TimerService, TimerService::class.java))
            }
        }
        (cdt as CountDownTimer).start()
        return START_STICKY
    }
}