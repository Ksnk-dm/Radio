package com.ksnk.radio.data.dao

import androidx.room.*
import com.ksnk.radio.data.entity.RadioWave

@Dao
interface RadioWaveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg radioWave: RadioWave)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(listRadioWave: List<RadioWave>)

    @Delete
    fun delete(radioWave: RadioWave)

    @Update
    fun update(vararg radioWave: RadioWave)

    @Query("SELECT * FROM radiowave")
    fun getAll(): List<RadioWave>

    @Query("SELECT * FROM radiowave WHERE favorite =1")
    fun getFavoriteRadioWave(): List<RadioWave>

    @Query("SELECT * FROM radiowave WHERE id==:id")
    fun getRadioWaveForId(id: Int?):RadioWave
}