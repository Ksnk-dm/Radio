package com.ksnk.radio.data.repository

import com.ksnk.radio.data.AppDataBase
import com.ksnk.radio.data.dao.RadioWaveDao
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.data.entity.Track
import javax.inject.Inject

class TrackRepository @Inject constructor(private val db: AppDataBase)  {
    private var trackDao = db.getTrackDao()!!

    fun insertTrack(track: Track) = trackDao.insert(track)

    fun deleteTrack(track: Track) = trackDao.delete(track)

    fun updateTrack(track: Track) = trackDao.update(track)

    fun getAllTrack(): List<Track> = trackDao.getAll()

    fun deleteAll() = trackDao.deleteAll()
}