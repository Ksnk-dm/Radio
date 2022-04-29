package com.ksnk.radio.di.modules

import com.ksnk.radio.services.PlayerService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract fun bindPlayerService(): PlayerService?
}