package com.ksnk.radio.ui.main

import com.ksnk.radio.base.BaseViewModel
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.data.repository.RadioWaveRepository
import javax.inject.Inject

class MainViewModel
@Inject constructor(var radioWaveRepository: RadioWaveRepository):BaseViewModel() {

    fun createRadioWave(){
        val radioWave:RadioWave = RadioWave(1,"name", "image", "url","fm")
        radioWaveRepository.insertRadioWave(radioWave)
    }

}