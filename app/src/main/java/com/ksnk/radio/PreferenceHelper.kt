package com.ksnk.radio

import android.content.SharedPreferences
import com.ksnk.radio.data.dao.RadioWaveDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceHelper @Inject constructor(sharedPreferences: SharedPreferences) {
    private var sharedPreferences = sharedPreferences
    fun getIdPlayMedia(): Int {
        return sharedPreferences.getInt("id", 1)
    }

    fun setIdPlayMedia(id: Int) {
        sharedPreferences.edit().putInt("id", id).apply()
    }
}