package com.ksnk.radio.data.repository

import com.ksnk.radio.data.AppDataBase
import com.ksnk.radio.data.dao.RadioWaveDao
import com.ksnk.radio.data.entity.RadioWave
import javax.inject.Inject

class RadioWaveRepository
@Inject constructor(private val db: AppDataBase) {
    private var radioWaveDao: RadioWaveDao = db.getRadioWaveDao()!!

    fun insertRadioWave(radioWave: RadioWave) = radioWaveDao.insert(radioWave)

    fun insertListRadioWave(listRadioWave: List<RadioWave>) = radioWaveDao.insertAll(listRadioWave)

    fun deleteRadioWave(radioWave: RadioWave) = radioWaveDao.delete(radioWave)

    fun updateRadioWave(radioWave: RadioWave) = radioWaveDao.update(radioWave)

    fun getAllRadioWave(): List<RadioWave> = radioWaveDao.getAll()
}