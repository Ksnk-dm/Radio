package com.ksnk.radio.di.modules

import com.ksnk.radio.services.PlayerService
import com.ksnk.radio.services.TimerService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract fun bindPlayerService(): PlayerService?

    @ContributesAndroidInjector
    abstract fun bindTimerService(): TimerService?
}