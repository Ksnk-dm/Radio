package com.ksnk.radio.data.dao

import androidx.room.*
import com.ksnk.radio.data.entity.RadioWave

@Dao
interface RadioWaveDao {
    @Insert
    fun insert(vararg radioWave: RadioWave)

    @Delete
    fun delete(radioWave: RadioWave)

    @Update
    fun update(vararg radioWave: RadioWave)

    @Query("SELECT * FROM radiowave")
    fun getAll():List<RadioWave>
}