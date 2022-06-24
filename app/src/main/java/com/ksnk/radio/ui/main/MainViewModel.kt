package com.ksnk.radio.ui.main

import com.ksnk.radio.base.BaseViewModel
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.data.entity.Track
import com.ksnk.radio.data.repository.RadioWaveRepository
import com.ksnk.radio.data.repository.TrackRepository
import javax.inject.Inject

class MainViewModel
@Inject constructor(
    var radioWaveRepository: RadioWaveRepository,
    var trackRepository: TrackRepository
) : BaseViewModel() {

    fun insertRadioWave(radioWave: RadioWave) {
        radioWaveRepository.insertRadioWave(radioWave)
    }

    fun insertTrack(track: Track) {
        trackRepository.insertTrack(track)
    }

    fun deleteRadioWave(radioWave: RadioWave) {
        radioWaveRepository.deleteRadioWave(radioWave)
    }

    fun deleteTrack(track: Track) {
        trackRepository.deleteTrack(track)
    }

    fun createListRadioWave(listRadioWave: List<RadioWave>) {
        radioWaveRepository.insertListRadioWave(listRadioWave)
    }

    fun getAllRadioWaves(): List<RadioWave> {
        return radioWaveRepository.getAllRadioWave()
    }

    fun getAllTracks(): List<Track> {
        return trackRepository.getAllTrack()
    }

    fun updateRadioWave(radioWave: RadioWave?) {
        radioWaveRepository.updateRadioWave(radioWave)
    }

    fun getFavoriteRadioWave(): List<RadioWave> {
        return radioWaveRepository.getFavoriteRadioWave()
    }

    fun getRadioWaveForId(id: Int?): RadioWave {
        return radioWaveRepository.getMediaForId(id)
    }

    fun getAllSortAsc(): List<RadioWave> {
        return radioWaveRepository.getAllSortAsc()
    }

    fun getAllSortDesc(): List<RadioWave> {
        return radioWaveRepository.getAllSortDesc()
    }

    fun getCustomSortAsc(): List<RadioWave> {
        return radioWaveRepository.getCustomSortAsc()
    }

    fun getCustomSortDesc(): List<RadioWave> {
        return radioWaveRepository.getCustomSortDesc()
    }

    fun getCustomAll(): List<RadioWave> {
        return radioWaveRepository.getCustomAll()
    }

    fun getPopularAsc(): List<RadioWave> {
        return radioWaveRepository.getPopularSortAsc()
    }

    fun getPopularDesc(): List<RadioWave> {
        return radioWaveRepository.getPopularSortDesc()
    }

    fun deleteAllHistory(){
        return trackRepository.deleteAll()
    }
}