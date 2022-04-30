package com.ksnk.radio.ui.main

import com.ksnk.radio.base.BaseViewModel
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.data.repository.RadioWaveRepository
import javax.inject.Inject

class MainViewModel
@Inject constructor(var radioWaveRepository: RadioWaveRepository) : BaseViewModel() {

    fun createRadioWave() {
       // val radioWave: RadioWave = RadioWave( "name", "image", "url", "fm")
      //  radioWaveRepository.insertRadioWave(radioWave)
    }

    fun createListRadioWave(listRadioWave: List<RadioWave>){
        radioWaveRepository.insertListRadioWave(listRadioWave)
    }

    fun getAll(): List<RadioWave> {
        return radioWaveRepository.getAllRadioWave()
    }

    fun updateRadioWave(radioWave: RadioWave){
        radioWaveRepository.updateRadioWave(radioWave)
    }

    fun getFavoriteRadioWave(): List<RadioWave>{
        return radioWaveRepository.getFavoriteRadioWave()
    }

    fun getRadioWaveForId(id:Int):RadioWave{
        return radioWaveRepository.getMediaForId(id)
    }

}