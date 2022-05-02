package com.ksnk.radio.helper

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.ksnk.radio.data.dao.RadioWaveDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceHelper @Inject constructor(private var sharedPreferences: SharedPreferences) {
    fun getIdPlayMedia(): Int {
        return sharedPreferences.getInt("id", 1)
    }

    fun setIdPlayMedia(id: Int?) {
        if (id != null) {
            sharedPreferences.edit().putInt("id", id).apply()
        }
    }

    fun getFirstStart(): Boolean {
        return sharedPreferences.getBoolean("firstStart", true)
    }

    @SuppressLint("CommitPrefEdits")
    fun setFirstStart(status: Boolean) {
        sharedPreferences.edit().putBoolean("firstStart", status).apply()
    }
}